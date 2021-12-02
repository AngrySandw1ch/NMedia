package ru.netology.viewmodel

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.switchMap
import kotlinx.coroutines.launch
import ru.netology.db.AppDb
import ru.netology.dto.Post
import ru.netology.model.FeedModel
import ru.netology.model.FeedModelState
import ru.netology.repository.*
import ru.netology.util.SingleLiveEvent
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map


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
    private val repository: PostRepository =
        PostRepositoryImpl(AppDb.getInstance(context = application).postDao())

    val data: LiveData<FeedModel> =
        repository.data.map {
            FeedModel(posts = it, empty = it.isEmpty())
        }.asLiveData(Dispatchers.Default)

    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    val newerCount: LiveData<Int> = data.switchMap {
        repository.getNewerCount(it.posts.firstOrNull()?.id ?: 0L)
            .catch { e -> e.printStackTrace() }
            .asLiveData(Dispatchers.Default)
    }



    val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    init {
        loadPosts()
    }

    fun loadPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(loading = true, responseCode = repository.responseCode)
            repository.getAll()
            _dataState.value = FeedModelState(responseCode = repository.responseCode)
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true, responseCode = repository.responseCode)
        }
    }

    fun refreshPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(refreshing = true, responseCode = repository.responseCode)
            repository.getAll()
            _dataState.value = FeedModelState(responseCode = repository.responseCode)
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true, responseCode = repository.responseCode)
        }
    }

    fun likeById(id: Long) = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(loading = true, responseCode = repository.responseCode)
            repository.likeById(id)
            _dataState.value = FeedModelState(responseCode = repository.responseCode)
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true, responseCode = repository.responseCode)
        }
    }

    fun unlikeById(id: Long) = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(loading = true, responseCode = repository.responseCode)
            repository.dislikeById(id)
            _dataState.value = FeedModelState(responseCode = repository.responseCode)
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true, responseCode = repository.responseCode)
        }
    }

    fun removeById(id: Long) = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(loading = true, responseCode = repository.responseCode)
            repository.removeById(id)
            _dataState.value = FeedModelState(responseCode = repository.responseCode)
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true, responseCode = repository.responseCode)
        }
    }

    fun save() {
        edited.value?.let {
            _postCreated.value = Unit
            viewModelScope.launch {
                try {
                    repository.save(it)
                    _dataState.value = FeedModelState(responseCode = repository.responseCode)
                } catch (e: Exception) {
                    _dataState.value = FeedModelState(error = true,
                        responseCode = repository.responseCode)
                }
            }
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

    fun shareById(id: Long) {
        //
    }


}