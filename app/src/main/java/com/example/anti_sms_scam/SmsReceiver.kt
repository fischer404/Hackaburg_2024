package com.example.anti_sms_scam

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telephony.SmsMessage
import android.util.Log

class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val bundle = intent.extras
        if (bundle != null) {
            val pdus = bundle.get("pdus") as Array<*>
            for (pdu in pdus) {
                val smsMessage = SmsMessage.createFromPdu(pdu as ByteArray)
                val sender = smsMessage.displayOriginatingAddress
                val messageBody = smsMessage.messageBody

                Log.d("SmsReceiver", "From: $sender, Message: $messageBody")

                // Sende einen Broadcast mit den SMS-Daten
                val smsIntent = Intent("SMS_RECEIVED_ACTION")
                smsIntent.putExtra("sender", sender)
                smsIntent.putExtra("message", messageBody)
                context.sendBroadcast(smsIntent)
            }
        }
    }
}
