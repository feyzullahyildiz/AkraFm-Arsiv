package com.feyzullahefendi.akraarsiv

import android.app.*
import android.content.BroadcastReceiver
import android.content.Intent
import android.content.Context
import android.content.IntentFilter
import android.net.Uri
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.android.exoplayer2.Player
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.exoplayer2.ExoPlayer
import java.util.*


class PlayService : Service() {
    companion object {
        const val INTENT_FILTER_NAME = "play-service"
        const val PLAY = 1
        const val FORWARD = 2
        const val BACKWARD = 3
        const val SEEK_TO = 4
        const val NOTIFY = 5
        private var exoPlayer: SimpleExoPlayer? = null

        var seekBarTimer: Timer? = null
        lateinit var handler: Handler
    }

    var model: StreamModel? = null
    override fun onCreate() {
        super.onCreate()
        Log.i(Utils.TAG, "PlayService onCreate")
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, IntentFilter(INTENT_FILTER_NAME))
        seekBarTimer = Timer()
        handler = Handler()
    }

    fun startTimer() {
        if (seekBarTimer == null) {
            seekBarTimer = Timer()
        }
        seekBarTimer?.purge()
        seekBarTimer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                handler.post {
                    val currentPosition = exoPlayer!!.currentPosition
                    sendStatus(MediaPlayerFragment.SEEKBAR_UPDATE, currentPosition)
                }

            }
        }, 0, 1000)
    }

    fun stopTimer() {
        seekBarTimer?.purge()
        seekBarTimer?.cancel()
        seekBarTimer = null
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.i(Utils.TAG, "PlayService onBind")
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(Utils.TAG, "PlayService onStartCommand")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver)
    }

    private val messageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.i(Utils.TAG, "onReceive messageReceiver")
            playerInit()
            when (intent?.getIntExtra("state", -1)) {
                PLAY -> {
                    exoPlayer!!.playWhenReady = !exoPlayer!!.playWhenReady
                    if (exoPlayer!!.playWhenReady) {
                        startTimer()
                        sendStatus(MediaPlayerFragment.PLAY_STATE_CHANGE, MediaPlayerFragment.VALUE_STARTED)

                    } else {
                        stopTimer()
                        sendStatus(MediaPlayerFragment.PLAY_STATE_CHANGE, MediaPlayerFragment.VALUE_PAUSED)
                    }
                }
                BACKWARD -> {
                    val duration = exoPlayer!!.currentPosition
                    if (duration > 5000) {
                        exoPlayer!!.seekTo(duration.minus(5000))
                    } else {
                        exoPlayer!!.seekTo(0)

                    }

                }
                FORWARD -> {
                    val duration = exoPlayer!!.currentPosition
                    val totalLen = exoPlayer!!.duration
                    if (totalLen > duration.plus(5000)) {
                        exoPlayer!!.seekTo(duration.plus(5000))
                    }
                }
                SEEK_TO -> {
                    val seekValue = intent.getLongExtra("payload", -1)
                    exoPlayer!!.seekTo(seekValue)
                }
                NOTIFY -> {
                    Log.e(Utils.TAG, "NOTIFY ME AGAIN DUDE")
                    val realDurationMillis = exoPlayer!!.duration
                    sendStatus(MediaPlayerFragment.TIME_SET, realDurationMillis)
                    if (exoPlayer!!.playWhenReady) {
                        sendStatus(MediaPlayerFragment.PLAY_STATE_CHANGE, MediaPlayerFragment.VALUE_STARTED)
                    } else {
                        sendStatus(MediaPlayerFragment.PLAY_STATE_CHANGE, MediaPlayerFragment.VALUE_PAUSED)
                    }
                    val currentPosition = exoPlayer!!.currentPosition
                    sendStatus(MediaPlayerFragment.SEEKBAR_UPDATE, currentPosition)

                }
                -1 -> {
                    val streamModel = intent.getSerializableExtra("model") as StreamModel
                    if(model != null && streamModel.guid == model?.guid) {
                        return
                    }
                    val dataSourceFactory =
                        DefaultHttpDataSourceFactory(Util.getUserAgent(this@PlayService, "akra-arsiv"))
                    val uri: Uri = Uri.parse(streamModel.sourceUrl())
                    val mediaSource = SsMediaSource.Factory(dataSourceFactory).createMediaSource(uri)

                    exoPlayer!!.playWhenReady = true
                    sendStatus(MediaPlayerFragment.PLAY_STATE_CHANGE, MediaPlayerFragment.VALUE_STARTED)
                    startTimer()
                    exoPlayer!!.prepare(mediaSource, true, true)
                    model = streamModel
                }
            }


        }
    }



    private fun playerInit() {
        if (exoPlayer == null) {
            exoPlayer = ExoPlayerFactory.newSimpleInstance(this@PlayService)
            exoPlayer!!.addListener(object : Player.EventListener {
                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                    Log.i(Utils.TAG, "onPlayerStateChanged $playWhenReady  $playbackState")
                    if (playbackState == ExoPlayer.STATE_READY) {
                        val realDurationMillis = exoPlayer!!.duration
                        sendStatus(MediaPlayerFragment.TIME_SET, realDurationMillis)
                    }
                }
            })
        }
    }

    fun sendStatus(statusCode: Int, payload: Long) {
        val intent = Intent(MediaPlayerFragment.INTENT_FILTER_NAME).apply {
            putExtra("state", statusCode)
            putExtra("payload", payload)
        }
        LocalBroadcastManager.getInstance(this@PlayService).sendBroadcast(intent)
    }
    fun sendStatus(statusCode: Int, payload: Int) {
        val intent = Intent(MediaPlayerFragment.INTENT_FILTER_NAME).apply {
            putExtra("state", statusCode)
            putExtra("payload", payload)
        }
        LocalBroadcastManager.getInstance(this@PlayService).sendBroadcast(intent)
    }
}
