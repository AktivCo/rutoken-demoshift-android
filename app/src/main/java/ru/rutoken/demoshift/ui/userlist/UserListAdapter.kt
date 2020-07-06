/*
 * Copyright (c) 2020, Aktiv-Soft JSC. See https://download.rutoken.ru/License_Agreement.pdf.
 * All Rights Reserved.
 */

package ru.rutoken.demoshift.ui.userlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import androidx.recyclerview.widget.SortedListAdapterCallback
import ru.rutoken.demoshift.R
import ru.rutoken.demoshift.database.User
import ru.rutoken.demoshift.databinding.FragmentUserBinding
import ru.rutoken.demoshift.ui.userlist.UserListFragmentDirections.toDocumentFragment
import java.text.SimpleDateFormat
import java.util.*


class UserListAdapter :
    RecyclerView.Adapter<UserListAdapter.UserViewHolder>() {
    private var users: SortedList<User> = SortedList(User::class.java, SortedListCallback(this))

    fun setUsers(userList: List<User>) {
        users.replaceAll(userList)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val view = holder.view
        val user = getUser(position)
        val binding = FragmentUserBinding.bind(view)
        binding.userFullName.text = user.fullName
        binding.userPosition.text =
            user.position ?: view.context.getString(R.string.field_not_set)
        binding.userOrganization.text = user.organization ?: view.context.getString(
            R.string.field_not_set
        )
        binding.userCertificateExpires.text =
            SimpleDateFormat("d MMMM yyyy", Locale.getDefault()).format(user.certificateExpires)

        binding.userCardView.setOnClickListener {
            view.findNavController().navigate(toDocumentFragment(user.id))
        }
    }

    fun getUser(position: Int): User = users[position]

    override fun getItemCount() = users.size()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val userView =
            FragmentUserBinding.inflate(LayoutInflater.from(parent.context), parent, false).root
        return UserViewHolder(userView)
    }

    class UserViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}

private class SortedListCallback(adapter: UserListAdapter) :
    SortedListAdapterCallback<User>(adapter) {
    override fun compare(o1: User, o2: User): Int {
        val fullNameCompare = o1.fullName.compareTo(o2.fullName)

        return if (fullNameCompare != 0)
            fullNameCompare
        else
            o1.tokenSerialNumber.compareTo(o2.tokenSerialNumber)
    }

    override fun areItemsTheSame(item1: User, item2: User) = item1 === item2

    override fun areContentsTheSame(oldItem: User, newItem: User) = compare(oldItem, newItem) == 0
}