package com.example.anti_sms_scam

data class Message(
    val sender: String,
    val content: String,
    var flag: String? = null,
    var scamExplanation: String? = null,  // Betrugsmasche
    var precautions: String? = null       // Vorsichtsmaßnahmen
)
