package com.feyzullahefendi.akraarsiv

import android.app.*
import android.content.BroadcastReceiver
import android.content.Intent
import android.content.Context
import android.content.IntentFilter
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.android.exoplayer2.Player
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.annotation.Nullable
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.getSystemService
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
    }

    var seekBarTimer: Timer? = null
    lateinit var handler: Handler
    override fun onCreate() {
        super.onCreate()
        Log.i(Utils.TAG, "PlayService onCreate")
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, IntentFilter(INTENT_FILTER_NAME))
        seekBarTimer = Timer()
        handler = Handler()
    }
    fun startTimer() {
        if(seekBarTimer == null) {
            seekBarTimer = Timer()
        }
        seekBarTimer?.purge()
        seekBarTimer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                handler.post {
                    val currentPosition = exoPlayer?.currentPosition
                    val intent = Intent(MediaPlayerFragment.INTENT_FILTER_NAME)
                    intent.putExtra("state", MediaPlayerFragment.SEEKBAR_UPDATE)
                    intent.putExtra("payload", currentPosition)
                    LocalBroadcastManager.getInstance(this@PlayService).sendBroadcast(intent)
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
                    exoPlayer?.playWhenReady = !exoPlayer?.playWhenReady!!
                    if(exoPlayer?.playWhenReady!!) {
                       startTimer()
                    }else {
                        stopTimer()
                    }
                }
                BACKWARD -> {
                    val duration = exoPlayer?.currentPosition
                    if (duration != null && duration > 5000) {
                        exoPlayer?.seekTo(duration.minus(5000))
                    } else {
                        exoPlayer?.seekTo(0)

                    }

                }
                FORWARD -> {
                    val duration = exoPlayer?.currentPosition
                    val totalLen = exoPlayer?.duration
                    if (duration != null && totalLen != null) {
                        if (totalLen > duration.plus(5000)) {
                            exoPlayer?.seekTo(duration.plus(5000))
                        }
                    }
                }
                SEEK_TO -> {
                    val seekValue = intent?.getLongExtra("payload", -1)
                    exoPlayer?.seekTo(seekValue)
                }
                -1 -> {
                    val model = intent?.getSerializableExtra("model") as StreamModel
                    val dataSourceFactory =
                        DefaultHttpDataSourceFactory(Util.getUserAgent(this@PlayService, "akra-arsiv"))
                    val uri: Uri = Uri.parse(model.sourceUrl())
                    val mediaSource = SsMediaSource.Factory(dataSourceFactory).createMediaSource(uri)

                    exoPlayer?.playWhenReady = true
                    startTimer()
                    exoPlayer?.prepare(mediaSource, true, true)
                }
            }


        }
    }

    private var exoPlayer: SimpleExoPlayer? = null

    private fun playerInit() {
        if (this.exoPlayer == null) {
            this@PlayService.exoPlayer = ExoPlayerFactory.newSimpleInstance(this@PlayService)
            this@PlayService.exoPlayer?.addListener(object : Player.EventListener {
                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                    Log.i(Utils.TAG, "onPlayerStateChanged $playWhenReady  $playbackState")
                    if (playbackState == ExoPlayer.STATE_READY) {
                        val realDurationMillis = this@PlayService.exoPlayer?.duration
                        Log.i(Utils.TAG, "realDurationMillis $realDurationMillis")
                        sendStatus(MediaPlayerFragment.TIME_SET, realDurationMillis!!)
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
}
