package ru.netology

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.launch
import androidx.activity.result.registerForActivityResult
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import ru.netology.adapter.OnInteractionListener
import ru.netology.adapter.PostAdapter
import ru.netology.dto.Post
import ru.netology.util.AndroidUtils
import ru.netology.viewmodel.PostViewModel
import androidx.fragment.app.viewModels;
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.card_post.view.*
import ru.netology.databinding.FragmentFeedBinding

class FeedFragment : Fragment() {

    val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentFeedBinding = FragmentFeedBinding.inflate(
            inflater,
            container,
            false
        )

        val adapter = PostAdapter (object: OnInteractionListener {
            override fun edit(post: Post) {
                viewModel.edit(post)
                val bundle = Bundle()
                bundle.putString("edit text", post.content)
                findNavController().navigate(R.id.action_feedFragment_to_editPostFragment, bundle)
            }

            override fun playVideo(post: Post) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(post.media))
                startActivity(intent)
            }

            override fun like(post: Post) {
                viewModel.likeById(post.id)
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
                    putInt("id", post.id)
                }
                findNavController().navigate(R.id.action_feedFragment_to_postFragment, bundle)
            }
        })
        binding.container.adapter = adapter
        viewModel.data.observe(viewLifecycleOwner) { posts ->
            adapter.submitList(posts)
        }
        viewModel.edited.observe(viewLifecycleOwner) {
            if (it.id == 0) {
                return@observe
            }
        }

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
        }

        return binding.root
    }


}