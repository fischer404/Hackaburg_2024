package com.example.anti_sms_scam

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.telephony.SmsMessage
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class SmsReceiver : BroadcastReceiver() {
    private val flagger = Flagger()

    private fun showNotification(context: Context, sender: String, message: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(context, "SMS_RECEIVED_CHANNEL")
            .setSmallIcon(R.drawable.ic_sms_notification)
            .setContentTitle("Alert! Scam Message Detected!")
            .setContentText("Click here for more information.")
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(1, builder.build())
        }
    }
    override fun onReceive(context: Context, intent: Intent) {
        val bundle = intent.extras
        if (bundle != null) {
            val pdus = bundle.get("pdus") as Array<*>
            for (pdu in pdus) {
                val smsMessage = SmsMessage.createFromPdu(pdu as ByteArray)
                val sender = smsMessage.displayOriginatingAddress
                val messageBody = smsMessage.messageBody
                val msg =Message(sender, messageBody)
                flagger.flagSMS(context, msg)
                if (msg.flag != null) {
                    showNotification(context, sender, messageBody)
                }
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
