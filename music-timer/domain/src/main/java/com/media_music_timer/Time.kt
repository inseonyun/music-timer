package com.media_music_timer

import java.time.LocalTime

data class Time (
    val time: LocalTime
) {
    fun getHour(): Int = time.hour
    fun getMinute(): Int = time.minute
    fun getSecond(): Int = time.second
    fun getSecondOfDayTotalTime(): Long = time.toSecondOfDay().toLong()

    companion object {
        fun from(secondOfDayTotalTime: Long): Time {
            return Time(LocalTime.ofSecondOfDay(secondOfDayTotalTime))
        }
    }
}
