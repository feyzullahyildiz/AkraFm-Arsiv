package com.feyzullahefendi.akraarsiv

import android.media.Image
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatSeekBar
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.audio.AudioListener
import com.google.android.exoplayer2.metadata.Metadata
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Log
import com.google.android.exoplayer2.util.Util
import java.util.*

class MediaPlayerActivity : AppCompatActivity() {
    val TAG = "MediaPlayerActivity"
    var durationSet = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.media_player)
        val model = Utils.streamModelSubject.value
        val seekBar = findViewById<SeekBar>(R.id.media_player_seekbar)
        val exoPlayer = ExoPlayerFactory.newSimpleInstance(this)
        if (model != null) {
            val dataSourceFactory =
                DefaultHttpDataSourceFactory(Util.getUserAgent(this, "app-name"))
            val uri: Uri = Uri.parse(model?.sourceUrl())
            val mediaSource = SsMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
            exoPlayer?.playWhenReady = true
            exoPlayer?.prepare(mediaSource, true, true)
        }
        val currentTimeTextView = findViewById<TextView>(R.id.media_player_current_value)
        val totalTimeTextView = findViewById<TextView>(R.id.media_player_total_value)
        exoPlayer.addListener(object : Player.EventListener {
            override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {
                Log.i(TAG, "onTimelineChanged $reason")
            }

            override fun onLoadingChanged(isLoading: Boolean) {
                Log.i(TAG, "onLoadingChanged: $isLoading")
            }

            override fun onRepeatModeChanged(repeatMode: Int) {
                Log.i(TAG, "onRepeatModeChanged $repeatMode")
            }

            override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {
            }

            override fun onSeekProcessed() {
                Log.i(TAG, "onSeekProcessed")
            }

            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                Log.i(TAG, "onPlayerStateChanged $playWhenReady  $playbackState")
                if (playbackState == ExoPlayer.STATE_READY && !durationSet) {
                    val realDurationMillis = exoPlayer.getDuration()
                    durationSet = true
                    Log.i(TAG, "realDurationMillis $realDurationMillis")
                    Log.i(TAG, "realDurationMillis ${Utils.getTimeFromSeconds(realDurationMillis)}")
                    totalTimeTextView.text = Utils.getTimeFromSeconds(realDurationMillis)
                    seekBar.max = (realDurationMillis / 1000).toInt()

                }

            }

            override fun onPositionDiscontinuity(reason: Int) {
                Log.i(TAG, "onPositionDiscontinuity, $reason")
            }

            override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {
                Log.i(TAG, "onTracksChanged")
            }

            override fun onPlayerError(error: ExoPlaybackException?) {
                Log.i(TAG, "onPlayerError")
            }
        })
        exoPlayer.addAudioListener(object : AudioListener {
            override fun onAudioAttributesChanged(audioAttributes: AudioAttributes?) {
                Log.i(TAG, "onAudioAttributesChanged ")
            }

            override fun onAudioSessionId(audioSessionId: Int) {
                Log.i(TAG, "onAudioSessionId $audioSessionId ")
            }

            override fun onVolumeChanged(volume: Float) {
                Log.i(TAG, "onVolumeChanged $volume")
            }
        })
        exoPlayer.addAnalyticsListener(object : AnalyticsListener {
            override fun onTimelineChanged(eventTime: AnalyticsListener.EventTime?, reason: Int) {
                Log.i(TAG, "onTimelineChanged")
            }

            override fun onMediaPeriodCreated(eventTime: AnalyticsListener.EventTime?) {
                Log.i(TAG, "onMediaPeriodCreated ${eventTime?.realtimeMs}")
            }

            override fun onMetadata(eventTime: AnalyticsListener.EventTime?, metadata: Metadata?) {
                Log.i(TAG, "onMetadata ${eventTime?.realtimeMs}")
            }
        })

        val button = findViewById<ImageButton>(R.id.media_player_start_button)
        button.setOnClickListener {

            exoPlayer.playWhenReady = !exoPlayer.playWhenReady
            exoPlayer.getPlaybackState();
        }
        val timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
//                Log.i(TAG, "A Kiss every 1 seconds")
//                exoPlayer.currentPosition
//                Log.i(TAG, "realDurationMillis ${Utils.getTimeFromSeconds(exoPlayer.currentPosition)}")
                this@MediaPlayerActivity.runOnUiThread {
                    val currentPosition = exoPlayer.currentPosition
                    currentTimeTextView.text = Utils.getTimeFromSeconds(currentPosition)
//                    seekBar.setProgress((currentPosition / 1000).toInt(), false)
                    seekBar.progress = (currentPosition / 1000).toInt()

                }
            }
        }, 0, 1000)
//        timer.cancel()
        var isTouch = false
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                Log.i(TAG, "onProgressChanged $p1 $p2")
                if(p2) {
                exoPlayer.seekTo((p1 * 1000).toLong())
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                Log.i(TAG, "onStartTrackingTouch")
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                Log.i(TAG, "onStopTrackingTouch")
            }
        })
        val seekButton = findViewById<ImageButton>(R.id.media_player_seek_button)
        seekButton.setOnClickListener {
            exoPlayer.seekTo(60 * 1000)
        }


    }

    override fun onStop() {
        super.onStop()

    }
}
