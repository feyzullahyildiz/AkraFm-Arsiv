package com.feyzullahefendi.akraarsiv

import com.feyzullahefendi.akraarsiv.Utils;
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun sourceUrlShouldBeValid() {
        assertEquals("com.feyzullahefendi.akraarsiv", "com.feyzullahefendi.akraarsiv")
        Utils.getSourceUrl("http://cdn.akradyo.net/vods3cf/_definst_/mp4:amazons3/akra/programlar/yeni/253/7159e243-7f3e-4772-9cce-355a570480d3.mp4")
        assertEquals(Utils.getSourceUrl("http://test.com/istanbul.mp4/playlist.m3u8"), "http://test.com/istanbul.mp4")
        assertEquals(Utils.getSourceUrl("http://test.com/istanbul.mp4/playlist.m3u8"), "http://test.com/istanbul.mp4")
        assertEquals(Utils.getSourceUrl("http://test.com/istanbul.mp4"), "http://test.com/istanbul.mp4")
        assertEquals(
            Utils.getSourceUrl("https://test.com/istanbul.mp4/playlist.m3u8?id=11"),
            "https://test.com/istanbul.mp4"
        )
        assertEquals(
            Utils.getSourceUrl("http://cdn.akradyo.net/vods3cf/_definst_/mp4:amazons3/akra/programlar/yeni/253/7159e243-7f3e-4772-9cce-355a570480d3.mp4/playlist.m3u8"),
            "http://cdn.akradyo.net/vods3cf/_definst_/mp4:amazons3/akra/programlar/yeni/253/7159e243-7f3e-4772-9cce-355a570480d3.mp4"
        )
    }

    @Test
    fun FFmpegFilterIsCorrect() {
        assertEquals(Utils.getFFmpegFilterComplexValue(1), "[0:0]concat=n=1:v=0:a=1[out]")
        assertEquals(Utils.getFFmpegFilterComplexValue(2), "[0:0][1:0]concat=n=2:v=0:a=1[out]")
        assertEquals(Utils.getFFmpegFilterComplexValue(3), "[0:0][1:0][2:0]concat=n=3:v=0:a=1[out]")
    }

    @Test
    fun durationAsString() {
        assertEquals(Utils.getTimeFromSeconds(10 * 1000), "00:00:10")
        assertEquals(Utils.getTimeFromSeconds(60 * 1000), "00:01:00")
        assertEquals(Utils.getTimeFromSeconds(2 * 60 * 60 * 1000), "02:00:00")
    }

}
