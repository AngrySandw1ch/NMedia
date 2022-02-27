package ru.netology.viewmodel

import android.net.Uri
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.netology.dto.Post
import ru.netology.model.FeedModel
import ru.netology.model.FeedModelState
import ru.netology.repository.*
import ru.netology.util.SingleLiveEvent
import ru.netology.auth.AppAuth
import ru.netology.dto.MediaUpload
import ru.netology.model.PhotoModel
import java.io.File
import javax.inject.Inject


private val empty = Post(
    id = 0L,
    authorId = 0L,
    author = "",
    content = "",
    published = "",
    authorAvatar = "",
    likedByMe = false
)

@ExperimentalCoroutinesApi
@HiltViewModel
class PostViewModel @Inject constructor(
    private val repository: PostRepository,
    auth: AppAuth
    ) : ViewModel() {

    private val cached = repository
        .data
        .cachedIn(viewModelScope)

    val data: Flow<PagingData<Post>> = auth
        .authStateFlow
        .flatMapLatest { (myId, _) ->
           cached.map {
               it.map { post ->
                   post.copy(ownedByMe = post.authorId == myId)
               }
           }
        }

    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState


    private val noPhoto = PhotoModel()
    private val _photo = MutableLiveData(noPhoto)
    val photo: LiveData<PhotoModel>
        get() = _photo

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
                    when(_photo.value) {
                        noPhoto -> repository.save(it)
                        else -> photo.value?.file?.let { file ->
                            repository.saveWithAttachment(it, MediaUpload(file))
                        }
                    }
                    _dataState.value = FeedModelState(responseCode = repository.responseCode)
                } catch (e: Exception) {
                    _dataState.value = FeedModelState(error = true,
                        responseCode = repository.responseCode)
                }
            }
        }
        edited.value = empty
        _photo.value = noPhoto
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

    fun changePhoto(uri: Uri?, file: File?) {
        _photo.value = PhotoModel(uri, file)
    }

    fun getById(id: Long): Flow<Post?> {
        return repository.getById(id)
    }




}