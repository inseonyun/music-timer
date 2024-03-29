package com.media_music_timer.util

import android.content.Context
import android.widget.Toast

fun showToast(
    context: Context,
    stringId: Int,
) {
    Toast.makeText(context, context.getString(stringId), Toast.LENGTH_SHORT).show()
}
