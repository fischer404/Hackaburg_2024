package com.example.anti_sms_scam

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.net.Uri

class MainActivity : AppCompatActivity() {

    private val REQUEST_CODE_READ_SMS = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkAndRequestPermissions()
    }

    private fun checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_SMS), REQUEST_CODE_READ_SMS)
        } else {
            readSms()
        }
    }

    @SuppressLint("Range")
    private fun readSms() {
        val smsList = mutableListOf<String>()
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
                    smsList.add("From: $address\nMessage: $smsBody")
                    it.moveToNext()
                }
            }
        }

        // Anzeigen der SMS in einem TextView oder ListView
        val textView: TextView = findViewById(R.id.textView)
        textView.text = smsList.joinToString("\n\n")
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
