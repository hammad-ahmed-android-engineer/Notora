package com.example.notora

import com.example.notora.domain.model.Note
import com.example.notora.domain.model.NoteSortOption
import com.example.notora.domain.util.NoteDateGrouper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.Locale

class NoteDateGrouperTest {

    private val fixedZone = ZoneId.of("UTC")
    // Base test date: Wednesday, October 21, 2026, 12:00:00 UTC
    private val baseDate = LocalDate.of(2026, 10, 21)
    private val baseInstant = baseDate.atTime(12, 0).toInstant(ZoneOffset.UTC)
    private val fixedClock = Clock.fixed(baseInstant, fixedZone)

    private fun createNote(id: Long, date: LocalDate, hour: Int = 12): Note {
        val timestamp = date.atTime(hour, 0).toInstant(ZoneOffset.UTC).toEpochMilli()
        return Note(
            id = id,
            title = "Note $id",
            createdAt = timestamp,
            updatedAt = timestamp
        )
    }

    @Test
    fun testEmptyNotesListReturnsEmptyGroups() {
        val groups = NoteDateGrouper.groupNotes(
            notes = emptyList(),
            sortOption = NoteSortOption.MODIFIED_DESC,
            zoneId = fixedZone,
            clock = fixedClock,
            locale = Locale.US
        )
        assertTrue(groups.isEmpty())
    }

    @Test
    fun testTodayAndYesterdayClassification() {
        val todayNote = createNote(1, baseDate)
        val yesterdayNote = createNote(2, baseDate.minusDays(1))

        val groups = NoteDateGrouper.groupNotes(
            notes = listOf(todayNote, yesterdayNote),
            sortOption = NoteSortOption.MODIFIED_DESC,
            zoneId = fixedZone,
            clock = fixedClock,
            locale = Locale.US
        )

        assertEquals(2, groups.size)
        assertEquals("today", groups[0].id)
        assertEquals("Today", groups[0].title)
        assertEquals(listOf(todayNote), groups[0].notes)

        assertEquals("yesterday", groups[1].id)
        assertEquals("Yesterday", groups[1].title)
        assertEquals(listOf(yesterdayNote), groups[1].notes)
    }

    @Test
    fun testEarlierThisWeekAndLastWeek() {
        // baseDate is Wednesday Oct 21, 2026.
        // Locale.US week starts Sunday Oct 18.
        // Sunday Oct 18 is earlier this week. Monday Oct 19 is earlier this week. Tuesday Oct 20 is yesterday.
        val earlierThisWeekNote = createNote(3, LocalDate.of(2026, 10, 19)) // Monday
        val lastWeekNote = createNote(4, LocalDate.of(2026, 10, 14)) // Wednesday of previous week

        val groups = NoteDateGrouper.groupNotes(
            notes = listOf(earlierThisWeekNote, lastWeekNote),
            sortOption = NoteSortOption.MODIFIED_DESC,
            zoneId = fixedZone,
            clock = fixedClock,
            locale = Locale.US
        )

        assertEquals(2, groups.size)
        assertEquals("earlier_this_week", groups[0].id)
        assertEquals("Earlier This Week", groups[0].title)
        assertEquals(listOf(earlierThisWeekNote), groups[0].notes)

        assertEquals("last_week", groups[1].id)
        assertEquals("Last Week", groups[1].title)
        assertEquals(listOf(lastWeekNote), groups[1].notes)
    }

    @Test
    fun testEarlierThisMonthAndLastMonth() {
        // Base is Oct 21, 2026.
        // Oct 2, 2026 is earlier this month (before last week).
        val earlierThisMonthNote = createNote(5, LocalDate.of(2026, 10, 2))
        // Sept 15, 2026 is last month.
        val lastMonthNote = createNote(6, LocalDate.of(2026, 9, 15))

        val groups = NoteDateGrouper.groupNotes(
            notes = listOf(earlierThisMonthNote, lastMonthNote),
            sortOption = NoteSortOption.MODIFIED_DESC,
            zoneId = fixedZone,
            clock = fixedClock,
            locale = Locale.US
        )

        assertEquals(2, groups.size)
        assertEquals("earlier_this_month", groups[0].id)
        assertEquals("Earlier This Month", groups[0].title)
        assertEquals(listOf(earlierThisMonthNote), groups[0].notes)

        assertEquals("last_month", groups[1].id)
        assertEquals("Last Month", groups[1].title)
        assertEquals(listOf(lastMonthNote), groups[1].notes)
    }

    @Test
    fun testLast6MonthsAndLastYearAndOlderYears() {
        // Base is Oct 21, 2026.
        // June 2026 is within last 6 months.
        val last6MonthsNote = createNote(7, LocalDate.of(2026, 6, 10))
        // 2025 is Last Year.
        val lastYearNote = createNote(8, LocalDate.of(2025, 8, 20))
        // 2024 is older year.
        val year2024Note = createNote(9, LocalDate.of(2024, 3, 15))
        // 2023 is older year.
        val year2023Note = createNote(10, LocalDate.of(2023, 11, 5))

        val groups = NoteDateGrouper.groupNotes(
            notes = listOf(last6MonthsNote, lastYearNote, year2024Note, year2023Note),
            sortOption = NoteSortOption.MODIFIED_DESC,
            zoneId = fixedZone,
            clock = fixedClock,
            locale = Locale.US
        )

        assertEquals(4, groups.size)
        assertEquals("last_6_months", groups[0].id)
        assertEquals("Last 6 Months", groups[0].title)

        assertEquals("last_year", groups[1].id)
        assertEquals("Last Year", groups[1].title)

        assertEquals("year_2024", groups[2].id)
        assertEquals("2024", groups[2].title)

        assertEquals("year_2023", groups[3].id)
        assertEquals("2023", groups[3].title)
    }

    @Test
    fun testNoEmptyGroupsGenerated() {
        val todayNote = createNote(1, baseDate)
        val oldNote = createNote(2, LocalDate.of(2022, 5, 10))

        val groups = NoteDateGrouper.groupNotes(
            notes = listOf(todayNote, oldNote),
            sortOption = NoteSortOption.MODIFIED_DESC,
            zoneId = fixedZone,
            clock = fixedClock,
            locale = Locale.US
        )

        // Only Today and 2022 should exist, no empty "Yesterday" or "Last Month"
        assertEquals(2, groups.size)
        assertEquals("Today", groups[0].title)
        assertEquals("2022", groups[1].title)
    }

    @Test
    fun testAscendingSortReversesGroupOrder() {
        val todayNote = createNote(1, baseDate)
        val yesterdayNote = createNote(2, baseDate.minusDays(1))
        val oldNote = createNote(3, LocalDate.of(2023, 1, 1))

        val groups = NoteDateGrouper.groupNotes(
            notes = listOf(oldNote, yesterdayNote, todayNote),
            sortOption = NoteSortOption.MODIFIED_ASC,
            zoneId = fixedZone,
            clock = fixedClock,
            locale = Locale.US
        )

        assertEquals(3, groups.size)
        assertEquals("2023", groups[0].title)
        assertEquals("Yesterday", groups[1].title)
        assertEquals("Today", groups[2].title)
    }

    @Test
    fun testLeapYearMonthTransition() {
        // Base: March 1, 2024 (2024 is leap year, Feb has 29 days)
        val leapBaseDate = LocalDate.of(2024, 3, 1) // Friday
        val leapInstant = leapBaseDate.atTime(12, 0).toInstant(ZoneOffset.UTC)
        val leapClock = Clock.fixed(leapInstant, fixedZone)

        // Feb 29, 2024 is Yesterday
        val feb29Note = createNote(11, LocalDate.of(2024, 2, 29))
        // Feb 28, 2024 is Earlier This Week
        val feb28Note = createNote(12, LocalDate.of(2024, 2, 28))

        val groups = NoteDateGrouper.groupNotes(
            notes = listOf(feb29Note, feb28Note),
            sortOption = NoteSortOption.MODIFIED_DESC,
            zoneId = fixedZone,
            clock = leapClock,
            locale = Locale.US
        )

        assertEquals(2, groups.size)
        assertEquals("yesterday", groups[0].id)
        assertEquals("earlier_this_week", groups[1].id)
    }

    @Test
    fun testTimeZoneDifferenceNearMidnight() {
        // 2026-10-21 01:00:00 UTC
        val midnightInstant = LocalDate.of(2026, 10, 21).atTime(1, 0).toInstant(ZoneOffset.UTC)

        // In Tokyo (UTC+9), 01:00 UTC is 10:00 AM on Oct 21 (Today)
        val tokyoZone = ZoneId.of("Asia/Tokyo")
        val tokyoClock = Clock.fixed(midnightInstant, tokyoZone)

        // In New York (UTC-4 / EDT), 01:00 UTC is 21:00 PM on Oct 20 (Yesterday relative to Oct 21)
        val nyZone = ZoneId.of("America/New_York")
        val nyClock = Clock.fixed(midnightInstant, nyZone)

        val noteTimestamp = midnightInstant.toEpochMilli()
        val note = Note(id = 20, title = "Near Midnight Note", updatedAt = noteTimestamp)

        val tokyoGroups = NoteDateGrouper.groupNotes(
            notes = listOf(note),
            sortOption = NoteSortOption.MODIFIED_DESC,
            zoneId = tokyoZone,
            clock = tokyoClock,
            locale = Locale.US
        )
        assertEquals("today", tokyoGroups[0].id)

        val nyGroups = NoteDateGrouper.groupNotes(
            notes = listOf(note),
            sortOption = NoteSortOption.MODIFIED_DESC,
            zoneId = nyZone,
            clock = nyClock,
            locale = Locale.US
        )
        assertEquals("today", nyGroups[0].id)
    }
}
