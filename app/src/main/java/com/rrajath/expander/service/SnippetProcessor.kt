package com.rrajath.expander.service

import java.text.SimpleDateFormat
import java.util.*

object SnippetProcessor {

    private val dynamicPlaceholderRegex = Regex("""\{\{([^}]+)\}\}""")

    /**
     * Processes a snippet expansion, replacing dynamic placeholders with actual values.
     *
     * Supported placeholders:
     * - {{date}} - Current date in yyyy-MM-dd format
     * - {{time}} - Current time in HH:mm:ss format
     * - {{datetime}} - Current date and time in yyyy-MM-dd HH:mm:ss format
     * - {{day}} - Day of week (short form, e.g., Mon, Tue)
     * - {{day_long}} - Day of week (long form, e.g., Monday, Tuesday)
     * - {{month}} - Month (short form, e.g., Jan, Feb)
     * - {{month_long}} - Month (long form, e.g., January, February)
     * - {{year}} - Full year (e.g., 2026)
     * - {{date:format}} - Custom date format (e.g., {{date:dd/MM/yyyy}})
     * - {{time:format}} - Custom time format (e.g., {{time:hh:mm a}})
     */
    fun process(expansion: String): String {
        var processed = expansion

        dynamicPlaceholderRegex.findAll(expansion).forEach { matchResult ->
            val placeholder = matchResult.value
            val content = matchResult.groupValues[1].trim()

            val replacement = when {
                content == "date" -> getCurrentDate()
                content == "time" -> getCurrentTime()
                content == "datetime" -> getCurrentDateTime()
                content == "day" -> getDayShort()
                content == "day_long" -> getDayLong()
                content == "month" -> getMonthShort()
                content == "month_long" -> getMonthLong()
                content == "year" -> getYear()
                content.startsWith("date:") -> {
                    val format = content.substring(5).trim()
                    formatCurrentDate(format)
                }
                content.startsWith("time:") -> {
                    val format = content.substring(5).trim()
                    formatCurrentTime(format)
                }
                else -> placeholder // Leave unknown placeholders as-is
            }

            processed = processed.replace(placeholder, replacement)
        }

        return processed
    }

    private fun getCurrentDate(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    private fun getCurrentTime(): String {
        return SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
    }

    private fun getCurrentDateTime(): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
    }

    private fun formatCurrentDate(format: String): String {
        return try {
            SimpleDateFormat(format, Locale.getDefault()).format(Date())
        } catch (e: Exception) {
            "{{date:$format}}" // Return original if format is invalid
        }
    }

    private fun formatCurrentTime(format: String): String {
        return try {
            SimpleDateFormat(format, Locale.getDefault()).format(Date())
        } catch (e: Exception) {
            "{{time:$format}}" // Return original if format is invalid
        }
    }

    private fun getDayShort(): String {
        return SimpleDateFormat("EEE", Locale.getDefault()).format(Date())
    }

    private fun getDayLong(): String {
        return SimpleDateFormat("EEEE", Locale.getDefault()).format(Date())
    }

    private fun getMonthShort(): String {
        return SimpleDateFormat("MMM", Locale.getDefault()).format(Date())
    }

    private fun getMonthLong(): String {
        return SimpleDateFormat("MMMM", Locale.getDefault()).format(Date())
    }

    private fun getYear(): String {
        return SimpleDateFormat("yyyy", Locale.getDefault()).format(Date())
    }
}
