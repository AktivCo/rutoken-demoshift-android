#!/usr/bin/env python
# -*- coding: utf-8 -*-
from __future__ import print_function
from argparse import ArgumentParser
from collections import defaultdict
from glob import glob
from itertools import permutations
from os import path, remove, makedirs, listdir, chmod
from os import name as os_name
from os.path import dirname, realpath, join
from re import compile, escape
from shutil import rmtree
from threading import Thread, Lock
from zipfile import ZipFile

try:  # python3
    from urllib.parse import urlencode
    from urllib.request import urlopen, Request
    from urllib.error import HTTPError
    from queue import Queue
except ImportError:  # python2
    from urllib2 import urlopen, HTTPError, Request
    from urllib import urlencode
    from Queue import Queue

if os_name != "nt":
    from os import symlink

URL_WITH_BINS = "https://builds.aktivco.ru/new/binary_deps"
DEFAULT_EXTERNAL_CONFIG = join(dirname(dirname(__file__)), "external.config")
print_lock = Lock()
MAX_PATH_WIN = 248


def printf(*args, **kwargs):
    with print_lock:
        print(*args, **kwargs)


class Worker(Thread):
    def __init__(self, tasks):
        Thread.__init__(self)
        self.tasks = tasks
        self.daemon = True
        self.start()

    def run(self):
        while True:
            func, args, kargs = self.tasks.get()
            try:
                func(*args, **kargs)
            except Exception as e:
                printf(e)
            self.tasks.task_done()


class Threadpool:
    def __init__(self, num_threads):
        self.tasks = Queue(num_threads)
        for count in range(num_threads):
            Worker(self.tasks)

    def add_task(self, func, *args, **kargs):
        self.tasks.put((func, args, kargs))

    def wait_completion(self):
        self.tasks.join()


def is_symlink(info=None):
    # Return true if the object ZipInfo passed in represents a symlink
    return (info.external_attr >> 16) & 0o770000 == 0o120000


def get_archive_list(package_url=None, dep_name=None):
    params = "?" + urlencode({"action": "list", "path": dep_name})
    try:
        request = Request(package_url + params)
        response = urlopen(request)
    except HTTPError as e:
        printf("[-] : list {0} from {1} failed (return code {2})".format(dep_name, package_url, e.code))
        return None

    buf = response.read().decode()
    content = buf.split("\0")
    return list(filter(None, content))


def split_platform(platform):
    platform_list = platform.split("-")

    return [platform_part.split('+') for platform_part in platform_list]


def make_extendable_pattern(parts):
    pattern = ""
    for part in parts:
        if pattern != "":
            pattern = "{0}-".format(pattern)

        part_pattern = ""
        for permutation in permutations(part):
            permutation_pattern = ""
            for part_variant in permutation:
                permutation_pattern = "{0}[^-]*((?<=[\+-])|(?<=^)){1}(\+[^-]*|(?=-)|(?=$))".format(permutation_pattern,
                                                                                                   escape(part_variant))
            if part_pattern != "":
                part_pattern = "{0}|{1}".format(part_pattern, permutation_pattern)
            else:
                part_pattern = "{0}".format(permutation_pattern)
        pattern = "{0}({1})".format(pattern, part_pattern)

    return pattern


def get_best_dependency(dep_matches, platform_runtime):
    deps = defaultdict(list)
    for match in dep_matches:
        groups_len = len(match.groups())

        runtime_version = match.group(7) if groups_len == 7 else match.group(5)
        deps[runtime_version].append(match)

    valid_runtimes = list(filter(lambda runtime: runtime <= platform_runtime,
                                 sorted(deps.keys(), reverse=True)))
    if len(valid_runtimes) == 0:
        return None

    return deps[valid_runtimes[0]][0].group(0)


def search_pattern(dep_name=None, archive_list=None, platform=None, target=None):
    if not platform:
        return ["*", ".*"]

    if archive_list == ["common"]:
        return [["{0}/common/**".format(dep_name)], "common"]

    platform_parts = split_platform(platform)

    mask_clang = "clang(\d+)v(\d+)"
    mask_msvc = "msvc(\d+)"

    for i in range(len(platform_parts), 0, -1):
        platform_parts_slice = platform_parts[:i]
        clang_match = compile(mask_clang).search(platform_parts[i - 1][0])
        msvc_match = compile(mask_msvc).search(platform_parts[i - 1][0])

        if target:
            pattern = "{0}-.*{1}".format(make_extendable_pattern(platform_parts_slice), target)
            matches = list(filter(compile(pattern).search, archive_list))
            if len(matches) == 0:
                pattern = make_extendable_pattern(platform_parts_slice)
        else:
            pattern = make_extendable_pattern(platform_parts_slice)

        matches = list(filter(compile(pattern).search, archive_list))

        if len(matches) > 0:
            return [["{0}/{1}/**".format(dep_name, match) for match in matches], pattern]
        elif (clang_match is not None) or (msvc_match is not None):
            pre_mask = make_extendable_pattern(platform_parts[:i - 1])
            if target:
                clang_full_mask = "{0}-{1}-.*-{2}".format(pre_mask, mask_clang, target)
                msvc_full_mask = "{0}-{1}-.*-{2}".format(pre_mask, mask_msvc, target)
            else:
                clang_full_mask = "{0}-{1}-.*".format(pre_mask, mask_clang)
                msvc_full_mask = "{0}-{1}-.*".format(pre_mask, mask_msvc)
            clang_matches = list(filter(None, map(compile(clang_full_mask).search, archive_list)))
            msvc_matches = list(filter(None, map(compile(msvc_full_mask).search, archive_list)))

            platform_runtime = clang_match.group(2) if clang_match is not None else msvc_match.group(1)

            best_dep_pattern = get_best_dependency((clang_matches + msvc_matches), platform_runtime)
            if best_dep_pattern is not None:
                return [["{0}/{1}/**".format(dep_name, best_dep_pattern)], best_dep_pattern]

    return None


def download_binary(package_url=None, package=None, path_to_package=None, pattern=None):
    if pattern == "*":
        params = ""
    else:
        params = "?" + urlencode({"action": "get", "eglob": pattern})

    try:
        request = urlopen(package_url + params)
        block_size = 5 * 1024
        with open(path_to_package, "wb") as archive:
            while True:
                chunk = request.read(block_size)
                if not chunk:
                    break
                archive.write(chunk)
    except HTTPError as e:
        printf("[-] : {0} download {1} failed (return code {2})".format(package, package_url, e.code))
        return False

    printf("[+] : {0} download successful".format(package))
    return True


def set_attributes(file_info, path_to_file):
    permissions = (file_info.external_attr >> 16) & 0o777
    if permissions:
        chmod(path_to_file, permissions)


def create_symlink(path_to_file):
    with open(path_to_file, "r") as symlink_file:
        symlink_name = symlink_file.read()
    remove(path_to_file)
    symlink(symlink_name, path_to_file)


def unzip(path_to_package=None, path_to_extract=None):
    with ZipFile(path_to_package, "r") as archive:
        for file_info in archive.infolist():
            path_to_file = path.join(path_to_extract, file_info.filename)

            if os_name == "nt" and len(path_to_file) >= MAX_PATH_WIN:
                continue

            archive.extract(file_info.filename, path_to_extract)

            if os_name != "nt":
                set_attributes(file_info, path_to_file)
                if is_symlink(file_info):
                    create_symlink(path_to_file)


def clean_folder_with_deps(path_to_package=None, external_out=None, dep_name=None):
    path_to_dep = path.join(external_out, dep_name)
    if not path.exists(path_to_dep):
        return None

    with ZipFile(path_to_package, "r") as archive:
        folder_in_archive = set([i.split("/")[1] for i in archive.namelist() if i.endswith('/')])
        folder_in_archive = set(filter(None, list(folder_in_archive)))
    local_folder = [i for i in listdir(path_to_dep)]

    for folder in local_folder:
        if folder in folder_in_archive:
            rmtree(path.join(path_to_dep, folder))


def add_version_file(external_out=None, dep_name=None, revision=None):
    version_files_list = glob(path.join(external_out, dep_name, "*", "*_version"))
    for platform in glob(path.join(external_out, dep_name, "*")):
        if path.isdir(platform):
            path_to_version = path.join(platform, "{0}_version".format(dep_name))
            with open(path_to_version, "w") as version_file:
                version_file.write(revision)


def post_download_steps(path_to_package=None, external_out=None, dep_name=None, revision=None):
    clean_folder_with_deps(path_to_package, external_out, dep_name)
    unzip(path_to_package, external_out)
    remove(path_to_package)
    add_version_file(external_out, dep_name, revision)


def exist_platform_in_dep(force, dep_name=None, revision=None, platform=None, local_dep=None):
    if platform not in local_dep:
        return False

    if force:
        return False

    if local_dep[platform] == revision:
        printf("[*] : {0} {1} already exist for {2}".format(dep_name, revision, platform))
        return True
    return False


def worker(external_out, platform, target, force, dep_name=None, revision=None, deps_tree=None):
    package = "{0}-{1}.zip".format(dep_name, revision)
    package_url = "{0}/{1}/{2}".format(URL_WITH_BINS, dep_name, package)
    path_to_package = path.join(external_out, package)

    archive_list = get_archive_list(package_url, dep_name)
    if not archive_list:
        return None

    pattern = search_pattern(dep_name, archive_list, platform, target)

    if not pattern:
        return None

    if dep_name in deps_tree and pattern[0] != "*":
        platforms_match_local = set(filter(compile(pattern[1]).search, deps_tree[dep_name].keys()))
        platforms_match_server = set(filter(compile(pattern[1]).search, archive_list))
        platform_match_all = platforms_match_server - platforms_match_local
        for platform in platform_match_all:
            if exist_platform_in_dep(force, dep_name, revision, platform, deps_tree[dep_name]):
                continue

            for remote_path in pattern[0]:
                if download_binary(package_url, package, path_to_package, remote_path):
                    post_download_steps(path_to_package, external_out, dep_name, revision)

        platform_not_match = set(deps_tree[dep_name].keys()) - platforms_match_local
        for platform in platform_not_match:
            local_revision = deps_tree[dep_name][platform]
            if local_revision != revision:
                printf("[!] : WARNING: {0} conflict version for {1}: {2} and {3}".format(dep_name, platform,
                                                                                         local_revision,
                                                                                         revision))
    else:
        for remote_path in pattern[0]:
            if download_binary(package_url, package, path_to_package, remote_path):
                post_download_steps(path_to_package, external_out, dep_name, revision)


def update(external_out, force, dep_name=None, revision=None, deps_tree=None):
    if dep_name not in deps_tree:
        return None

    package = "{0}-{1}.zip".format(dep_name, revision)
    package_url = "{0}/{1}/{2}".format(URL_WITH_BINS, dep_name, package)
    path_to_package = path.join(external_out, package)

    platforms = deps_tree[dep_name].keys()
    for platform in platforms:
        local_revision = deps_tree[dep_name][platform]
        if local_revision == revision and not force:
            printf("[*] : {0} already update for {1}".format(dep_name, platform))
        else:
            archive_list = get_archive_list(package_url, dep_name)
            if not archive_list:
                return None
            pattern = "{0}/{1}/**".format(dep_name, platform)
            if download_binary(package_url, package, path_to_package, pattern):
                post_download_steps(path_to_package, external_out, dep_name, revision)
                printf("[+] : {0} updated for {1}".format(dep_name, platform))


def check_update(dep_name=None, revision=None, deps_tree=None):
    if dep_name not in deps_tree:
        return True

    is_updated = True
    platforms = deps_tree[dep_name].keys()
    for platform in platforms:
        local_revision = deps_tree[dep_name][platform]
        if local_revision != revision:
            printf("[!] : WARNING: {0} conflict version for {1}: {2} and {3}".format(dep_name, platform,
                                                                                     local_revision,
                                                                                     revision))
            is_updated = False
    return is_updated


def get_deps_version(external_out):
    deps_tree = {}
    version_files_list = glob(path.join(external_out, "*", "*", "*_version"))
    for path_to_version_file in version_files_list:
        with open(path_to_version_file, "r") as version_file:
            dep_name = path.basename(path_to_version_file).split("_")[0]
            platform = path.basename(path.split(path_to_version_file)[0])
            version = version_file.read().rstrip()

            if dep_name not in deps_tree:
                deps_tree[dep_name] = {}

            deps_tree[dep_name][platform] = version
    return deps_tree


def check_arguments(args):
    if not args.platform and args.target:
        printf("Error, set platform argument!")
        exit(1)

    if args.check and args.update:
        printf("Error, only one operation is allowed: check or update")
        exit(1)


def get_arguments():
    parser = ArgumentParser(description="Script for download binary dependencies, requires external.config file")
    parser.add_argument("-p", "--platform", help="Platform for binary dependencies", type=str, required=False)
    parser.add_argument("-e", "--external-config", help="Path to external.config", type=str, required=False,
                        default=DEFAULT_EXTERNAL_CONFIG)
    parser.add_argument("-t", "--target", help="Target for binary dependencies: release/debug", type=str,
                        required=False)
    parser.add_argument("-u", "--update", help="Updating all binary dependencies", action="store_true", required=False)
    parser.add_argument("-c", "--check", help="Checking for available updates", action="store_true", required=False)
    parser.add_argument("-j", "--job", help="Setting maximum of threads for download dependencies", type=int,
                        required=False)
    parser.add_argument("-f", "--force", help="Conflict version and exists packages will be replaced",
                        action="store_true",
                        required=False)

    return parser.parse_args()


def main():
    workspace = dirname(dirname(realpath(__file__)))
    external_out = join(workspace, "external")

    args = get_arguments()

    check_arguments(args)

    binary_dict = {}
    with open(args.external_config, "r") as config:
        for line in config.read().splitlines():
            dep_name, revision = line.split(" ")[0], line.split(" ")[1]
            binary_dict[dep_name] = revision

    if path.exists(external_out):
        deps_tree = get_deps_version(external_out)
    else:
        deps_tree = {}
        makedirs(external_out)

    if args.check:
        need_to_update = False
        for dep_name, revision in binary_dict.items():
            if not check_update(dep_name, revision, deps_tree):
                need_to_update = True
        if need_to_update:
            exit(2)
        else:
            exit(0)

    if args.job:
        count_thread = args.job
    else:
        count_thread = 2

    pool = Threadpool(count_thread)
    for dep_name, revision in binary_dict.items():
        if args.update:
            pool.add_task(update, external_out, args.force, dep_name, revision, deps_tree)
        else:
            pool.add_task(worker, external_out, args.platform, args.target, args.force, dep_name, revision, deps_tree)
    pool.wait_completion()


if __name__ == "__main__":
    main()
