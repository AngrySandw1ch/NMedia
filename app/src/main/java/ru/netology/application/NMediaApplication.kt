package ru.netology.application

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import ru.netology.auth.AppAuth
import javax.inject.Inject

@HiltAndroidApp
class NMediaApplication: Application() {
    private val appScope = CoroutineScope(Dispatchers.Default)
    @Inject
    lateinit var auth: AppAuth

    override fun onCreate() {
        super.onCreate()
        setupAuth()
    }

    private fun setupAuth() {
        auth.sendPushToken()
    }
}