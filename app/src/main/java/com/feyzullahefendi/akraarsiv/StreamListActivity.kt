package com.feyzullahefendi.akraarsiv

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.feyzullahefendi.akraarsiv.Utils.streamModelSubject
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.google.android.exoplayer2.audio.AudioListener
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util
import java.lang.Exception

class StreamListActivity : AppCompatActivity() {

    val mediaPlayer: MediaPlayer = MediaPlayer()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stream_list)

        val recyclerView = findViewById<RecyclerView>(R.id.activity_stream_list_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)


        val streamModel = Utils.childProgramSubject.value
        RequestManager.getStreamListOfProgram(this, streamModel!!.id, object : streamResponseInterface {
            override fun success(streamModels: ArrayList<StreamModel>) {
                val adapter = StreamModelRecyclerViewAdapter(streamModels)
                recyclerView.adapter = adapter

            }

            override fun error(error: Exception) {


            }
        })
    }

    fun setNotification() {
        NotificationCompat.Builder(this, "default")
    }


    inner class StreamModelRecyclerViewAdapter(val streamModels: ArrayList<StreamModel>) :
        RecyclerView.Adapter<StreamModelViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StreamModelViewHolder {
            val itemView = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.stream_item, parent, false)
            return StreamModelViewHolder(itemView)
        }

        override fun getItemCount(): Int {
            return this.streamModels.size
        }

        override fun onBindViewHolder(holder: StreamModelViewHolder, position: Int) {
            holder.bindData(streamModels[position])
        }


    }

    inner class StreamModelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val textView: TextView = itemView.findViewById(R.id.stream_item_title_text_view)
        private val startButton: Button = itemView.findViewById(R.id.stream_item_start_button)
        private val stopButton: Button = itemView.findViewById(R.id.stream_item_stop_button)

        lateinit var model: StreamModel


        init {
            startButton.setOnClickListener {
//                PlayService.startMusic(this@StreamListActivity, model)
                //                val mediaPlayer = this@StreamListActivity.mediaPlayer
//                if (mediaPlayer.isPlaying) {
//                    mediaPlayer.stop()
//                    mediaPlayer.reset()
//                    mediaPlayer.release()
//                }
//                mediaPlayer.setDataSource(model.onlinePlaylistUrl())
//                mediaPlayer.prepare()
//                mediaPlayer.start()

                // Create a data source factory.

//                val i = 0
                streamModelSubject.onNext(this.model)
                val mediaPlayerIntent = Intent(this@StreamListActivity, MediaPlayerActivity::class.java)
                startActivity(mediaPlayerIntent)

            }
            stopButton.setOnClickListener {
                //                val mediaPlayer = this@StreamListActivity.mediaPlayer
//                if (mediaPlayer.isPlaying) {
//                    mediaPlayer.stop()
//                    mediaPlayer.reset()
//                    mediaPlayer.release()
//                }
//                PlayService.stopMusic(this@StreamListActivity)
            }
        }

        fun bindData(model: StreamModel) {
            this.model = model
            textView.text = model.name
        }
    }
}
