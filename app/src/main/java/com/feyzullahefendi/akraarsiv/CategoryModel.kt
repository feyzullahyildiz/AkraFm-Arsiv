package com.feyzullahefendi.akraarsiv

import java.io.Serializable

class CategoryModel(val catId: String, val catName: String, val progs: ArrayList<ChildProgram>)
class ChildProgram(val id: String, val name: String)
open class StreamModel(val guid: String, val name: String, val date: String, val url: String): Serializable {
    private val base = "http://cdn.akradyo.net/vods3cf/_definst_/%s"

    open fun sourceUrl(): String {
        return String.format(base, url)
    }
//    fun onlinePlaylistUrl(): String {
//        return  sourceUrl() + "/playlist.m3u8"
//    }
    open fun isMp4(): Boolean {
        return  true
    }
}

class LiveRadioStreamModal(): StreamModel("live_radio", "Canlı Radyo", "", "") {
    override fun sourceUrl(): String {
        return "http://cdn3.akradyo.net/akracanli2/_definst_/livestream_aac/chunklist.m3u8"
    }

    override fun isMp4(): Boolean {
        return false
    }
}