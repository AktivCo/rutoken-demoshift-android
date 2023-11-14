[Russian/Русский](README_RUS.md)

## Description

Rutoken DemoShift is a demo application which shows use cases
of [Rutoken ECP Series](https://www.rutoken.ru/products/all/rutoken-ecp-3/) security tokens and smart cards (including
wireless devices) for a workgroup with a single mobile device.
It also contains some useful classes for device detection and signing docs.

DemoShift does not require the Rutoken Control Panel to be installed. For more information about this type of
integration with Rutokens, see [documentation](https://github.com/AktivCo/android-standalone-scheme-docs).

## Requirements

Rutoken DemoShift should be built using Android SDK Platform 34 or newer.

External dependencies are located in [Rutoken SDK](https://www.rutoken.ru/developers/sdk/).

Required libraries:

* librtpkcs11ecp.so for the following architectures: armeabi-v7a, arm64-v8a.

## How to build

Before building the project copy librtpkcs11ecp.so library to `<project_root>/app/src/main/jniLibs/<arch>/`, where
`<arch>` is library architecture.

Use Android Studio to build the project.

## Preliminary actions

To create a key pair and a certificate on Rutoken ECP Series devices follow these steps:

* Download and install [Rutoken plugin](https://www.rutoken.ru/products/all/rutoken-plugin/) on your desktop computer;
* Restart your browser to complete plugin installation;
* Go to the [Rutoken register center](https://ra.rutoken.ru) website;
* Connect Rutoken ECP Series device to your desktop computer;
    * For Rutoken ECP Bluetooth make sure that only red LED is active (without blue). If not, press and hold the button
      on the device until blue light turns off;
* Make sure that Rutoken ECP Series device is found by the website;
* Create a key pair and a certificate following the instructions on the website;
* Make sure that website has found the certificate and the key pair on your device;
* Disconnect the device from the desktop computer and use it with Android device.

## Restriction

* Rutoken DemoShift can only be run on physical devices, not on emulators.

## License

The project source code is distributed under the [Simplified BSD License](LICENSE), unless otherwise specified in the
source file.
