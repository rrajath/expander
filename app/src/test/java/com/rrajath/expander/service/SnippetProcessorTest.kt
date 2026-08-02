package com.rrajath.expander.service

import org.junit.Assert.assertEquals
import org.junit.Test
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.*

class SnippetProcessorTest {

    private fun expectedDate(format: String, offsetAmount: Long = 0, unit: ChronoField = ChronoField.DAYS): String {
        val zoned = Date().toInstant().atZone(ZoneId.systemDefault())
        val adjusted = when (unit) {
            ChronoField.DAYS -> zoned.plusDays(offsetAmount)
            ChronoField.WEEKS -> zoned.plusWeeks(offsetAmount)
            ChronoField.MONTHS -> zoned.plusMonths(offsetAmount)
            ChronoField.YEARS -> zoned.plusYears(offsetAmount)
        }
        return SimpleDateFormat(format, Locale.getDefault()).format(Date.from(adjusted.toInstant()))
    }

    private enum class ChronoField { DAYS, WEEKS, MONTHS, YEARS }

    @Test
    fun `plain date placeholder is unaffected`() {
        val result = SnippetProcessor.process("Today is {{date}}")
        assertEquals("Today is ${expectedDate("yyyy-MM-dd")}", result)
    }

    @Test
    fun `date plus days`() {
        val result = SnippetProcessor.process("{{date+3d}}")
        assertEquals(expectedDate("yyyy-MM-dd", 3, ChronoField.DAYS), result)
    }

    @Test
    fun `date minus weeks`() {
        val result = SnippetProcessor.process("{{date-2w}}")
        assertEquals(expectedDate("yyyy-MM-dd", -2, ChronoField.WEEKS), result)
    }

    @Test
    fun `date plus months with custom format`() {
        val result = SnippetProcessor.process("{{date+1m:dd/MM/yyyy}}")
        assertEquals(expectedDate("dd/MM/yyyy", 1, ChronoField.MONTHS), result)
    }

    @Test
    fun `date plus years`() {
        val result = SnippetProcessor.process("{{date+1y}}")
        assertEquals(expectedDate("yyyy-MM-dd", 1, ChronoField.YEARS), result)
    }

    @Test
    fun `month_long placeholder supports offset`() {
        val result = SnippetProcessor.process("{{month_long+1m}}")
        assertEquals(expectedDate("MMMM", 1, ChronoField.MONTHS), result)
    }

    @Test
    fun `day_long placeholder supports offset`() {
        val result = SnippetProcessor.process("{{day_long+7d}}")
        assertEquals(expectedDate("EEEE", 7, ChronoField.DAYS), result)
    }

    @Test
    fun `year_short placeholder supports offset`() {
        val result = SnippetProcessor.process("{{year_short-1y}}")
        assertEquals(expectedDate("yy", -1, ChronoField.YEARS), result)
    }

    @Test
    fun `datetime placeholder supports offset`() {
        val result = SnippetProcessor.process("{{datetime+1d}}")
        assertEquals(expectedDate("yyyy-MM-dd HH:mm:ss", 1, ChronoField.DAYS), result)
    }

    @Test
    fun `invalid unit is left unchanged`() {
        val result = SnippetProcessor.process("{{date+3x}}")
        assertEquals("{{date+3x}}", result)
    }

    @Test
    fun `missing unit is left unchanged`() {
        val result = SnippetProcessor.process("{{date+3}}")
        assertEquals("{{date+3}}", result)
    }

    @Test
    fun `format on placeholder that does not support format is left unchanged`() {
        val result = SnippetProcessor.process("{{day_long:custom}}")
        assertEquals("{{day_long:custom}}", result)
    }

    @Test
    fun `unknown placeholder is left unchanged`() {
        val result = SnippetProcessor.process("{{unknown}}")
        assertEquals("{{unknown}}", result)
    }

    @Test
    fun `multiple placeholders in one expansion`() {
        val result = SnippetProcessor.process("Due {{date+3d}}, was {{date-1w}}")
        assertEquals(
            "Due ${expectedDate("yyyy-MM-dd", 3, ChronoField.DAYS)}, was ${expectedDate("yyyy-MM-dd", -1, ChronoField.WEEKS)}",
            result
        )
    }
}
