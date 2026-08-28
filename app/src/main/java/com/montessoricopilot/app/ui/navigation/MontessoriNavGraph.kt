package com.montessoricopilot.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.montessoricopilot.app.data.content.ContentDatabase
import com.montessoricopilot.app.data.repository.ChildRepository
import com.montessoricopilot.app.data.repository.ContentRepository
import com.montessoricopilot.app.data.repository.JournalRepository
import com.montessoricopilot.app.data.repository.RecommendationRepository
import com.montessoricopilot.app.data.repository.ShelfRepository
import com.montessoricopilot.app.data.user.UserDatabase
import com.montessoricopilot.app.ui.screens.ChildHomeScaffold
import com.montessoricopilot.app.ui.screens.ChildListScreen

/**
 * Whole-app navigation: a child picker, then a per-child home that hosts
 * the four core screens (Today / Library / Journal / Shelf) behind a
 * bottom nav bar. Repositories are constructed once here and threaded down
 * — see design doc v0.2 for why the repository layer is the deliberate seam
 * for adding accounts/sync later without touching the UI.
 */
@Composable
fun MontessoriNavGraph(contentDatabase: ContentDatabase, userDatabase: UserDatabase) {
    val navController = rememberNavController()

    val childRepository = remember { ChildRepository(userDatabase.userDao()) }
    val contentRepository = remember { ContentRepository(contentDatabase.contentDao()) }
    val journalRepository = remember { JournalRepository(userDatabase.userDao()) }
    val shelfRepository = remember { ShelfRepository(userDatabase.userDao()) }
    val recommendationRepository = remember {
        RecommendationRepository(contentDatabase.contentDao(), userDatabase.userDao())
    }

    NavHost(navController = navController, startDestination = "children") {
        composable("children") {
            ChildListScreen(
                childRepository = childRepository,
                onChildSelected = { childId -> navController.navigate("child/$childId") },
            )
        }
        composable(
            route = "child/{childId}",
            arguments = listOf(navArgument("childId") { type = NavType.IntType }),
        ) { backStackEntry ->
            val childId = backStackEntry.arguments?.getInt("childId") ?: return@composable
            ChildHomeScaffold(
                childId = childId,
                childRepository = childRepository,
                contentRepository = contentRepository,
                journalRepository = journalRepository,
                shelfRepository = shelfRepository,
                recommendationRepository = recommendationRepository,
                onBack = { navController.popBackStack() },
            )
        }
    }
}
