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
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                ?: return false
            clipboard.setPrimaryClip(ClipData.newPlainText("Keepr Link", url))
            true
        } catch (e: Exception) {
            false
        }
    }
}
