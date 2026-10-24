package com.example.notora.domain.util

import com.example.notora.domain.model.Note
import com.example.notora.domain.model.NoteDateGroup
import com.example.notora.domain.model.NoteSortOption
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.Locale

/**
 * Pure domain utility responsible for date-wise grouping of notes.
 *
 * Rules:
 * - Notes modified today -> "Today"
 * - Notes modified yesterday -> "Yesterday"
 * - Notes earlier in the current calendar week -> "Earlier This Week"
 * - Notes from the previous calendar week -> "Last Week"
 * - Notes earlier in the current calendar month -> "Earlier This Month"
 * - Notes from the previous calendar month -> "Last Month"
 * - Older notes within the previous 6-month period -> "Last 6 Months"
 * - Notes from the previous calendar year -> "Last Year"
 * - Older notes -> Grouped by year, such as "2025", "2024", etc.
 *
 * Guaranteed:
 * - Non-overlapping calendar boundaries based on local time zone.
 * - Every note appears in exactly one section.
 * - Empty sections are omitted.
 */
object NoteDateGrouper {

    data class DateSection(
        val id: String,
        val title: String,
        val orderWeight: Long
    )

    fun groupNotes(
        notes: List<Note>,
        sortOption: NoteSortOption = NoteSortOption.MODIFIED_DESC,
        zoneId: ZoneId = ZoneId.systemDefault(),
        clock: Clock = Clock.system(zoneId),
        locale: Locale = Locale.getDefault()
    ): List<NoteDateGroup> {
        if (notes.isEmpty()) return emptyList()

        val nowDate = LocalDate.now(clock)
        val isAscending = sortOption == NoteSortOption.MODIFIED_ASC || sortOption == NoteSortOption.CREATED_ASC

        // Group notes while preserving their sorted order within each section
        val groupedMap = LinkedHashMap<DateSection, MutableList<Note>>()

        for (note in notes) {
            val timestamp = getTimestampForSorting(note, sortOption)
            val section = classifyTimestamp(timestamp, nowDate, zoneId, locale)
            groupedMap.getOrPut(section) { mutableListOf() }.add(note)
        }

        // Sort sections chronologically
        val sortedEntries = if (isAscending) {
            groupedMap.entries.sortedByDescending { it.key.orderWeight }
        } else {
            groupedMap.entries.sortedBy { it.key.orderWeight }
        }

        return sortedEntries.map { (section, sectionNotes) ->
            NoteDateGroup(
                id = section.id,
                title = section.title,
                notes = sectionNotes
            )
        }
    }

    fun getTimestampForSorting(note: Note, sortOption: NoteSortOption): Long {
        return when (sortOption) {
            NoteSortOption.CREATED_DESC, NoteSortOption.CREATED_ASC -> note.createdAt
            else -> note.updatedAt
        }
    }

    fun classifyTimestamp(
        timestamp: Long,
        nowDate: LocalDate,
        zoneId: ZoneId = ZoneId.systemDefault(),
        locale: Locale = Locale.getDefault()
    ): DateSection {
        val noteDate = Instant.ofEpochMilli(timestamp).atZone(zoneId).toLocalDate()
        return classifyLocalDate(noteDate, nowDate, locale)
    }

    fun classifyLocalDate(
        noteDate: LocalDate,
        nowDate: LocalDate,
        locale: Locale = Locale.getDefault()
    ): DateSection {
        val firstDayOfWeek = WeekFields.of(locale).firstDayOfWeek
        val startOfThisWeek = nowDate.with(TemporalAdjusters.previousOrSame(firstDayOfWeek))
        val startOfLastWeek = startOfThisWeek.minusWeeks(1)
        val endOfLastWeek = startOfThisWeek.minusDays(1)

        val startOfThisMonth = nowDate.withDayOfMonth(1)

        val prevMonthDate = nowDate.minusMonths(1)
        val startOfLastMonth = prevMonthDate.withDayOfMonth(1)
        val endOfLastMonth = prevMonthDate.withDayOfMonth(prevMonthDate.lengthOfMonth())

        val startOf6Months = nowDate.minusMonths(6).withDayOfMonth(1)
        val prevYear = nowDate.minusYears(1).year

        return when {
            // 1. Today (also captures any future clock drifts)
            !noteDate.isBefore(nowDate) -> DateSection("today", "Today", 100L)

            // 2. Yesterday
            noteDate == nowDate.minusDays(1) -> DateSection("yesterday", "Yesterday", 200L)

            // 3. Earlier This Week
            !noteDate.isBefore(startOfThisWeek) -> DateSection("earlier_this_week", "Earlier This Week", 300L)

            // 4. Last Week
            !noteDate.isBefore(startOfLastWeek) && !noteDate.isAfter(endOfLastWeek) -> {
                DateSection("last_week", "Last Week", 400L)
            }

            // 5. Earlier This Month
            !noteDate.isBefore(startOfThisMonth) -> DateSection("earlier_this_month", "Earlier This Month", 500L)

            // 6. Last Month
            !noteDate.isBefore(startOfLastMonth) && !noteDate.isAfter(endOfLastMonth) -> {
                DateSection("last_month", "Last Month", 600L)
            }

            // 7. Last 6 Months
            !noteDate.isBefore(startOf6Months) -> DateSection("last_6_months", "Last 6 Months", 700L)

            // 8. Current calendar year, but older than 6 months
            noteDate.year == nowDate.year -> DateSection("year_${noteDate.year}", "${noteDate.year}", 750L)

            // 9. Previous calendar year
            noteDate.year == prevYear -> DateSection("last_year", "Last Year", 800L)

            // 10. Older years (e.g. 2024, 2023, 2022)
            else -> {
                val weight = 1000L + (prevYear - noteDate.year).toLong()
                DateSection("year_${noteDate.year}", "${noteDate.year}", weight)
            }
        }
    }
}
