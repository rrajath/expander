package com.rrajath.expander.service

import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.*

object SnippetProcessor {

    private val dynamicPlaceholderRegex = Regex("""\{\{([^}]+)\}\}""")

    private val placeholderPattern = Regex(
        """^(date|time|datetime|day|day_long|month|month_long|year|year_short|week_num)(?:([+-]\d+)([dwmy]))?(?::(.+))?$"""
    )

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
     * - {{year_short}} - Two-digit year (e.g., 26)
     * - {{week_num}} - Week number (e.g., 3)
     * - {{date:format}} - Custom date format (e.g., {{date:dd/MM/yyyy}})
     * - {{time:format}} - Custom time format (e.g., {{time:hh:mm a}})
     *
     * Any of the placeholders above can be offset by appending a signed amount and a
     * unit letter right after the placeholder name, before the optional format:
     * - d = days, w = weeks, m = months, y = years
     * e.g. {{date+3d}}, {{date-2w}}, {{month_long+1m}}, {{date+1y:dd/MM/yyyy}}
     */
    fun process(expansion: String): String {
        var processed = expansion

        dynamicPlaceholderRegex.findAll(expansion).forEach { matchResult ->
            val placeholder = matchResult.value
            val content = matchResult.groupValues[1].trim()
            val match = placeholderPattern.matchEntire(content)

            val replacement = if (match == null) {
                placeholder // Leave unknown/invalid placeholders as-is
            } else {
                val (base, offsetSign, offsetUnit, format) = match.destructured
                val baseDate = if (offsetUnit.isNotEmpty()) {
                    applyOffset(Date(), offsetSign.toInt(), offsetUnit)
                } else {
                    Date()
                }

                when {
                    base == "date" && format.isNotEmpty() -> formatDate(baseDate, format, placeholder)
                    base == "date" -> formatDate(baseDate, "yyyy-MM-dd", placeholder)
                    base == "time" && format.isNotEmpty() -> formatDate(baseDate, format, placeholder)
                    base == "time" -> formatDate(baseDate, "HH:mm:ss", placeholder)
                    base == "datetime" && format.isEmpty() -> formatDate(baseDate, "yyyy-MM-dd HH:mm:ss", placeholder)
                    base == "day" && format.isEmpty() -> formatDate(baseDate, "EEE", placeholder)
                    base == "day_long" && format.isEmpty() -> formatDate(baseDate, "EEEE", placeholder)
                    base == "month" && format.isEmpty() -> formatDate(baseDate, "MMM", placeholder)
                    base == "month_long" && format.isEmpty() -> formatDate(baseDate, "MMMM", placeholder)
                    base == "year" && format.isEmpty() -> formatDate(baseDate, "yyyy", placeholder)
                    base == "year_short" && format.isEmpty() -> formatDate(baseDate, "yy", placeholder)
                    base == "week_num" && format.isEmpty() -> formatDate(baseDate, "w", placeholder)
                    else -> placeholder // format specified on a placeholder that doesn't support one
                }
            }

            processed = processed.replace(placeholder, replacement)
        }

        return processed
    }

    private fun applyOffset(date: Date, amount: Int, unit: String): Date {
        val zonedDateTime = date.toInstant().atZone(ZoneId.systemDefault())
        val adjusted = when (unit) {
            "d" -> zonedDateTime.plusDays(amount.toLong())
            "w" -> zonedDateTime.plusWeeks(amount.toLong())
            "m" -> zonedDateTime.plusMonths(amount.toLong())
            "y" -> zonedDateTime.plusYears(amount.toLong())
            else -> zonedDateTime
        }
        return Date.from(adjusted.toInstant())
    }

    private fun formatDate(date: Date, format: String, fallback: String): String {
        return try {
            SimpleDateFormat(format, Locale.getDefault()).format(date)
        } catch (e: Exception) {
            fallback // Return original placeholder if format is invalid
        }
    }
}
