package com.example.notora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.notora.presentation.MainViewModel
import com.example.notora.presentation.navigation.NotoraNavGraph
import com.example.notora.presentation.navigation.Screen
import com.example.notora.core.designsystem.NotoraTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

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
            val themeMode by mainViewModel.themeMode.collectAsStateWithLifecycle()
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
