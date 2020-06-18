package ru.rutoken.demoshift.ui.sign

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf
import ru.rutoken.demoshift.R
import ru.rutoken.demoshift.databinding.FragmentSignBinding
import ru.rutoken.demoshift.databinding.WorkProgressBinding
import ru.rutoken.demoshift.ui.sign.SignFragmentDirections.toSignResultFragment
import ru.rutoken.demoshift.user.UserRepository
import ru.rutoken.demoshift.utils.asReadableText


class SignFragment : Fragment() {
    private lateinit var workProgressBinding: WorkProgressBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSignBinding.inflate(inflater)
        workProgressBinding = WorkProgressBinding.bind(binding.root)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args: SignFragmentArgs by navArgs()
        val viewModel: SignViewModel =
            getViewModel(parameters = { parametersOf(args.pin, args.documentUri, args.userId) })


        viewModel.status.observe(viewLifecycleOwner, Observer { status ->
            status.message?.let { workProgressBinding.statusTextView.text = it }

            workProgressBinding.progressBar.visibility =
                if (status.isProgress) View.VISIBLE else View.INVISIBLE
        })

        viewModel.result.observe(viewLifecycleOwner, Observer { result ->
            if (result.isSuccess) {
                findNavController().navigate(
                    toSignResultFragment(
                        args.documentUri,
                        result.getOrThrow()
                    )
                )
            } else {
                val exceptionMessage = (result.exceptionOrNull() as Exception).message.orEmpty()
                workProgressBinding.statusTextView.text =
                    result.exceptionOrNull()?.asReadableText(requireContext())
                        ?: "${getString(R.string.error_text)}\n\n" + exceptionMessage
            }
        })
    }
}
