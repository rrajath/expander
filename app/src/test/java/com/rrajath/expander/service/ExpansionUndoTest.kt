package com.rrajath.expander.service

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExpansionUndoTest {

    // ---- shouldUndo -------------------------------------------------------

    @Test
    fun `single character removed from a static expansion triggers undo`() {
        assertTrue(
            ExpansionUndo.shouldUndo(
                currentText = "be right bac",
                textBeforeTrigger = "",
                expansion = "be right back"
            )
        )
    }

    @Test
    fun `whole chunk removed from a dynamic expansion still triggers undo`() {
        // Keyboard deleted "-26" in one backspace after "dt " expanded to "2026-08-26".
        assertTrue(
            ExpansionUndo.shouldUndo(
                currentText = "2026-08-",
                textBeforeTrigger = "",
                expansion = "2026-08-26"
            )
        )
    }

    @Test
    fun `deleting all the way back to the leading text triggers undo`() {
        assertTrue(
            ExpansionUndo.shouldUndo(
                currentText = "Meeting on ",
                textBeforeTrigger = "Meeting on ",
                expansion = "2026-08-26"
            )
        )
    }

    @Test
    fun `dynamic expansion with internal spaces and punctuation triggers undo`() {
        assertTrue(
            ExpansionUndo.shouldUndo(
                currentText = "Sent Tuesday, 2026-08-2",
                textBeforeTrigger = "Sent ",
                expansion = "Tuesday, 2026-08-26"
            )
        )
    }

    @Test
    fun `unchanged expanded text does not trigger undo`() {
        assertFalse(
            ExpansionUndo.shouldUndo(
                currentText = "2026-08-26",
                textBeforeTrigger = "",
                expansion = "2026-08-26"
            )
        )
    }

    @Test
    fun `text longer than the expansion does not trigger undo`() {
        assertFalse(
            ExpansionUndo.shouldUndo(
                currentText = "2026-08-26 and more",
                textBeforeTrigger = "",
                expansion = "2026-08-26"
            )
        )
    }

    @Test
    fun `edit that diverges from the expansion does not trigger undo`() {
        assertFalse(
            ExpansionUndo.shouldUndo(
                currentText = "2026-08-XX",
                textBeforeTrigger = "",
                expansion = "2026-08-26"
            )
        )
    }

    @Test
    fun `edit before the leading text does not trigger undo`() {
        assertFalse(
            ExpansionUndo.shouldUndo(
                currentText = "Meeting o2026-08-26",
                textBeforeTrigger = "Meeting on ",
                expansion = "2026-08-26"
            )
        )
    }

    // ---- isHistoryStale -------------------------------------------------

    @Test
    fun `our own set-text echo is not stale`() {
        assertFalse(
            ExpansionUndo.isHistoryStale(
                currentText = "2026-08-26",
                textBeforeTrigger = "",
                expansion = "2026-08-26"
            )
        )
    }

    @Test
    fun `a partial delete into the expansion is not stale`() {
        assertFalse(
            ExpansionUndo.isHistoryStale(
                currentText = "2026-08-",
                textBeforeTrigger = "",
                expansion = "2026-08-26"
            )
        )
    }

    @Test
    fun `typing past the expansion makes the history stale`() {
        assertTrue(
            ExpansionUndo.isHistoryStale(
                currentText = "2026-08-26!",
                textBeforeTrigger = "",
                expansion = "2026-08-26"
            )
        )
    }

    @Test
    fun `editing the expansion into something else makes the history stale`() {
        assertTrue(
            ExpansionUndo.isHistoryStale(
                currentText = "2026-08-2X",
                textBeforeTrigger = "",
                expansion = "2026-08-26"
            )
        )
    }
}
