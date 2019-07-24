package com.feyzullahefendi.akraarsiv

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class NotificationBroadCastReceiver : BroadcastReceiver() {
    companion object {
        const val DEFAULT = "com.feyzullahefendi.akraarsiv.DEFAULT"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null) {

            val state = intent.getIntExtra("state", -1)
            Log.i(Utils.TAG, "state $state")
            when (state) {
                PlayService.OPEN_APP -> {
                    val mainActivityIntent = Intent(context, MainActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    ContextCompat.startActivity(context!!, mainActivityIntent, null)
                }
                else -> {

                    val localIntent = Intent(PlayService.INTENT_FILTER_NAME)
                    localIntent.apply {
                        putExtra("state", state)
                    }
                    LocalBroadcastManager.getInstance(context!!).sendBroadcast(localIntent)
                }
            }


        }
    }

}