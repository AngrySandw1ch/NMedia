package ru.netology

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.netology.databinding.ActivityEditedPostBinding

class EditedPostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityEditedPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val postContent = intent.getStringExtra("post content")
        binding.edit.setText(postContent)
        binding.edit.requestFocus()


        binding.ok.setOnClickListener {
            val intent = Intent()
            if (binding.edit.text.isNullOrBlank()) {
                setResult(Activity.RESULT_CANCELED, intent)
            } else {
                val content = binding.edit.text.toString()
                intent.putExtra("edited post content", content)
                setResult(Activity.RESULT_OK, intent)
            }
            finish()
        }
        binding.cancel.setOnClickListener {
            val intent = Intent()
            setResult(Activity.RESULT_CANCELED, intent)
            finish()
        }
    }
}