package com.example.connectify.core.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.connectify.R

fun Context.sendSharePostIntent(postId: String) {
    val intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(
            Intent.EXTRA_TEXT,
            getString(
                R.string.share_intent_text,
                "https://connectify.com/$postId"
            )
        )
        type = "text/plain"
    }

    if(intent.resolveActivity(packageManager) != null) {
        startActivity(Intent.createChooser(intent, "Select an app"))
    }
}

fun Context.openUrlInBrowser(url: String) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse(url)
    }
    startActivity(Intent.createChooser(intent, "Select an app"))
}