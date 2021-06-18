package ru.netology

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import ru.netology.NewPostFragment.Companion.textArg


class AppActivity : AppCompatActivity(R.layout.activity_app) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent?.let {
            if (it.action != Intent.ACTION_SEND) {
                return@let
            }

            val text = it.getStringExtra(Intent.EXTRA_TEXT)

            if (text?.isNotBlank() != true) {
                findNavController(R.id.nav_host_fragment).navigate(
                    R.id.action_feedFragment_to_newPostFragment,
                    Bundle().apply {
                        textArg = text
                })
            }
        }

    }
}