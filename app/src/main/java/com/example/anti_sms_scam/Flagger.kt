package com.example.anti_sms_scam


import com.example.anti_sms_scam.Message
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.File

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

    private fun findClosestKey(map: Map<String, String>, target: String): String? {
        var minDistance = Int.MAX_VALUE
        var closestKey = ""
        for (key in map.keys) {
            val distance = levenshteinDistance(key, target)
            if (distance < minDistance) {
                minDistance = distance
                closestKey = key
            }
        }
        if (minDistance.toDouble()/target.length <= 0.3) {
            return map[closestKey]
        }
        return null

    }

    private fun getJson(): Map<String, String> {
        val jsonString = File("scam_example.json").readText()
        val mapper = jacksonObjectMapper().registerModule(KotlinModule())
        return mapper.readValue(jsonString)
    }

    fun flagSMS(msg: Message) {
        val map = getJson()
        val flag = findClosestKey(map, msg.content)
        msg.flag = flag
    }
}