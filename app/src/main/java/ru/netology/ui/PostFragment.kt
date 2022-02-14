package ru.netology.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.paging.map
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import ru.netology.R
import ru.netology.databinding.FragmentPostBinding
import ru.netology.viewmodel.PostViewModel

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class PostFragment : Fragment() {
    val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPostBinding.inflate(
            inflater,
            container,
            false
        )
        val postId = arguments?.getLong("id")

        lifecycleScope.launchWhenCreated {
            viewModel.data.collect {
                it.map { post ->
                    if (post.id == postId) {
                        binding.post.content.text = post.content
                        binding.post.published.text = post.published
                        binding.post.author.text = post.author
                        binding.post.like.text = if (post.likes > 0) "${post.likes}" else ""
                        binding.post.share.text = if (post.shares > 0) "${post.shares}" else ""
                        binding.post.like.isChecked = post.likedByMe
                        binding.post.like.setOnClickListener {
                            viewModel.likeById(post.id)
                        }
                        binding.post.share.setOnClickListener {
                            viewModel.shareById(post.id)
                            val intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, binding.post.content.text.toString())
                            }
                            val shareIntent =
                                Intent.createChooser(intent, getString(R.string.chooserSharePost))
                            startActivity(shareIntent)
                        }
                        binding.post.videoPlayer.setOnClickListener {
                            //val intent = Intent(Intent.ACTION_VIEW, Uri.parse(media))
                            // startActivity(intent)
                        }
                        binding.post.menu.setOnClickListener { menu ->
                            PopupMenu(menu.context, menu).apply {
                                inflate(R.menu.options_post)
                                setOnMenuItemClickListener { menuItem ->
                                    when (menuItem.itemId) {
                                        R.id.menu_remove -> {
                                            viewModel.removeById(post.id)
                                            findNavController().navigate(R.id.action_focusedPost_to_feedFragment)
                                            true
                                        }
                                        R.id.menu_edit -> {
                                            viewModel.edit(post)
                                            val bundle = Bundle()
                                            bundle.putString("edit text", post.content)
                                            findNavController().navigate(
                                                R.id.action_focusedPost_to_editPostFragment,
                                                bundle
                                            )
                                            true
                                        }
                                        else -> false
                                    }
                                }
                            }.show()
                        }
                    }
                }
            }
        }
        viewModel.edited.observe(viewLifecycleOwner) {
            if (it.id == 0L) {
                return@observe
            }
        }

        return binding.root
    }
}