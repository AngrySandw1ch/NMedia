package ru.netology.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.netology.db.AppDb
import ru.netology.dto.Post
import ru.netology.model.FeedModel
import ru.netology.repository.*
import ru.netology.util.SingleLiveEvent
import java.io.IOException
import java.lang.RuntimeException
import kotlin.concurrent.thread

private val empty = Post(
    id = 0L,
    author = "",
    content = "",
    published = "",
    authorAvatar = "",
    likedByMe = false
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    // упрощённый вариант
    private val repository: PostRepository = PostRepositoryImpl()
    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel>
        get() = _data
    val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated


    init {
        loadPosts()
    }

    fun loadPosts() {
        _data.value = FeedModel(loading = true)
        repository.getAllAsync(object : PostRepository.Callback<List<Post>> {
            override fun onSuccess(posts: List<Post>) {
                _data.postValue(FeedModel(posts = posts, empty = posts.isEmpty(), responseCode = repository.getResponseCode()))
            }

            override fun onError(e: Exception) {
                _data.postValue(FeedModel(error = true))
            }
        })
    }

    fun save() {
        edited.value?.let {
            repository.save(it, object : PostRepository.Callback<Post> {
                override fun onSuccess(posts: Post) {
                   val posts1 = _data.value?.posts ?: throw Exception("Empty data value")
                   _data.postValue(FeedModel(posts = posts1.plus(posts), empty = posts1.isEmpty(), responseCode = repository.getResponseCode()))
                    _postCreated.postValue(Unit)
                }

                override fun onError(e: Exception) {
                    _data.postValue(FeedModel(error = true))
                }
            })
        }
        edited.value = empty
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }

    fun likeById(id: Long) {
        repository.likeById(id, object : PostRepository.Callback<Post> {
            override fun onSuccess(posts: Post) {
                _data.postValue(_data.value?.let { feedModel ->
                    feedModel.posts.let { list ->
                        list.map {
                            if(it.id != id) it else posts
                        }
                    }
                }?.let { postsList ->
                    _data.value?.copy(posts = postsList, responseCode = repository.getResponseCode())
                })
            }

            override fun onError(e: Exception) {
                println(e.stackTrace)
            }
        })
    }

    fun unlikeById(id: Long) {
        repository.dislikeById(id, object : PostRepository.Callback<Post> {
            override fun onSuccess(posts: Post) {
                _data.postValue(_data.value?.let { feedModel ->
                    feedModel.posts.let { list ->
                        list.map {
                            if(it.id != id) it else posts
                        }
                    }
                }?.let { postsList ->
                    _data.value?.copy(posts = postsList, responseCode = repository.getResponseCode())
                })
            }
            override fun onError(e: Exception) {
                println(e.stackTrace)
            }
        })
    }


    fun shareById(id: Long) {
        //thread { repository.shareById(id) }
    }

    fun removeById(id: Long) {
            repository.removeById(id, object : PostRepository.Callback<Unit> {
                override fun onSuccess(posts: Unit) {
                    _data.postValue(
                        _data.value?.copy(posts = _data.value?.posts.orEmpty()
                            .filter { it.id != id },
                            responseCode = repository.getResponseCode()))
                }

                override fun onError(e: Exception) {
                    println(e.stackTrace)
                }
            })
    }
}