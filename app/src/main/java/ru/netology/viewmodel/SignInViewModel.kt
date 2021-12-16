package ru.netology.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.netology.api.PostsApi
import ru.netology.dto.UserKey
import java.lang.RuntimeException

class SignInViewModel: ViewModel() {

    private val _data: MutableLiveData<UserKey> = MutableLiveData<UserKey>()
    val data: LiveData<UserKey>
        get() = _data

    fun signIn(login: String, password: String, context: Context) = viewModelScope.launch {
        val response = PostsApi.service.updateUser(login, password)
        if (!response.isSuccessful) {
            Toast.makeText(context, "Incorrect login or password", Toast.LENGTH_LONG).show()
            return@launch
        }
        val userKey: UserKey = response.body() ?: throw RuntimeException("Body is null")
        _data.value = userKey
    }

}