package com.example.anti_sms_scam

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.*
import org.junit.Before

import org.junit.Test

class FlaggerTest {

    lateinit var context: Context
    @Before
    fun setup() {
        // Initialize Robolectric
        val application = ApplicationProvider.getApplicationContext<Application>()
        context = application.applicationContext
    }

    @Test
    fun flagWholeSMS() {
        val msg = Message("xyz", "Your bank account has been compromised. Please verify your personal information by clicking the link below.", null)
        Flagger().flagSMS(context, msg)
        assert(msg.flag == "phishing_scam")
    }

    @Test
    fun flagPartSMS() {
        val msg = Message("xyz", "Your bank account has been. Please verify your personal information by clicking the link below.", null)
        Flagger().flagSMS(context, msg)
        assert(msg.flag == "phishing_scam")
    }

    @Test
    fun flagFalseSMS() {
        val msg = Message("xyz", "Hi, Mom I'm coming home late today", null)
        Flagger().flagSMS(context, msg)
        assert(msg.flag == null)
    }
}