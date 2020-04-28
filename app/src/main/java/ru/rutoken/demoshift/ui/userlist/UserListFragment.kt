package ru.rutoken.demoshift.ui.userlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import ru.rutoken.demoshift.databinding.FragmentUserListBinding
import ru.rutoken.demoshift.user.User
import ru.rutoken.demoshift.user.UserRepository
import java.util.*

class UserListFragment : Fragment() {
    private val userRepository = UserRepository.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentUserListBinding.inflate(inflater)
        val userListAdapter = UserListAdapter(userRepository.getUsers().value ?: mutableListOf())

        binding.usersRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = userListAdapter
        }

        //FIXME: Remove using UserRepository from ui
        userRepository.getUsers().observe(viewLifecycleOwner, Observer {
            userListAdapter.users = it
            userListAdapter.notifyDataSetChanged()
        })

        userRepository.addUser(
            User(
                0,
                "Иванов Иван Иванович",
                "Генеральный директор",
                "ООО Организация больших и малых закупок",
                Date()
            )
        )
        userRepository.addUser(
            User(
                5,
                "Петров Иван Иванович",
                "Заместитель директора",
                "ООО Организация больших и малых закупок",
                Date()
            )
        )

        binding.addUserButton.setOnClickListener {
            findNavController().navigate(UserListFragmentDirections.toAddUserFragment())
        }

        return binding.root
    }
}