package com.example.anti_sms_scam

import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import android.content.Context

data class PhishingScam(
    val flag: String,
    val messages: List<String>,
    val precaution: String,
    val explanation: String
)

class Flagger {
    private fun levenshteinDistance(s1: String, s2: String): Int {
        val len1 = s1.length
        val len2 = s2.length
        val dp = Array(len1 + 1) { IntArray(len2 + 1) }

        for (i in 0..len1) {
            for (j in 0..len2) {
                if (i == 0) {
                    dp[i][j] = j
                } else if (j == 0) {
                    dp[i][j] = i
                } else {
                    val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
                    dp[i][j] = minOf(
                        dp[i - 1][j] + 1,     // Deletion
                        dp[i][j - 1] + 1,     // Insertion
                        dp[i - 1][j - 1] + cost // Substitution
                    )
                }
            }
        }
        return dp[len1][len2]
    }

    private fun findClosestKey(context: Context, target: String): Triple<String?, String?, String?> {

        val jsonString = context.assets.open("scam_example.json").bufferedReader().use { it.readText() }
        val mapper = jacksonObjectMapper().registerModule(KotlinModule())
        val json : List<PhishingScam> =  mapper.readValue(jsonString)


        var minDistance = Int.MAX_VALUE
        var closestFlag: String? = null
        var closestExplanation: String? = null
        var closestPrecautions: String? = null


        for (type in json) {
            for (msg in type.messages) {
                val distance = levenshteinDistance(msg, target)
                if (distance < minDistance) {
                    minDistance = distance
                    closestFlag = type.flag
                    closestExplanation = type.explanation
                    closestPrecautions = type.precaution
                }
            }
        }
        if (minDistance.toDouble()/target.length <= 0.3) {
            return Triple(closestFlag, closestExplanation, closestPrecautions)
        }
        return Triple(null, null, null)

    }

    fun flagSMS(context: Context, msg: Message) {
        val (flag, explanation, precautions) = findClosestKey(context, msg.content)
        msg.flag = flag
        msg.scamExplanation = explanation
        msg.precautions = precautions
    }
}