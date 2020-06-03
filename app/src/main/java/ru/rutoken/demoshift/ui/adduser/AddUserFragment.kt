package ru.rutoken.demoshift.ui.adduser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import ru.rutoken.demoshift.databinding.FragmentAddUserBinding
import ru.rutoken.demoshift.ui.adduser.AddUserFragmentDirections.toUserListFragment
import ru.rutoken.demoshift.user.User
import ru.rutoken.demoshift.utils.showError
import java.util.*


class AddUserFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentAddUserBinding.inflate(inflater)
        val viewModel: AddUserViewModel by viewModels()

        binding.newUserButton.setOnClickListener {
            val user = User(
                "Захаров Захар Захарович",
                "CEO",
                "ООО Рога и копыта",
                Date()
            )
            viewModel.addUser(user)
        }

        viewModel.userAddingSucceed.observe(viewLifecycleOwner, Observer {
            findNavController().navigate(toUserListFragment())
        })

        viewModel.userAddingFailure.observe(viewLifecycleOwner, Observer {
            showError(binding.root, it)
        })

        return binding.root
    }
}