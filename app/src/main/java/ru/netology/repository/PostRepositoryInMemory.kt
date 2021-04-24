package ru.netology.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.dto.Post

class PostRepositoryInMemory: PostRepository {
    var post = Post(
        id = 1,
        author = "Нетология. Университет интернет-профессий будущего",
        content = "Какой-то контент",
        published = "21 мая в 18:36",
        likedByMe = false
    )

    private val data = MutableLiveData(post)

    override fun get(): LiveData<Post> = data
    override fun like() {
        val likes = if (post.likedByMe) post.likes - 1 else post.likes + 1
        post = post.copy(likedByMe = !post.likedByMe, likes = likes)
        data.value = post
    }
    override fun share() {
        val shares = post.shares + 1
        post = post.copy(shares = shares)
        data.value = post
    }
}