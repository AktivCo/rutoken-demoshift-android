package ru.rutoken.demoshift.ui.userlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.rutoken.demoshift.R
import ru.rutoken.demoshift.databinding.FragmentUserBinding
import ru.rutoken.demoshift.user.User
import java.text.SimpleDateFormat
import java.util.*

class UserListAdapter(var users: List<User>) :
    RecyclerView.Adapter<UserListAdapter.UserViewHolder>() {
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
                if (user.certificateExpires != null)
                    SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
                        .format(user.certificateExpires)
                else
                    view.context.getString(R.string.field_not_set)
    }

    private fun getUser(position: Int) = users[position]

    override fun getItemCount() = users.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val userView =
            FragmentUserBinding.inflate(LayoutInflater.from(parent.context), parent, false).root
        return UserViewHolder(userView)
    }

    class UserViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}