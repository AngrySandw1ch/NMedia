package ru.netology.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.netology.dto.Post
import ru.netology.repository.PostRepository
import ru.netology.repository.PostRepositoryFileImpl
import ru.netology.repository.PostRepositoryInMemory
import ru.netology.repository.PostRepositorySharedPrefsImpl

private val empty = Post(
    id = 0,
    author = "",
    content = "",
    published = "",
    likedByMe = false
)

class PostViewModel(application: Application): AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositoryFileImpl(application)
    val data = repository.getAll()
    val edited = MutableLiveData(empty)

    fun likeById(id: Int) = repository.likeById(id)
    fun shareById(id: Int) = repository.shareById(id)
    fun removeById(id: Int) = repository.removeById(id)
    fun save() {
        edited.value?.let {
            repository.save(it)
        }
        edited.value = empty
    }
    fun edit(post: Post) {
        edited.value = post
    }
    fun changeContent(content: String) {
        val text = content.trim()
        edited.value?.let {
            if (it.content != text) {
                edited.value = edited.value?.copy(content = text)
            }

        }
    }
}