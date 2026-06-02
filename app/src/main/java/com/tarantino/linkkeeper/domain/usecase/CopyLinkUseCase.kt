package com.tarantino.linkkeeper

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class CopyLinkUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    operator fun invoke(url: String): Boolean {
        return try {
            val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("Keepr Link", url)
            clipboardManager.setPrimaryClip(clipData)
            true
        } catch (e: Exception) {
            false
        }
    }
}
