package com.example.connectify.core.util

interface PostDownloader {
    fun downloadFile(url: String): Long
}