package com.rrajath.expander.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ProcessTextHelperTest {

    private val processTextAction = "android.intent.action.PROCESS_TEXT"

    @Test
    fun `process text action with text returns the text`() {
        val result = ProcessTextHelper.extractSelectedText(processTextAction, "Hello world")
        assertEquals("Hello world", result)
    }

    @Test
    fun `text with surrounding whitespace is returned verbatim`() {
        val result = ProcessTextHelper.extractSelectedText(processTextAction, "  padded text  ")
        assertEquals("  padded text  ", result)
    }

    @Test
    fun `null action returns null`() {
        assertNull(ProcessTextHelper.extractSelectedText(null, "Hello"))
    }

    @Test
    fun `wrong action returns null`() {
        assertNull(ProcessTextHelper.extractSelectedText("android.intent.action.SEND", "Hello"))
    }

    @Test
    fun `null text returns null`() {
        assertNull(ProcessTextHelper.extractSelectedText(processTextAction, null))
    }

    @Test
    fun `empty text returns null`() {
        assertNull(ProcessTextHelper.extractSelectedText(processTextAction, ""))
    }

    @Test
    fun `whitespace-only text returns null`() {
        assertNull(ProcessTextHelper.extractSelectedText(processTextAction, "   \n\t "))
    }

    @Test
    fun `multiline text is preserved`() {
        val text = "line one\nline two\n\nline four"
        val result = ProcessTextHelper.extractSelectedText(processTextAction, text)
        assertEquals(text, result)
    }

    @Test
    fun `non-string CharSequence is converted to String`() {
        val builder = StringBuilder("built text")
        val result = ProcessTextHelper.extractSelectedText(processTextAction, builder)
        assertEquals("built text", result)
    }
}
