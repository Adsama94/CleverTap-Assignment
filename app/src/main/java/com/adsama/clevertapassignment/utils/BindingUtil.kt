package com.adsama.clevertapassignment.utils

import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.BindingAdapter
import com.adsama.clevertapassignment.R
import com.bumptech.glide.Glide

@BindingAdapter("hundImage")
fun setHundImage(imageView: ImageView, hundImageUrl: String?) {
    Glide.with(imageView.context).load(hundImageUrl)
        .error(AppCompatResources.getDrawable(imageView.context, R.drawable.empty_error))
        .into(imageView)
}

@BindingAdapter("setSubmitButtonState")
fun setSubmitButtonState(button: Button, string: String?) {
    if (string.isNullOrEmpty()) {
        button.isEnabled = false
    } else button.isEnabled = string.toInt() <= 10
}