// Material 3 still marks TopAppBar (and several other components) as
// experimental. File-level opt-in rather than per-function, so adding another
// M3 component to this screen later doesn't fail the build again.
@file:OptIn(ExperimentalMaterial3Api::class)

package com.montessoricopilot.app.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.montessoricopilot.app.R
import com.montessoricopilot.app.data.repository.ChildRepository
import com.montessoricopilot.app.data.repository.ContentRepository
import com.montessoricopilot.app.data.repository.JournalRepository
import com.montessoricopilot.app.data.repository.RecommendationRepository
import com.montessoricopilot.app.data.repository.ShelfRepository

private enum class Tab(val labelRes: Int) {
    TODAY(R.string.tab_today),
    LIBRARY(R.string.tab_library),
    JOURNAL(R.string.tab_journal),
    SHELF(R.string.tab_shelf),
}

@Composable
fun ChildHomeScaffold(
    childId: Int,
    childRepository: ChildRepository,
    contentRepository: ContentRepository,
    journalRepository: JournalRepository,
    shelfRepository: ShelfRepository,
    recommendationRepository: RecommendationRepository,
    onBack: () -> Unit,
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = Tab.entries

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(tabs[selectedTab].labelRes)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_to_children),
                        )
                    }
                },
            )
        },
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = {
                            Icon(
                                when (tab) {
                                    Tab.TODAY -> Icons.Filled.Home
                                    Tab.LIBRARY -> Icons.Filled.List
                                    Tab.JOURNAL -> Icons.Filled.Book
                                    Tab.SHELF -> Icons.Filled.Inventory2
                                },
                                contentDescription = stringResource(tab.labelRes),
                            )
                        },
                        label = { Text(stringResource(tab.labelRes)) },
                    )
                }
            }
        },
    ) { padding ->
        val contentModifier = Modifier.padding(padding)
        when (tabs[selectedTab]) {
            Tab.TODAY -> TodayScreen(
                childId, childRepository, contentRepository,
                recommendationRepository, shelfRepository, contentModifier,
            )
            Tab.LIBRARY -> LibraryScreen(contentRepository, contentModifier)
            Tab.JOURNAL -> JournalScreen(childId, journalRepository, contentModifier)
            Tab.SHELF -> ShelfScreen(childId, shelfRepository, contentRepository, contentModifier)
        }
    }
}
