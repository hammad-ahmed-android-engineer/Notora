package com.example.notora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.notora.domain.repository.SettingsRepository
import com.example.notora.ui.navigation.NotoraNavGraph
import com.example.notora.ui.navigation.Screen
import com.example.notora.ui.theme.NotoraTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Notora)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val actionExtra = intent.getStringExtra("action")
        val openNoteIdExtra = intent.getLongExtra("open_note_id", -1L)

        val initialDestination = when {
            openNoteIdExtra != -1L -> Screen.createEditorRoute(openNoteIdExtra)
            actionExtra == "new_note" -> Screen.createEditorRoute(null, "STANDARD")
            actionExtra == "new_checklist" -> Screen.createEditorRoute(null, "CHECKLIST")
            else -> Screen.SPLASH
        }

        setContent {
            val themeMode by settingsRepository.themeMode.collectAsState(initial = "SYSTEM")
            val useDarkTheme = when (themeMode) {
                "LIGHT" -> false
                "DARK" -> true
                else -> isSystemInDarkTheme()
            }

            NotoraTheme(darkTheme = useDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NotoraNavGraph(
                        navController = navController,
                        startDestination = initialDestination
                    )
                }
            }
        }
    }
}
