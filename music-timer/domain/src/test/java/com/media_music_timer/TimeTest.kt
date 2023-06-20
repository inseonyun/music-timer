package com.media_music_timer

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalTime

class TimeTest {
    @Test
    fun `타이머의 시를 반환한다`() {
        val time = LocalTime.of(20, 0, 0)
        val timer = Time(time)

        val actual = timer.getHour()
        val expected = 20

        assertEquals(expected, actual)
    }

    @Test
    fun `타이머의 분을 반환한다`() {
        val time = LocalTime.of(20, 30, 0)
        val timer = Time(time)

        val actual = timer.getMinute()
        val expected = 30

        assertEquals(expected, actual)
    }

    @Test
    fun `타이머의 초를 반환한다`() {
        val time = LocalTime.of(19, 59, 10)
        val timer = Time(time)

        val actual = timer.getSecond()
        val expected = 10

        assertEquals(expected, actual)
    }

    @Test
    fun `타이머의 총 시간을 초로 반환한다`() {
        val time = LocalTime.of(20, 0, 0)
        val timer = Time(time)

        val actual = timer.getSecondOfDayTotalTime()
        val expected = 20 * 60 * 60L

        assertEquals(expected, actual)
    }
}
