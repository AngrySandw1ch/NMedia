package ru.netology.viewmodel

import androidx.lifecycle.ViewModel
import ru.netology.repository.PostRepository
import ru.netology.repository.PostRepositoryInMemory

class PostViewModel: ViewModel() {
    private val repository: PostRepository = PostRepositoryInMemory()
    val data = repository.get()
    fun like() = repository.like()
    fun share() = repository.share()
}