package com.example.notora.data.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.notora.domain.repository.NoteRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var noteRepository: NoteRepository

    @Inject
    lateinit var reminderScheduler: ReminderScheduler

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || intent.action == Intent.ACTION_MY_PACKAGE_REPLACED) {
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val activeNotes = noteRepository.getActiveNotes().first()
                    activeNotes.forEach { note ->
                        if (note.isReminderActive && note.reminderTime != null && note.reminderTime > System.currentTimeMillis()) {
                            reminderScheduler.scheduleReminder(note)
                        }
                    }
                } catch (_: Exception) {
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
