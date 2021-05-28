package ru.netology

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract

class EditPostResultContract: ActivityResultContract<String, String?>(){
    override fun createIntent(context: Context, input: String?): Intent {
       val intent = Intent(context, EditedPostActivity::class.java)
        intent.putExtra("post content", input)
        return intent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): String? {
        if (resultCode == Activity.RESULT_OK) {
            return intent?.getStringExtra("edited post content")
        } else {
            return null
        }
    }
}