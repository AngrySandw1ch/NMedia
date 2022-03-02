package ru.netology.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import ru.netology.adapter.OnInteractionListener
import ru.netology.adapter.FeedAdapter
import ru.netology.dto.Post
import ru.netology.viewmodel.PostViewModel
import androidx.fragment.app.viewModels;
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.card_post.view.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import ru.netology.R
import ru.netology.adapter.PagingLoadStateAdapter
import ru.netology.auth.AppAuth
import ru.netology.databinding.FragmentFeedBinding
import javax.inject.Inject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class FeedFragment : Fragment() {

    val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    @Inject
    lateinit var appAuth: AppAuth

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

        val adapter = FeedAdapter(object : OnInteractionListener {
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

        val itemDecoration = DividerItemDecoration(requireActivity(), RecyclerView.VERTICAL)
            .apply {
                setDrawable(resources.getDrawable(R.drawable.divider_drawable))
            }

        binding.container.adapter = adapter.withLoadStateHeaderAndFooter(
            header = PagingLoadStateAdapter(object : PagingLoadStateAdapter.OnInteractionListener {
                override fun onRetry() {
                    adapter.retry()
                }
            }),
            footer = PagingLoadStateAdapter(object : PagingLoadStateAdapter.OnInteractionListener {
                override fun onRetry() {
                    adapter.retry()
                }
            })
        )
        binding.container.addItemDecoration(itemDecoration)

        lifecycleScope.launchWhenCreated {
            appAuth.authStateFlow.collectLatest {
                    adapter.refresh()
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.data.collectLatest {
                adapter.submitData(it)
            }
        }

        lifecycleScope.launchWhenCreated {
            adapter.loadStateFlow.collectLatest { state ->
                binding.swipeToRefresh.isRefreshing =
                    state.refresh is LoadState.Loading
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
            adapter.refresh()
        }
        return binding.root
    }
}