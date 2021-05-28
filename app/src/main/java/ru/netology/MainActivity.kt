package ru.netology

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.launch
import androidx.activity.result.registerForActivityResult
import androidx.activity.viewModels
import androidx.lifecycle.observe
import ru.netology.adapter.OnInteractionListener
import ru.netology.adapter.PostAdapter
import ru.netology.databinding.ActivityMainBinding
import ru.netology.dto.Post
import ru.netology.util.AndroidUtils
import ru.netology.viewmodel.PostViewModel

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val viewModel: PostViewModel by viewModels()
        val editPostLauncher = registerForActivityResult(EditPostResultContract()) {
            it ?: return@registerForActivityResult
            viewModel.changeContent(it)
            viewModel.save()
        }
        val adapter = PostAdapter (object: OnInteractionListener {
            override fun edit(post: Post) {
                viewModel.edit(post)
                editPostLauncher.launch(post.content)
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
        })
        binding.container.adapter = adapter
        viewModel.data.observe(this) { posts ->
            adapter.submitList(posts)
        }
        viewModel.edited.observe(this) {
            if (it.id == 0) {
                return@observe
            }
        }

        val postLauncher = registerForActivityResult(NewPostResultContract()) {
            it ?: return@registerForActivityResult
            viewModel.changeContent(it)
            viewModel.save()
        }
        binding.fab?.setOnClickListener {
            postLauncher.launch()
        }
    }
}