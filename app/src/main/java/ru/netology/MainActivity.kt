package ru.netology

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.observe
import ru.netology.databinding.ActivityMainBinding
import ru.netology.dto.Post
import ru.netology.viewmodel.PostViewModel

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        val viewModel: PostViewModel by viewModels()
        viewModel.data.observe(this) { post ->
            with(binding) {
                author.text = post.author
                content.text = post.content
                published.text = post.published
                like.setImageResource(if (post.likedByMe) R.drawable.ic_baseline_favorite_red_24 else R.drawable.ic_baseline_favorite_24)
                likesCounter.text = PostManager.formatNum(post.likes)
                sharesCounter.text = if (post.shares == 0) "" else PostManager.formatNum(post.shares)
            }
        }

        binding.like.setOnClickListener {
            viewModel.like()
        }
        binding.share.setOnClickListener {
            viewModel.share()
        }




    }
}