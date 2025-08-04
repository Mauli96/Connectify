package com.connectify.android.core.util

interface PostDownloader {
    fun downloadFile(url: String): Long
}