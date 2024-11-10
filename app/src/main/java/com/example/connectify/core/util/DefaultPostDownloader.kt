package com.example.connectify.core.util

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import androidx.core.net.toUri

class DefaultPostDownloader(
    private val context: Context
): PostDownloader {

    private val downloadManager = context.getSystemService(DownloadManager::class.java)

    override fun downloadFile(url: String): Long {
        val request = DownloadManager.Request(url.toUri())
            .setMimeType("image/jpeg")
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setTitle("Downloading Post")
            .setDescription("Downloading post image...")
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "post_image.jpg")
        return downloadManager.enqueue(request)
    }
}