package ru.netology.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import ru.netology.R
import ru.netology.databinding.FragmentImageBinding


class ImageFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentImageBinding.inflate(inflater, container, false)
        val url = arguments?.getString("image_url")
        Glide.with(binding.image)
            .load(url)
            .placeholder(R.drawable.ic_round_cloud_download_24)
            .error(R.drawable.ic_baseline_error_24)
            .timeout(30_000)
            .into(binding.image)
        return binding.root
    }

}