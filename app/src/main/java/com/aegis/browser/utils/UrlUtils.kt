package com.aegis.browser.utils

import java.net.URLEncoder

object UrlUtils {

    fun smartUrlFilter(input: String): String {
        val trimmed = input.trim()
        return when {
            trimmed.startsWith("http://") || trimmed.startsWith("https://") -> trimmed
            trimmed.contains(".") && !trimmed.contains(" ") -> "https://$trimmed"
            else -> {
                val encoded = URLEncoder.encode(trimmed, "UTF-8")
                "https://www.google.com/search?q=$encoded"
            }
        }
    }
}