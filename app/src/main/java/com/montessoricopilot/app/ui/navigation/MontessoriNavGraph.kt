package com.montessoricopilot.app.ui.navigation

// slideIntoContainer / slideOutOfContainer are members of
// AnimatedContentTransitionScope — the implicit receiver of the transition
// lambdas below — so they are called, not imported.
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import com.montessoricopilot.app.data.repository.DailyRepository
import com.montessoricopilot.app.data.repository.JournalRepository
import com.montessoricopilot.app.data.repository.RecommendationRepository
import com.montessoricopilot.app.data.repository.ShelfRepository
import com.montessoricopilot.app.data.user.UserDatabase
import com.montessoricopilot.app.ui.screens.ActivityDetailScreen
import com.montessoricopilot.app.ui.screens.AttributionsScreen
import com.montessoricopilot.app.ui.screens.ChildHomeScaffold
import com.montessoricopilot.app.ui.screens.ChildListScreen

private const val NAV_DURATION_MS = 260

/**
 * Whole-app navigation.
 *
 * Transitions are a horizontal slide plus fade — the standard "forward/back"
 * spatial model, which tells the user where they are without needing a shared
 * element. Deliberately not using SharedTransitionLayout: it is still
 * experimental and fiddly to wire through nested navigation, and a broken
 * transition is worse than a plain one.
 *
 * Repositories are constructed once here and passed down. This is the seam the
 * PRD relies on for later growth: adding accounts or sync means introducing a
 * remote data source behind these same classes, not rewriting the UI.
 */
@Composable
fun MontessoriNavGraph(contentDatabase: ContentDatabase, userDatabase: UserDatabase) {
    val navController = rememberNavController()

    val contentDao = remember { contentDatabase.contentDao() }
    val userDao = remember { userDatabase.userDao() }

    val childRepository = remember { ChildRepository(userDao) }
    val contentRepository = remember { ContentRepository(contentDao) }
    val journalRepository = remember { JournalRepository(userDao) }
    val shelfRepository = remember { ShelfRepository(userDao) }
    val recommendationRepository = remember {
        RecommendationRepository(contentDao, userDao, contentRepository)
    }
    val dailyRepository = remember {
        DailyRepository(contentDao, userDao, contentRepository)
    }

    NavHost(
        navController = navController,
        startDestination = "children",
        enterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(NAV_DURATION_MS),
            ) + fadeIn(tween(NAV_DURATION_MS))
        },
        exitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(NAV_DURATION_MS),
            ) + fadeOut(tween(NAV_DURATION_MS))
        },
        popEnterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(NAV_DURATION_MS),
            ) + fadeIn(tween(NAV_DURATION_MS))
        },
        popExitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(NAV_DURATION_MS),
            ) + fadeOut(tween(NAV_DURATION_MS))
        },
    ) {
        composable("children") {
            ChildListScreen(
                childRepository = childRepository,
                onChildSelected = { childId -> navController.navigate("child/$childId") },
                onAttributions = { navController.navigate("attributions") },
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
                dailyRepository = dailyRepository,
                onBack = { navController.popBackStack() },
                onActivityClick = { activityId ->
                    navController.navigate("activity/$activityId")
                },
            )
        }

        composable(
            route = "activity/{activityId}",
            arguments = listOf(navArgument("activityId") { type = NavType.IntType }),
        ) { backStackEntry ->
            val activityId = backStackEntry.arguments?.getInt("activityId") ?: return@composable
            ActivityDetailScreen(
                activityId = activityId,
                contentRepository = contentRepository,
                onBack = { navController.popBackStack() },
            )
        }

        composable("attributions") {
            AttributionsScreen(
                contentRepository = contentRepository,
                onBack = { navController.popBackStack() },
            )
        }
    }
}
