package ru.rutoken.demoshift.ui.userlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import ru.rutoken.demoshift.databinding.FragmentUserListBinding

class UserListFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentUserListBinding.inflate(inflater)
        val viewModel : UserListViewModel by viewModels()
        val userListAdapter = UserListAdapter(viewModel.getUsers().value ?: mutableListOf())

        binding.usersRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = userListAdapter
        }

        viewModel.getUsers().observe(viewLifecycleOwner, Observer {
            userListAdapter.users = it
            userListAdapter.notifyDataSetChanged()
        })

        binding.addUserButton.setOnClickListener {
            findNavController().navigate(UserListFragmentDirections.toAddUserFragment())
        }

        return binding.root
    }
}