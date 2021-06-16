package ru.netology

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import ru.netology.databinding.FragmentPostBinding
import ru.netology.viewmodel.PostViewModel

class PostFragment: Fragment() {
    val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentPostBinding.inflate(
            inflater,
            container,
            false
        )
        binding.post.like.setText(arguments?.getInt("likes").toString())
        binding.post.share.setText(arguments?.getInt("shares").toString())
        binding.post.content.setText(arguments?.getString("content").toString())
        binding.post.author.setText(arguments?.getString("author").toString())
        binding.post.published.setText(arguments?.getString("published").toString())

        binding.post.like.setOnClickListener {
            if (arguments != null) {
                viewModel.likeById(requireArguments().getInt("id"))
            }
        }
        binding.post.share.setOnClickListener {
            if (arguments != null) {
                viewModel.shareById(requireArguments().getInt("shares"))
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, binding.post.content.text)
                }
                val shareIntent = Intent.createChooser(intent, getString(R.string.chooserSharePost))
                startActivity(shareIntent)
            }
        }
        binding.post.videoPlayer.setOnClickListener {
            if (arguments != null) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(requireArguments().getString("media").toString()))
            }
        }
        viewModel.edited.observe(viewLifecycleOwner) {
            if (it.id == 0) {
                return@observe
            }
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}