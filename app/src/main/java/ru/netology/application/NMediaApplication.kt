package ru.netology.application

import android.app.Application
import ru.netology.auth.AppAuth

class NMediaApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        AppAuth.initApp(this)
    }
}