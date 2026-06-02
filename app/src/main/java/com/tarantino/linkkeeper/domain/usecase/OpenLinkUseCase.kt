package com.tarantino.linkkeeper

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.app.Activity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class OpenLinkUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    operator fun invoke(url: String): Result<Unit> {
        val uri = runCatching { Uri.parse(url) }.getOrNull() 
            ?: return Result.failure(IllegalArgumentException("Invalid URL"))
        
        return try {
            val intent = Intent(Intent.ACTION_VIEW, uri)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            Result.success(Unit)
        } catch (e: ActivityNotFoundException) {
            Result.failure(e)
        } catch (e: SecurityException) {
            Result.failure(e)
        }
    }
}
