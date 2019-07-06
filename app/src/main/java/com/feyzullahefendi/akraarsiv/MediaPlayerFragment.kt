package com.feyzullahefendi.akraarsiv

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import io.reactivex.disposables.Disposable
import org.reactivestreams.Subscription
import androidx.localbroadcastmanager.content.LocalBroadcastManager


class MediaPlayerFragment : Fragment() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        val instance = MediaPlayerFragment()
        const val INTENT_FILTER_NAME = "media-player-fragment"
        const val SEEKBAR_UPDATE = 1
        const val TIME_SET = 2
    }

    var totalTime: Long = 0
    lateinit var playButton: Button
    lateinit var forwardButton: Button
    lateinit var backwardButton: Button
    lateinit var seekbar: SeekBar
    lateinit var titleTimeTextView: TextView
    lateinit var totalTimeTextView: TextView
    lateinit var currentTimeTextView: TextView
    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent!!.getIntExtra("state", -1)) {
                SEEKBAR_UPDATE -> {
                    val time = intent.getLongExtra("payload", 0)
                    if (totalTime > 0.toLong()) {
                        val timeString = Utils.getTimeFromSeconds(time)
                        currentTimeTextView.text = timeString
                        seekbar.progress = (time / 1000).toInt()
//                        Log.i(Utils.TAG, "MediaPlayerFragment onReceive $timeString")
                    }
                }
                TIME_SET -> {
//                    Log.i(Utils.TAG, "MediaPlayerFragment onReceive TIME_SET")
                    val time = intent.getLongExtra("payload", 0)
                    val timeString = Utils.getTimeFromSeconds(time)
                    totalTimeTextView.text = timeString
//                    Log.i(Utils.TAG, "timeString $timeString")
//                    Log.i(Utils.TAG, "TIME_SET $time")
                    seekbar.max = (time / 1000).toInt()
                    totalTime = time
                }
                -1 -> {
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LocalBroadcastManager.getInstance(activity!!).registerReceiver(
            broadcastReceiver, IntentFilter(INTENT_FILTER_NAME)
        )
//        Log.i(Utils.TAG, "MediaPlayerFragment onCreate")
    }

    lateinit var subscription: Disposable
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.layout_media_player, container, false)

        playButton = view.findViewById<Button>(R.id.layout_media_player_play_button)
        playButton.setOnClickListener {
            changeStatus(PlayService.PLAY)
        }
        backwardButton = view.findViewById<Button>(R.id.layout_media_player_backward_button)
        backwardButton.setOnClickListener {
            changeStatus(PlayService.BACKWARD)
        }
        forwardButton = view.findViewById<Button>(R.id.layout_media_player_forward_button)
        forwardButton.setOnClickListener {
            changeStatus(PlayService.FORWARD)
        }
        seekbar = view.findViewById<SeekBar>(R.id.layout_media_player_seekbar)
        totalTimeTextView = view.findViewById<TextView>(R.id.layout_media_player_total_time_text_view)
        currentTimeTextView = view.findViewById<TextView>(R.id.layout_media_player_current_time_text_view)
        titleTimeTextView = view.findViewById(R.id.layout_media_player_title_text_view)

        subscription = Utils.streamModelSubject.subscribe {
            Log.i(Utils.TAG, "MediaPlayerFragment send")
            val intent = Intent(PlayService.INTENT_FILTER_NAME)
            intent.putExtra("model", it)
            LocalBroadcastManager.getInstance(activity!!).sendBroadcast(intent)
            titleTimeTextView.text = it.name
        }
        seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, value: Int, isTouched: Boolean) {
                if(isTouched) {
                    Log.i(Utils.TAG, "VALUE $value")
                    changeStatus(PlayService.SEEK_TO, (value * 1000).toLong())
                }
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}

        })
        return view
    }

    fun changeStatus(state: Int, payload: String) {
        val intent = Intent(PlayService.INTENT_FILTER_NAME)
        intent.putExtra("state", state)
        intent.putExtra("payload", payload)
        LocalBroadcastManager.getInstance(activity!!).sendBroadcast(intent)
    }
    fun changeStatus(state: Int, payload: Long) {
        val intent = Intent(PlayService.INTENT_FILTER_NAME)
        intent.putExtra("state", state)
        intent.putExtra("payload", payload)
        LocalBroadcastManager.getInstance(activity!!).sendBroadcast(intent)
    }

    fun changeStatus(state: Int) {
        val intent = Intent(PlayService.INTENT_FILTER_NAME)
        intent.putExtra("state", state)
        LocalBroadcastManager.getInstance(activity!!).sendBroadcast(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(activity!!).unregisterReceiver(broadcastReceiver)
    }
}