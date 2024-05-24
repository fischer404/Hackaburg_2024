package com.example.anti_sms_scam

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private val REQUEST_CODE_READ_SMS = 1
    private val smsList = mutableListOf<Message>()
    private lateinit var smsUpdateReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkAndRequestPermissions()

        smsUpdateReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val sender = intent?.getStringExtra("sender") ?: return
                val message = intent.getStringExtra("message") ?: return
                addSms(Message(sender, message))
            }
        }

        registerReceiver(smsUpdateReceiver, IntentFilter("SMS_RECEIVED_ACTION"))
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(smsUpdateReceiver)
    }

    private fun checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS), REQUEST_CODE_READ_SMS)
        } else {
            readSms()
        }
    }

    @SuppressLint("Range")
    private fun readSms() {
        val cursor = contentResolver.query(
            Uri.parse("content://sms/inbox"),
            null,
            null,
            null,
            "date DESC"
        )

        cursor?.use {
            val totalSms = it.count
            if (it.moveToFirst()) {
                for (i in 0 until minOf(10, totalSms)) {
                    val smsBody = it.getString(it.getColumnIndex("body"))
                    val address = it.getString(it.getColumnIndex("address"))
                    val message = Message(sender = address, content = smsBody)
                    smsList.add(message)
                    it.moveToNext()
                }
            }
        }

        updateSmsDisplay()
    }

    fun addSms(message: Message) {
        smsList.add(0, message) // Neue SMS an den Anfang der Liste hinzufügen
        if (smsList.size > 10) {
            smsList.removeAt(smsList.size - 1) // Entferne die älteste SMS, um die Liste auf 10 zu beschränken
        }
        updateSmsDisplay()
    }

    private fun updateSmsDisplay() {
        val textView: TextView = findViewById(R.id.textView)
        textView.text = smsList.joinToString("\n\n") { "From: ${it.sender}\nMessage: ${it.content}\nFlag: ${it.flag}" }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_READ_SMS) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                readSms()
            } else {
                Toast.makeText(this, "Permission denied to read your SMS", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
