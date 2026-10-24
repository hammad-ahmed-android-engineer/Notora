package com.example.notora.ui.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.notora.R
import com.example.notora.common.util.AppUtils
import com.example.notora.common.util.rememberDebouncedClick
import com.example.notora.ui.calendar.components.CalendarDateHeader
import com.example.notora.ui.components.NoteCard
import com.example.notora.ui.components.common.AppEmptyState
import com.example.notora.ui.components.common.AppTopBar
import com.example.notora.ui.navigation.Screen
import com.example.notora.ui.navigation.components.AppBottomNavBar
import com.example.notora.ui.navigation.components.AppNavigationRail
import com.example.notora.ui.theme.AppTypography
import com.example.notora.ui.theme.NotoraPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToEditor: (noteId: Long) -> Unit,
    modifier: Modifier = Modifier,
    onNavigateToHome: () -> Unit = onNavigateBack,
    onNavigateToCategories: () -> Unit = {},
    onNavigateToArchive: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    val configuration = LocalConfiguration.current
    val isLandscape = AppUtils.isLandscape(configuration)

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = uiState.selectedDate
    )

    LaunchedEffect(datePickerState.selectedDateMillis) {
        datePickerState.selectedDateMillis?.let {
            viewModel.selectDate(it)
        }
    }

    val contentScaffold = @Composable { scaffoldModifier: Modifier ->
        Scaffold(
            modifier = scaffoldModifier,
            topBar = {
                AppTopBar(
                    title = stringResource(R.string.calendar_screen_title),
                    onNavigateBack = onNavigateBack,
                    actions = {
                        if (!isLandscape) {
                            IconButton(onClick = rememberDebouncedClick { showDatePicker = true }) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = stringResource(R.string.calendar_select_date),
                                    tint = NotoraPrimary
                                )
                            }
                        }
                    }
                )
            },
            bottomBar = {
                if (!isLandscape) {
                    AppBottomNavBar(
                        currentRoute = Screen.CALENDAR,
                        onNavigateToHome = onNavigateToHome,
                        onNavigateToCategories = onNavigateToCategories,
                        onNavigateToCalendar = { /* Already here */ },
                        onNavigateToArchive = onNavigateToArchive,
                        onNavigateToSettings = onNavigateToSettings,
                        onCreateNote = {
                            onNavigateToEditor(-1L)
                        }
                    )
                }
            },
            floatingActionButton = {
                if (isLandscape) {
                    FloatingActionButton(
                        onClick = rememberDebouncedClick {
                            onNavigateToEditor(-1L)
                        },
                        containerColor = NotoraPrimary,
                        contentColor = Color.White,
                        shape = CircleShape
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.nav_create_note),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                CalendarDateHeader(
                    formattedDate = uiState.formattedDate,
                    onChangeDateClick = { showDatePicker = true }
                )

                if (uiState.notesForDate.isEmpty()) {
                    AppEmptyState(
                        icon = Icons.AutoMirrored.Filled.EventNote,
                        title = stringResource(R.string.calendar_empty_title),
                        description = stringResource(R.string.calendar_empty_desc),
                        actionButtonText = stringResource(R.string.home_action_create_note),
                        onActionClick = rememberDebouncedClick {
                            onNavigateToEditor(-1L)
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    LazyVerticalStaggeredGrid(
                        columns = StaggeredGridCells.Fixed(
                            if (isLandscape) 3 else 2
                        ),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalItemSpacing = 14.dp,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(uiState.notesForDate, key = { it.id }) { note ->
                            NoteCard(
                                note = note,
                                onClick = rememberDebouncedClick { onNavigateToEditor(note.id) },
                                onPinClick = {},
                                onFavoriteClick = {},
                                onArchiveClick = {},
                                onTrashClick = {},
                                onDuplicateClick = {}
                            )
                        }
                    }
                }
            }

            if (showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(onClick = rememberDebouncedClick {
                            datePickerState.selectedDateMillis?.let { viewModel.selectDate(it) }
                            showDatePicker = false
                        }) {
                            Text(stringResource(R.string.action_save), style = AppTypography.buttonText, color = NotoraPrimary)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = rememberDebouncedClick { showDatePicker = false }) {
                            Text(stringResource(R.string.action_cancel), style = AppTypography.buttonText)
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }
        }
    }

    if (isLandscape) {
        Row(modifier = modifier.fillMaxSize()) {
            AppNavigationRail(
                currentRoute = Screen.CALENDAR,
                onNavigateToHome = onNavigateToHome,
                onNavigateToCategories = onNavigateToCategories,
                onNavigateToCalendar = { /* Already here */ },
                onNavigateToArchive = onNavigateToArchive,
                onNavigateToSettings = onNavigateToSettings
            )
            contentScaffold(Modifier.weight(1f))
        }
    } else {
        contentScaffold(modifier.fillMaxSize())
    }
}
