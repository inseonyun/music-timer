package com.media_music_timer.model

import android.os.Parcelable
import com.media_music_timer.Time
import kotlinx.parcelize.Parcelize
import java.time.LocalTime

@Parcelize
data class TimeModel(
    val time: LocalTime
) : Parcelable {
    companion object {
        fun TimeModel.toModel(): Time = Time(time)
        fun Time.toUIModel(): TimeModel = TimeModel(time)
    }
}
