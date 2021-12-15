package ru.netology.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.R
import ru.netology.auth.AppAuth
import ru.netology.databinding.FragmentSignInBinding
import ru.netology.util.AndroidUtils
import ru.netology.viewmodel.SignInViewModel

class SignInFragment : Fragment() {
    private lateinit var binding: FragmentSignInBinding
    private val viewModel: SignInViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignInBinding.inflate(inflater, container, false)
        binding.login.requestFocus()

        binding.buttonEnter.setOnClickListener {
            viewModel.signIn(
                binding.login.text.toString(),
                binding.password.text.toString(),
                requireContext()
            )
        }

        viewModel.data.observe(viewLifecycleOwner) {
            if (it.id != 0L) {
                AppAuth.getInstance().setAuth(it.id, it.token)
                findNavController().navigate(R.id.action_signInFragment_to_feedFragment)
                AndroidUtils.hideKeyboard(requireView())
            }
        }

        return binding.root
    }

}