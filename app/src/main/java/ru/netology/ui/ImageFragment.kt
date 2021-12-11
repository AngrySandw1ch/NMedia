package ru.netology.ui

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_edited_post.*
import ru.netology.R
import ru.netology.databinding.FragmentImageBinding


class ImageFragment : Fragment() {
    private val BASE_URL = "http://10.0.2.2:9999/media/"
    private lateinit var appContext: AppActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appContext = context as AppActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentImageBinding.inflate(inflater, container, false)
        val url = BASE_URL + arguments?.getString("image_url")
        Glide.with(binding.image)
            .load(url)
            .override(1000, 700)
            .placeholder(R.drawable.ic_round_cloud_download_24)
            .error(R.drawable.ic_baseline_error_24)
            .timeout(30_000)
            .into(binding.image)

        appContext.supportActionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.colorBlack)))
        return binding.root
    }

    override fun onDetach() {
        super.onDetach()
        appContext.supportActionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.colorPrimary)))
    }
}