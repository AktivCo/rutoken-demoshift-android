package ru.rutoken.demoshift.ui.sign

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import ru.rutoken.demoshift.databinding.FragmentSignBinding


class SignFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSignBinding.inflate(inflater)
        binding.signPdfView.fromAsset("sign_document.pdf")
            .scrollHandle(DefaultScrollHandle(requireContext()))
            .load()
        return binding.root
    }
}
