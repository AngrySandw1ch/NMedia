package ru.netology.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.netology.util.PostUtils
import ru.netology.R
import ru.netology.databinding.CardAdBinding
import ru.netology.databinding.CardPostBinding
import ru.netology.dto.Ad
import ru.netology.dto.FeedItem
import ru.netology.dto.Post

interface OnInteractionListener {
    fun like(post: Post) {}
    fun edit(post: Post) {}
    fun remove(post: Post) {}
    fun share(post: Post) {}
    fun playVideo(post: Post) {}
    fun postClicked(post: Post) {}
    fun imageClicked(post: Post) {}
    fun onAdClick(ad: Ad) {}
}

class FeedAdapter(
    private val onInteractionListener: OnInteractionListener
) :
    PagingDataAdapter<FeedItem, PostViewHolder>(FeedItemDiffCallBack()) {
    private val typeAd = 0
    private val typePost = 1

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Ad -> typeAd
            is Post -> typePost
            null -> throw IllegalArgumentException("unknown item type")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            typeAd -> AdViewHolder(
                CardAdBinding.inflate(layoutInflater, parent, false),
                onInteractionListener
            )
            typePost -> PostViewHolder(
                CardPostBinding.inflate(layoutInflater, parent, false),
                onInteractionListener
            )
            else -> throw IllegalArgumentException("unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        getItem(position)?.let {
            when (it) {
                is Post -> (holder as? PostViewHolder)?.bind(it)
                is Ad -> (holder as? AdViewHolder)?.bind(it)
            }
        }
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
            menu.visibility = if (post.ownedByMe) View.VISIBLE else View.GONE
            imageContent.visibility =
                if (post.attachment?.url.isNullOrBlank()) View.GONE else View.VISIBLE

            val url = "http://10.0.2.2:9999/avatars/${post.authorAvatar}"
            val imageUrl = "http://10.0.2.2:9999/media/${post.attachment?.url}"

            Glide.with(avatar)
                .load(url)
                .placeholder(R.drawable.ic_round_cloud_download_24)
                .error(R.drawable.ic_baseline_error_24)
                .circleCrop()
                .timeout(30_000)
                .into(avatar)

            Glide.with(imageContent)
                .load(imageUrl)
                //.override(1000,700)
                .placeholder(R.drawable.ic_round_cloud_download_24)
                .error(R.drawable.ic_baseline_error_24)
                .timeout(30_000)
                .into(imageContent)

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
            imageContent.setOnClickListener {
                onInteractionListener.imageClicked(post)
            }


            menu.setOnClickListener { it ->
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    menu.setGroupVisible(R.id.owned, post.ownedByMe)
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

class AdViewHolder(
    private val binding: CardAdBinding,
    private val onInteractionListener: OnInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(ad: Ad) {
        with(binding) {
            Glide.with(image)
                .load("http://10.0.2.2:9999/media/${ad.image}")
                .timeout(30_000)
                .into(image)

            image.setOnClickListener {
                onInteractionListener.onAdClick(ad)
            }
        }
    }
}


class FeedItemDiffCallBack : DiffUtil.ItemCallback<FeedItem>() {
    override fun areItemsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
        if(oldItem::class != newItem::class) {
            return false
        }
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
        return oldItem == newItem
    }

}