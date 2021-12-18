package ru.netology.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.netology.R
import ru.netology.api.Api
import ru.netology.auth.AppAuth
import ru.netology.dto.Push
import ru.netology.error.ApiError
import ru.netology.error.NetworkError
import ru.netology.error.UnknownError
import java.io.IOException
import kotlin.random.Random

class FCMService : FirebaseMessagingService() {
    private val content = "content"
    private val channelId = "remote"
    private val gson = Gson()
    private var serverToken = Push()

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_remote_name)
            val descriptionText = getString(R.string.channel_remote_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val appToken = AppAuth.getInstance().authStateFlow.value
        appToken.token?.let { updatePushToken(it) }

        if (appToken.id == serverToken.recipientId) {
            showNotification(serverToken.recipientId, gson.fromJson(message.data[content], String::class.java))
        } else if (appToken.id != serverToken.recipientId && serverToken.recipientId == 0L) {
            AppAuth.getInstance().sendPushToken()
        } else if (appToken.id != serverToken.recipientId && serverToken.recipientId != 0L) {
            AppAuth.getInstance().sendPushToken()
        } else {
            showNotification(Random.nextLong(100,200), gson.fromJson(message.data[content], String::class.java))
        }

        //println(message.data["content"])
    }

    override fun onNewToken(token: String) {
        AppAuth.getInstance().sendPushToken(token)
    }

    private fun updatePushToken(token: String) {
        try {
            CoroutineScope(Dispatchers.Default).launch {
                val response = Api.service.getPush(token)
                if (!response.isSuccessful) {
                    throw ApiError(response.code(), response.message())
                }
                serverToken = response.body() ?: throw ApiError(response.code(), response.message())
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }

    }

    private fun showNotification(id: Long?, content: String?) {
        val notification = NotificationCompat.Builder(this, channelId)
        .setSmallIcon(R.drawable.ic_netology)
            .setContentTitle(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        id?.toInt()?.let {
            NotificationManagerCompat.from(this)
                .notify(it, notification)
        }
    }
}
