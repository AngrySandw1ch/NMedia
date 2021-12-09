package ru.netology.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import ru.netology.adapter.OnInteractionListener
import ru.netology.adapter.PostAdapter
import ru.netology.dto.Post
import ru.netology.viewmodel.PostViewModel
import androidx.fragment.app.viewModels;
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.card_post.view.*
import ru.netology.R
import ru.netology.databinding.FragmentFeedBinding

class FeedFragment : Fragment() {

    val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentFeedBinding = FragmentFeedBinding.inflate(
            inflater,
            container,
            false
        )

        val adapter = PostAdapter(object : OnInteractionListener {
            override fun edit(post: Post) {
                viewModel.edit(post)
                val bundle = Bundle()
                bundle.putString("edit text", post.content)
                findNavController().navigate(R.id.action_feedFragment_to_editPostFragment, bundle)
            }

            override fun playVideo(post: Post) {
                //val intent = Intent(Intent.ACTION_VIEW, Uri.parse(post.media))
                //startActivity(intent)
            }

            override fun like(post: Post) {
                if (!post.likedByMe) {
                    viewModel.likeById(post.id)
                } else {
                    viewModel.unlikeById(post.id)
                }
            }

            override fun remove(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun share(post: Post) {
                viewModel.shareById(post.id)
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, post.content)
                }
                val shareIntent = Intent.createChooser(intent, getString(R.string.chooserSharePost))
                startActivity(shareIntent)
            }

            override fun postClicked(post: Post) {
                val bundle = Bundle().apply {
                    putLong("id", post.id)
                }
                findNavController().navigate(R.id.action_feedFragment_to_postFragment, bundle)
            }

            override fun imageClicked(post: Post) {
                val bundle = Bundle().apply {
                    putString("image_url", post.attachment?.url)
                }
                findNavController().navigate(R.id.action_feedFragment_to_imageFragment, bundle)

            }
        })

        binding.container.adapter = adapter
        viewModel.data.observe(viewLifecycleOwner) { state ->
            //val newPost = adapter.itemCount > 0 && adapter.itemCount < state.posts.size
            adapter.submitList(state.posts)
            binding.emptyText.isVisible = state.empty

        }
        viewModel.newerCount.observe(viewLifecycleOwner) {
            if (it != 0) {
                binding.newerPostsButton?.isVisible = true
            }
        }

        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            binding.progress.isVisible = state.loading
            binding.errorGroup.isVisible = state.error
            binding.swipeToRefresh.isRefreshing = state.refreshing
            if (state.responseCode != 200) {
                binding.errorGroup.isVisible = false
                binding.serverErrorGroup?.isVisible = true
                binding.serverErrorText?.text =
                    getString(R.string.server_error, state.responseCode.toString())
            } else binding.serverErrorGroup?.isVisible = false

        }

        viewModel.edited.observe(viewLifecycleOwner) {
            if (it.id == 0L) {
                return@observe
            }
        }
        binding.newerPostsButton?.setOnClickListener {
            binding.container.smoothScrollToPosition(0)
            binding.newerPostsButton.isVisible = false
        }

        binding.retryButton.setOnClickListener {
            viewModel.loadPosts()
        }

        binding.repeatRequestButton?.setOnClickListener {
            viewModel.loadPosts()
        }

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
        }

        binding.swipeToRefresh.setOnRefreshListener {
            viewModel.refreshPosts()
        }
        return binding.root
    }


}