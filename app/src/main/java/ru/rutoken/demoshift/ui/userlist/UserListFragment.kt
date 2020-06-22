package ru.rutoken.demoshift.ui.userlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.rutoken.demoshift.databinding.FragmentUserListBinding
import ru.rutoken.demoshift.ui.pin.PinDialogFragment
import ru.rutoken.demoshift.ui.pin.PinDialogFragment.Companion.DIALOG_RESULT_KEY
import ru.rutoken.demoshift.ui.pin.PinDialogFragment.Companion.PIN_KEY
import ru.rutoken.demoshift.ui.userlist.UserListFragmentDirections.toAddUserFragment

class UserListFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        childFragmentManager.setFragmentResultListener(DIALOG_RESULT_KEY, this) { _, bundle ->
            val pin = bundle.getString(PIN_KEY)
            findNavController().navigate(toAddUserFragment(pin!!))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentUserListBinding.inflate(inflater)
        val viewModel: UserListViewModel by viewModel()
        val userListAdapter = UserListAdapter()

        binding.usersRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = userListAdapter
        }

        viewModel.getUsers().observe(viewLifecycleOwner, Observer {
            userListAdapter.users = it

            binding.emptyUserListTextView.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
            binding.usersRecyclerView.visibility = if (it.isEmpty()) View.GONE else View.VISIBLE

            userListAdapter.notifyDataSetChanged()
        })

        binding.addUserButton.setOnClickListener {
            PinDialogFragment().show(childFragmentManager, null)
        }

        return binding.root
    }
}