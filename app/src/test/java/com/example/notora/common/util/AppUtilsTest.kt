package com.example.notora.common.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class AppUtilsTest {

    @Test
    fun testStringUtils_wordCount() {
        assertEquals(0, AppUtils.string.countWords(""))
        assertEquals(0, AppUtils.string.countWords("   "))
        assertEquals(3, AppUtils.string.countWords("Hello world test"))
        assertEquals(4, AppUtils.string.countWords("   Multiple   spaces   between words  "))
    }

    @Test
    fun testStringUtils_characterCount() {
        assertEquals(5, AppUtils.string.countCharacters("hello", includeSpaces = true))
        assertEquals(5, AppUtils.string.countCharacters("h e l l o", includeSpaces = false))
        assertEquals(9, AppUtils.string.countCharacters("h e l l o", includeSpaces = true))
    }

    @Test
    fun testStringUtils_readingTime() {
        assertEquals(0, AppUtils.string.calculateReadingTimeMinutes(0))
        assertEquals(1, AppUtils.string.calculateReadingTimeMinutes(50))
        assertEquals(1, AppUtils.string.calculateReadingTimeMinutes(200))
        assertEquals(2, AppUtils.string.calculateReadingTimeMinutes(201))
    }

    @Test
    fun testStringUtils_cleanTagName() {
        assertEquals("work", AppUtils.string.cleanTagName("#work"))
        assertEquals("ideas", AppUtils.string.cleanTagName("  #ideas  "))
        assertEquals("project", AppUtils.string.cleanTagName("project"))
    }

    @Test
    fun testStringUtils_truncate() {
        assertEquals("Hello", AppUtils.string.truncate("Hello", 10))
        assertEquals("Hello...", AppUtils.string.truncate("Hello World", 5))
    }

    @Test
    fun testValidationUtils_isValidTitle() {
        assertFalse(AppUtils.validation.isValidTitle(null))
        assertFalse(AppUtils.validation.isValidTitle(""))
        assertFalse(AppUtils.validation.isValidTitle("   "))
        assertTrue(AppUtils.validation.isValidTitle("Valid Note Title"))
    }

    @Test
    fun testValidationUtils_isValidTagName() {
        assertFalse(AppUtils.validation.isValidTagName(null))
        assertFalse(AppUtils.validation.isValidTagName(""))
        assertFalse(AppUtils.validation.isValidTagName("tag with space"))
        assertFalse(AppUtils.validation.isValidTagName("tag!special"))
        assertTrue(AppUtils.validation.isValidTagName("project123"))
        assertTrue(AppUtils.validation.isValidTagName("work_task"))
        assertTrue(AppUtils.validation.isValidTagName("clean-code"))
    }

    @Test
    fun testValidationUtils_isValidHexColor() {
        assertTrue(AppUtils.validation.isValidHexColor("#FFF"))
        assertTrue(AppUtils.validation.isValidHexColor("#6366F1"))
        assertTrue(AppUtils.validation.isValidHexColor("6366F1"))
        assertTrue(AppUtils.validation.isValidHexColor("#806366F1"))
        assertFalse(AppUtils.validation.isValidHexColor(null))
        assertFalse(AppUtils.validation.isValidHexColor(""))
        assertFalse(AppUtils.validation.isValidHexColor("#GGGGGG"))
    }

    @Test
    fun testValidationUtils_isValidUrl() {
        assertTrue(AppUtils.validation.isValidUrl("https://google.com"))
        assertTrue(AppUtils.validation.isValidUrl("http://localhost:8080"))
        assertFalse(AppUtils.validation.isValidUrl(null))
        assertFalse(AppUtils.validation.isValidUrl("not a url"))
    }

    @Test
    fun testDateTimeUtils_formatDayKey() {
        val calendar = Calendar.getInstance()
        calendar.set(2026, Calendar.JANUARY, 15)
        val timestamp = calendar.timeInMillis
        assertEquals("20260115", AppUtils.dateTime.formatDayKey(timestamp))
    }

    @Test
    fun testDateTimeUtils_relativeTime() {
        val now = System.currentTimeMillis()
        assertEquals("Just now", AppUtils.dateTime.formatRelativeTime(now - 10_000))
        assertEquals("5m ago", AppUtils.dateTime.formatRelativeTime(now - 5 * 60 * 1000))
        assertEquals("2h ago", AppUtils.dateTime.formatRelativeTime(now - 2 * 60 * 60 * 1000))
    }

    @Test
    fun testUiUtils_gridColumnCount() {
        assertEquals(2, AppUtils.ui.getGridColumnCount(isLandscape = false, portraitColumns = 2, landscapeColumns = 3))
        assertEquals(3, AppUtils.ui.getGridColumnCount(isLandscape = true, portraitColumns = 2, landscapeColumns = 3))
    }
}
