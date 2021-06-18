package ru.netology

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
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
        val postId = arguments?.getInt("id")
        viewModel.data.observe(viewLifecycleOwner) {
            val post = it.filter { it.id == postId }.first()

            post.apply {
                binding.post.content.text = "$content"
                binding.post.published.text = "$published"
                binding.post.author.text = "$author"
                binding.post.like.text = if (likes > 0) "$likes" else ""
                binding.post.share.text = "$shares"
                binding.post.like.isChecked = likedByMe
            }
            binding.post.like.setOnClickListener {
                    viewModel.likeById(post.id)
            }
            binding.post.share.setOnClickListener {
                    viewModel.shareById(post.id)
                    val intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, binding.post.content.text)
                    }
                    val shareIntent = Intent.createChooser(intent, getString(R.string.chooserSharePost))
                    startActivity(shareIntent)
            }
            binding.post.videoPlayer.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(post.media))
                    startActivity(intent)
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
                                findNavController().navigate(R.id.action_focusedPost_to_editPostFragment, bundle)
                                true
                            }
                            else -> false
                        }
                    }
                }.show()
            }
        }


        viewModel.edited.observe(viewLifecycleOwner) {
            if (it.id == 0) {
                return@observe
            }
        }
        return binding.root
    }
}