package ru.netology.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.PostManager
import ru.netology.R
import ru.netology.databinding.CardPostBinding
import ru.netology.dto.Post

typealias LikeListener = (Post) -> Unit
typealias ShareListener = (Post) -> Unit

class PostAdapter(private val likeListener: LikeListener, private val shareListener: ShareListener) :
    ListAdapter<Post, PostViewHolder>(PostDiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, likeListener, shareListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }

}

class PostViewHolder(private val binding: CardPostBinding, private val likeListener: LikeListener, private val shareListener: ShareListener) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) {
        with(binding) {
            author.text = post.author
            published.text = post.published
            content.text = post.content
            like.setImageResource(
                if (post.likedByMe) R.drawable.ic_baseline_favorite_red_24 else R.drawable.ic_baseline_favorite_24
            )
            likesCounter.text = if (post.likes == 0) "" else PostManager.formatNum(post.likes)
            sharesCounter.text = if (post.shares == 0) "" else PostManager.formatNum(post.shares)
            like.setOnClickListener {
                likeListener(post)
            }
            share.setOnClickListener {
                shareListener(post)
            }
        }
    }
}

class PostDiffCallBack: DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }

}