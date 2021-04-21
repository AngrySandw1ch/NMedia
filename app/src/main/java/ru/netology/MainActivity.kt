package ru.netology

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.netology.databinding.ActivityMainBinding
import ru.netology.dto.Post

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val post = Post(
            id = 1,
            author = "Нетология. Университет интернет-профессий будущего",
            content = "Какой-то контент",
            published = "21 мая в 18:36",
            likedByMe = false
        )

        var shareClickCounter = 998

        with(binding) {
            author.text = post.author
            content.text = post.content
            published.text = post.published

            if (post.likedByMe) {
                like.setImageResource(R.drawable.ic_baseline_favorite_red_24)
            }

            like.setOnClickListener {
                post.likedByMe = !post.likedByMe
                post.likes = 1234
                if (post.likedByMe) {
                    post.likes++
                    likesCounter.text = PostManager.formatNum(post.likes)
                    like.setImageResource(R.drawable.ic_baseline_favorite_red_24)
                }
                else {
                    post.likes--
                    likesCounter.text = PostManager.formatNum(post.likes)
                    like.setImageResource(R.drawable.ic_baseline_favorite_24)
                }
            }
            share.setOnClickListener {
                shareClickCounter++
                sharesCounter.text = PostManager.formatNum(shareClickCounter)
            }
        }
    }
}