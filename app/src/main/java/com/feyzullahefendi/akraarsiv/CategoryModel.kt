package com.feyzullahefendi.akraarsiv

import java.io.Serializable

class CategoryModel(val catId: String, val catName: String, val progs: ArrayList<ChildProgram>)
class ChildProgram(val id: String, val name: String)
class StreamModel(val guid: String, val name: String, val date: String, val url: String): Serializable {
    private val base = "http://cdn.akradyo.net/vods3cf/_definst_/%s"

    fun sourceUrl(): String {
        return String.format(base, url)
    }
    fun onlinePlaylistUrl(): String {
        return  sourceUrl() + "/playlist.m3u8"
    }
}
