package com.feyzullahefendi.akraarsiv

import android.app.Activity
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception
import java.nio.charset.StandardCharsets

const val CATEGORY_URL = "http://www.akradyo.net/jsonmediacat.ashx"
const val PROGRAM_STREAM_LIST_URL = "http://www.akradyo.net/jsonmediaarchive.ashx?id=%s"
val client: OkHttpClient = OkHttpClient()

object RequestManager {
    fun getCategory(activity: Activity, listener: CategoryResponseInterface) {
        Thread {
            try {
                val request = Request.Builder().url(CATEGORY_URL).build()
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val body = String(response.body()!!.bytes(), StandardCharsets.UTF_8)
                    val json = JSONObject(body)
                    val cats = json.get("cats") as JSONArray
                    val categoryModelArray: ArrayList<CategoryModel> = ArrayList()
                    for (i in 0 until cats.length()) {
                        val cat = cats.getJSONObject(i)
                        val catId = cat.get("catId") as String
                        val catName = cat.get("catName") as String
                        val progs = cat.get("progs") as JSONArray
                        val progList: ArrayList<ChildProgram> = ArrayList()
                        for (j in 0 until progs.length()) {
                            val prog = progs.getJSONObject(j)
                            val progId = prog.get("id") as String
                            val progName = prog.get("name") as String
                            progList.add(ChildProgram(progId, progName))
                        }
                        categoryModelArray.add(CategoryModel(catId, catName, progList))
                    }
                    activity.runOnUiThread {
                        listener.success(categoryModelArray)
                    }
                    Log.i("TAG", "BAŞARILI")
                } else {
                    Log.i("TAG", "HATALI")
                    activity.runOnUiThread {
                        listener.error(Exception(response.message()))
                    }
                }
            } catch (error: Exception) {
                activity.runOnUiThread {
                    listener.error(Exception(error.message))
                }
            }

        }.start()
    }

    fun getStreamListOfProgram(activity: Activity, programId: String, listener: StreamResponseInterface) {
        Thread {
            try {
                val url = String.format(PROGRAM_STREAM_LIST_URL, programId)
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val body = String(response.body()!!.bytes(), StandardCharsets.UTF_8)
                    val streamList = JSONArray(body)
                    val streamModelList: ArrayList<StreamModel> = ArrayList()
                    for (i in 0 until streamList.length()) {
                        val item = streamList.getJSONObject(i)
                        val name = item.get("name") as String
                        val guid = item.get("guid") as String
                        val url = item.get("url") as String
                        val date = item.get("date") as String
                        streamModelList.add(StreamModel(guid, name, date, url))

                    }
                    activity.runOnUiThread{
                        listener.success(streamModelList)
                    }
                } else {
                    activity.runOnUiThread {
                        listener.error(Exception(response.message()))
                    }
                }
            } catch (error: Exception) {
                activity.runOnUiThread {
                    listener.error(error)
                }
            }


        }.start()
    }
}
//OkHttpClient client = new OkHttpClient();
//Request request = new Request.Builder().url(url).build();
//Response response = client.newCall(request).execute();


interface CategoryResponseInterface {
    fun success(categories: ArrayList<CategoryModel>)
    fun error(error: Exception)
}

interface StreamResponseInterface {
    fun success(streamModels: ArrayList<StreamModel>)
    fun error(error: Exception)
}