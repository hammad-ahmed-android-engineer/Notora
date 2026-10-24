package com.example.notora.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.notora.ui.archive.ArchiveScreen
import com.example.notora.ui.archive.ArchiveViewModel
import com.example.notora.ui.calendar.CalendarScreen
import com.example.notora.ui.calendar.CalendarViewModel
import com.example.notora.ui.categories.CategoriesScreen
import com.example.notora.ui.categories.CategoryViewModel
import com.example.notora.ui.editor.NoteEditorScreen
import com.example.notora.ui.editor.NoteEditorViewModel
import com.example.notora.ui.home.HomeScreen
import com.example.notora.ui.home.HomeViewModel
import com.example.notora.ui.revisions.NoteRevisionsScreen
import com.example.notora.ui.search.SearchScreen
import com.example.notora.ui.search.SearchViewModel
import com.example.notora.ui.settings.SettingsScreen
import com.example.notora.ui.settings.SettingsViewModel
import com.example.notora.ui.splash.SplashScreen
import com.example.notora.ui.tags.TagsScreen
import com.example.notora.ui.tags.TagViewModel
import com.example.notora.ui.templates.TemplatesScreen
import com.example.notora.ui.templates.TemplatesViewModel
import com.example.notora.ui.trash.TrashScreen
import com.example.notora.ui.trash.TrashViewModel

object Screen {
    const val SPLASH = "splash"
    const val HOME = "home"
    const val EDITOR = "editor?noteId={noteId}&noteType={noteType}"
    const val CATEGORIES = "categories"
    const val TAGS = "tags"
    const val SEARCH = "search"
    const val ARCHIVE = "archive"
    const val TRASH = "trash"
    const val CALENDAR = "calendar"
    const val TEMPLATES = "templates"
    const val REVISIONS = "revisions/{noteId}"
    const val SETTINGS = "settings"

    fun createEditorRoute(noteId: Long? = null, noteType: String? = null): String {
        val idParam = noteId ?: -1L
        val typeParam = noteType ?: "STANDARD"
        return "editor?noteId=$idParam&noteType=$typeParam"
    }

    fun createRevisionsRoute(noteId: Long): String {
        return "revisions/$noteId"
    }
}

@Composable
fun NotoraNavGraph(
    navController: NavHostController,
    startDestination: String = Screen.SPLASH,
    modifier: Modifier = Modifier
) {
    fun navigateToTab(route: String) {
        if (navController.currentDestination?.route != route) {
            navController.navigate(route) {
                popUpTo(Screen.HOME) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = {
            fadeIn(animationSpec = tween(200))
        },
        exitTransition = {
            fadeOut(animationSpec = tween(200))
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(200))
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(200))
        }
    ) {
        composable(Screen.SPLASH) {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate(Screen.HOME) {
                        popUpTo(Screen.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.HOME) {
            val viewModel: HomeViewModel = hiltViewModel()
            HomeScreen(
                viewModel = viewModel,
                onNavigateToEditor = { noteId, noteType ->
                    navController.navigate(Screen.createEditorRoute(noteId, noteType))
                },
                onNavigateToCategories = { navigateToTab(Screen.CATEGORIES) },
                onNavigateToTags = { navController.navigate(Screen.TAGS) },
                onNavigateToArchive = { navigateToTab(Screen.ARCHIVE) },
                onNavigateToTrash = { navController.navigate(Screen.TRASH) },
                onNavigateToCalendar = { navigateToTab(Screen.CALENDAR) },
                onNavigateToTemplates = { navController.navigate(Screen.TEMPLATES) },
                onNavigateToSettings = { navigateToTab(Screen.SETTINGS) }
            )
        }

        composable(
            route = Screen.EDITOR,
            arguments = listOf(
                navArgument("noteId") {
                    type = NavType.LongType
                    defaultValue = -1L
                },
                navArgument("noteType") {
                    type = NavType.StringType
                    defaultValue = "STANDARD"
                }
            )
        ) {
            val viewModel: NoteEditorViewModel = hiltViewModel()
            NoteEditorScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToRevisions = { noteId ->
                    navController.navigate(Screen.createRevisionsRoute(noteId))
                }
            )
        }

        composable(Screen.CATEGORIES) {
            val viewModel: CategoryViewModel = hiltViewModel()
            CategoriesScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToHome = { navigateToTab(Screen.HOME) },
                onNavigateToCalendar = { navigateToTab(Screen.CALENDAR) },
                onNavigateToArchive = { navigateToTab(Screen.ARCHIVE) },
                onNavigateToSettings = { navigateToTab(Screen.SETTINGS) }
            )
        }

        composable(Screen.TAGS) {
            val viewModel: TagViewModel = hiltViewModel()
            TagsScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.SEARCH) {
            val viewModel: SearchViewModel = hiltViewModel()
            SearchScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEditor = { noteId ->
                    navController.navigate(Screen.createEditorRoute(noteId))
                }
            )
        }

        composable(Screen.ARCHIVE) {
            val viewModel: ArchiveViewModel = hiltViewModel()
            ArchiveScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEditor = { noteId ->
                    navController.navigate(Screen.createEditorRoute(noteId))
                },
                onNavigateToHome = { navigateToTab(Screen.HOME) },
                onNavigateToCategories = { navigateToTab(Screen.CATEGORIES) },
                onNavigateToCalendar = { navigateToTab(Screen.CALENDAR) },
                onNavigateToSettings = { navigateToTab(Screen.SETTINGS) }
            )
        }

        composable(Screen.TRASH) {
            val viewModel: TrashViewModel = hiltViewModel()
            TrashScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.CALENDAR) {
            val viewModel: CalendarViewModel = hiltViewModel()
            CalendarScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEditor = { noteId ->
                    navController.navigate(Screen.createEditorRoute(noteId))
                },
                onNavigateToHome = { navigateToTab(Screen.HOME) },
                onNavigateToCategories = { navigateToTab(Screen.CATEGORIES) },
                onNavigateToArchive = { navigateToTab(Screen.ARCHIVE) },
                onNavigateToSettings = { navigateToTab(Screen.SETTINGS) }
            )
        }

        composable(Screen.TEMPLATES) {
            val viewModel: TemplatesViewModel = hiltViewModel()
            TemplatesScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNoteCreated = { noteId ->
                    navController.navigate(Screen.createEditorRoute(noteId))
                }
            )
        }

        composable(
            route = Screen.REVISIONS,
            arguments = listOf(
                navArgument("noteId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getLong("noteId") ?: 0L
            NoteRevisionsScreen(
                noteId = noteId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.SETTINGS) {
            val viewModel: SettingsViewModel = hiltViewModel()
            SettingsScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToHome = { navigateToTab(Screen.HOME) },
                onNavigateToCategories = { navigateToTab(Screen.CATEGORIES) },
                onNavigateToCalendar = { navigateToTab(Screen.CALENDAR) },
                onNavigateToArchive = { navigateToTab(Screen.ARCHIVE) }
            )
        }
    }
}
