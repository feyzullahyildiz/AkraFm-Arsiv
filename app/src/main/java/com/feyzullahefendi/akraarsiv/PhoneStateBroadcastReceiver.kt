package com.feyzullahefendi.akraarsiv

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telecom.TelecomManager
import android.telephony.TelephonyManager
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class PhoneStateBroadcastReceiver: BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent != null) {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)!!
            if(state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                Log.i("PHONE_STATE", "TelephonyManager.EXTRA_STATE_RINGING")
                val stopIntent = Intent(PlayService.INTENT_FILTER_NAME)
                stopIntent.putExtra("state", PlayService.STOP)
                LocalBroadcastManager.getInstance(context!!).sendBroadcast(stopIntent)
            } else if(state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                Log.i("PHONE_STATE", "TelephonyManager.EXTRA_STATE_OFFHOOK")
            } else if(state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                Log.i("PHONE_STATE", "TelephonyManager.EXTRA_STATE_IDLE")
            } else {
                Log.i("PHONE_STATE", "state patates HOPPALA")
            }
        } else {
            Log.i("PHONE_STATE", "intent null")
        }
//        try {
//            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
//            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
//                Toast.makeText(context, "Ringing State, Toast.LENGTH_SHORT).show();
//            }
//            if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK) {
//                    Toast.makeText(context, "Received State", Toast.LENGTH_SHORT).show();
//                }
//                if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
//                    Toast.makeText(context, "Idle State", Toast.LENGTH_SHORT).show();
//                }
//        } catch (Exception e1) {
//            e1.printStackTrace();
//        }
    }

}