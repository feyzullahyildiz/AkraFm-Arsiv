package com.feyzullahefendi.akraarsiv

import android.app.IntentService
import android.content.Intent
import android.content.Context
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.android.exoplayer2.Player
import android.app.PendingIntent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.Nullable
import com.google.android.exoplayer2.ui.PlayerNotificationManager


private const val ACTION_START = "com.feyzullahefendi.akraarsiv.action.FOO"
private const val ACTION_STOP = "com.feyzullahefendi.akraarsiv.action.STOP"

private const val EXTRA_MODEL = "com.feyzullahefendi.akraarsiv.extra.PARAM1"


class PlayService : IntentService("PlayService") {
    init {
//        PlayService.instance = this
    }

    private var exoPlayer: SimpleExoPlayer? = null
    override fun onHandleIntent(intent: Intent?) {
        playerInit()
        when (intent?.action) {
            ACTION_START -> {
                val model = intent.getSerializableExtra(EXTRA_MODEL) as StreamModel
//                val param2 = intent.getStringExtra(EXTRA_PARAM2)
//                handleActionFoo(param1)
                val dataSourceFactory =
                    DefaultHttpDataSourceFactory(Util.getUserAgent(this, "app-name"))
                val uri: Uri = Uri.parse(model.sourceUrl())
                val mediaSource = SsMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
//                val player = ExoPlayerFactory.newSimpleInstance(this)

                exoPlayer?.playWhenReady = true
                exoPlayer?.prepare(mediaSource, true, true)

                val builder = NotificationCompat.Builder(this, "app-name")
                    .setSmallIcon(R.drawable.exo_icon_play)
                    .setContentTitle("My notification")
                    .setContentText("Much longer text that cannot fit one line...")
                    .setStyle(
                        NotificationCompat.BigTextStyle()
                            .bigText("Much longer text that cannot fit one line...")
                    )
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                NotificationManagerCompat.from(this).notify(14, builder.build())
            }
            ACTION_STOP -> {
                val model = intent.getSerializableExtra(EXTRA_MODEL) as StreamModel
                this.exoPlayer?.stop()
            }
        }
    }

    private fun playerInit() {
        if (this.exoPlayer == null) {
            this.exoPlayer = ExoPlayerFactory.newSimpleInstance(this)
        }
    }


    private fun handleActionFoo(param1: String, param2: String) {

    }

    private fun handleActionBaz(param1: String, param2: String) {

    }

    companion object {
        @JvmStatic
        fun startMusic(context: Context, model: StreamModel) {
            val intent = Intent(context, PlayService::class.java).apply {
                action = ACTION_START
                putExtra(EXTRA_MODEL, model)
            }
            context.startService(intent)

        }

        @JvmStatic
        fun stopMusic(context: Context) {
            val intent = Intent(context, PlayService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }

//        private lateinit var instance: PlayService

    }
}
