package com.example.notora.ui.settings

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.notora.R
import com.example.notora.common.util.AppUtils
import com.example.notora.ui.components.common.AppTopBar
import com.example.notora.ui.navigation.Screen
import com.example.notora.ui.navigation.components.AppBottomNavBar
import com.example.notora.ui.navigation.components.AppNavigationRail
import com.example.notora.ui.settings.components.SettingsAboutCard
import com.example.notora.ui.settings.components.SettingsAppearanceCard
import com.example.notora.ui.settings.components.SettingsBackupCard
import com.example.notora.ui.settings.components.SettingsEditorCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    onNavigateToHome: () -> Unit = onNavigateBack,
    onNavigateToCategories: () -> Unit = {},
    onNavigateToCalendar: () -> Unit = {},
    onNavigateToArchive: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isLandscape = AppUtils.isLandscape(configuration)

    val exportBackupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri: Uri? ->
        uri?.let {
            viewModel.exportBackup(it) { success ->
                val msg = if (success) "Backup exported successfully" else "Backup export failed"
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    val importBackupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.importBackup(it) { result ->
                val msg = if (result.success) {
                    "Successfully imported ${result.importedNotesCount} notes, ${result.importedCategoriesCount} categories, ${result.importedTagsCount} tags."
                } else {
                    result.errorMessage ?: "Import failed: Invalid file or schema."
                }
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
            }
        }
    }

    val contentScaffold = @Composable { scaffoldModifier: Modifier ->
        Scaffold(
            modifier = scaffoldModifier,
            topBar = {
                AppTopBar(
                    title = stringResource(R.string.settings_screen_title),
                    onNavigateBack = onNavigateBack
                )
            },
            bottomBar = {
                if (!isLandscape) {
                    AppBottomNavBar(
                        currentRoute = Screen.SETTINGS,
                        onNavigateToHome = onNavigateToHome,
                        onNavigateToCategories = onNavigateToCategories,
                        onNavigateToCalendar = onNavigateToCalendar,
                        onNavigateToArchive = onNavigateToArchive,
                        onNavigateToSettings = { /* Already here */ },
                        onCreateNote = onNavigateToHome
                    )
                }
            }
        ) { paddingValues ->
            if (isLandscape) {
                // Landscape 2-Column Responsive Layout
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Left Column: Appearance & Preferences
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        SettingsAppearanceCard(uiState = uiState, viewModel = viewModel)
                        SettingsEditorCard(uiState = uiState, viewModel = viewModel)
                    }

                    // Right Column: Backup & App Info
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        SettingsBackupCard(
                            onExport = { exportBackupLauncher.launch("notora_backup_${System.currentTimeMillis()}.json") },
                            onImport = { importBackupLauncher.launch(arrayOf("application/json")) }
                        )
                        SettingsAboutCard()
                    }
                }
            } else {
                // Portrait Vertical Settings Stream
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    SettingsAppearanceCard(uiState = uiState, viewModel = viewModel)
                    SettingsEditorCard(uiState = uiState, viewModel = viewModel)
                    SettingsBackupCard(
                        onExport = { exportBackupLauncher.launch("notora_backup_${System.currentTimeMillis()}.json") },
                        onImport = { importBackupLauncher.launch(arrayOf("application/json")) }
                    )
                    SettingsAboutCard()
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }

    if (isLandscape) {
        Row(modifier = modifier.fillMaxSize()) {
            AppNavigationRail(
                currentRoute = Screen.SETTINGS,
                onNavigateToHome = onNavigateToHome,
                onNavigateToCategories = onNavigateToCategories,
                onNavigateToCalendar = onNavigateToCalendar,
                onNavigateToArchive = onNavigateToArchive,
                onNavigateToSettings = { /* Already here */ }
            )
            contentScaffold(Modifier.weight(1f))
        }
    } else {
        contentScaffold(modifier.fillMaxSize())
    }
}
