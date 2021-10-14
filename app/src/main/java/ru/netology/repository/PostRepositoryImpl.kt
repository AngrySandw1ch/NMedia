package ru.netology.repository


import android.icu.number.IntegerWidth
import okhttp3.*
import ru.netology.api.PostsApi
import ru.netology.dto.Post
import java.io.IOException
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Call

class PostRepositoryImpl : PostRepository {
    private var responseCode: Int = 0



    override fun getAllAsync(callback: PostRepository.Callback<List<Post>>) {
        PostsApi.retrofitService.getAll().enqueue(object : Callback<List<Post>> {
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                if (!response.isSuccessful) {
                    responseCode = response.code()
                    callback.onError(RuntimeException(response.message()))
                    return
                }
                responseCode = response.code()
                callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))
            }

            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                callback.onError(RuntimeException(t.message))
            }
        })
    }

    override fun save(post: Post, callback: PostRepository.Callback<Post>) {
        PostsApi.retrofitService.save(post).enqueue(object : Callback<Post> {
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                if (!response.isSuccessful) {
                    responseCode = response.code()
                    callback.onError(RuntimeException(response.message()))
                    return
                }
                responseCode = response.code()
                callback.onSuccess(response.body() ?: throw RuntimeException("Body is null"))
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                callback.onError(RuntimeException(t.message))
            }
        })
    }

    override fun removeById(id: Long, callback: PostRepository.Callback<Unit>) {
        PostsApi.retrofitService.removeById(id).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (!response.isSuccessful) {
                    responseCode = response.code()
                    callback.onError(RuntimeException(response.message()))
                    return
                }
                responseCode = response.code()
                callback.onSuccess(Unit)
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                callback.onError(RuntimeException(t.message))
            }


        })
    }

    override fun likeById(id: Long, callback: PostRepository.Callback<Post>) {
        PostsApi.retrofitService.likeById(id).enqueue(object : Callback<Post> {
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                if (!response.isSuccessful) {
                    responseCode = response.code()
                    callback.onError(RuntimeException(response.message()))
                    return
                }
                responseCode = response.code()
                callback.onSuccess(response.body() ?: throw RuntimeException("Body is null"))
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                 callback.onError(RuntimeException(t.message))
            }
        })
    }

    override fun dislikeById(id: Long, callback: PostRepository.Callback<Post>) {
        PostsApi.retrofitService.dislikeById(id).enqueue(object : Callback<Post> {
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                if (!response.isSuccessful) {
                    responseCode = response.code()
                    callback.onError(RuntimeException(response.message()))
                    return
                }
                responseCode = response.code()
                callback.onSuccess(response.body() ?: throw RuntimeException("Body is null"))
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                callback.onError(RuntimeException(t.message))
            }

        })
    }

    override fun getResponseCode(): Int {
        return responseCode
    }

}