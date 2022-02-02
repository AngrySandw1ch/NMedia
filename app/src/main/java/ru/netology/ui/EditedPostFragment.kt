package ru.netology.ui


import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.databinding.FragmentEditedPostBinding
import ru.netology.util.AndroidUtils
import ru.netology.util.StringArg
import ru.netology.viewmodel.PostViewModel

@AndroidEntryPoint
class EditedPostFragment : Fragment() {
    val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    companion object {
        var Bundle.edit: String? by StringArg
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentEditedPostBinding = FragmentEditedPostBinding.inflate(
            inflater,
            container,
            false
        )
        binding.edit.setText(arguments?.getString("edit text").toString())
        binding.edit.requestFocus()

        binding.ok.setOnClickListener {
            viewModel.changeContent(binding.edit.text.toString())
            viewModel.save()
            viewModel.loadPosts()
            AndroidUtils.hideKeyboard(requireView())
            findNavController().navigateUp()
        }
        binding.cancel.setOnClickListener {
            AndroidUtils.hideKeyboard(requireView())
            findNavController().navigateUp()
        }

        return binding.root
    }
}