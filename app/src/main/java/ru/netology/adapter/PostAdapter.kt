package ru.netology.adapter

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.BitmapImageViewTarget
import ru.netology.util.PostUtils
import ru.netology.R
import ru.netology.databinding.CardPostBinding
import ru.netology.dto.Post

interface OnInteractionListener {
    fun like(post: Post) {}
    fun edit(post: Post) {}
    fun remove(post: Post) {}
    fun share(post: Post) {}
    fun playVideo(post: Post){}
    fun postClicked(post: Post){}
}

class PostAdapter(
    private val onInteractionListener: OnInteractionListener
) :
    ListAdapter<Post, PostViewHolder>(PostDiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }

}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(post: Post) {
        with(binding) {
            author.text = post.author
            published.text = post.published
            content.text = post.content
            like.isChecked = post.likedByMe
            share.text = if (post.shares == 0) "" else PostUtils.formatNum(post.shares)
            like.text = if (post.likes == 0) "" else PostUtils.formatNum(post.likes)
            mediaLayout.visibility = if (post.media.isNullOrBlank()) View.GONE else View.VISIBLE

            val url = "http://10.0.2.2:9999/avatars/${post.authorAvatar}"

            Glide.with(avatar)
                .load(url)
                .circleCrop()
                .placeholder(R.drawable.ic_round_cloud_download_24)
                .error(R.drawable.ic_baseline_error_24)
                .into(avatar)


            playVideoButton.setOnClickListener {
                onInteractionListener.playVideo(post)
            }

            like.setOnClickListener {
                onInteractionListener.like(post)
            }
            share.setOnClickListener {
                onInteractionListener.share(post)
            }
            content.setOnClickListener {
                onInteractionListener.postClicked(post)
            }


            menu.setOnClickListener { it ->
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    setOnMenuItemClickListener { menuItem ->
                        when (menuItem.itemId) {
                            R.id.menu_remove -> {
                                onInteractionListener.remove(post)
                                true
                            }
                            R.id.menu_edit -> {
                                onInteractionListener.edit(post)
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

class PostDiffCallBack : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }

}