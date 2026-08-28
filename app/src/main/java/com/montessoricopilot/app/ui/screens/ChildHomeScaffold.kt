package com.montessoricopilot.app.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List as ListIcon
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.montessoricopilot.app.data.repository.ChildRepository
import com.montessoricopilot.app.data.repository.ContentRepository
import com.montessoricopilot.app.data.repository.JournalRepository
import com.montessoricopilot.app.data.repository.RecommendationRepository
import com.montessoricopilot.app.data.repository.ShelfRepository

private enum class Tab(val label: String) { TODAY("Today"), LIBRARY("Library"), JOURNAL("Journal"), SHELF("Shelf") }

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
                title = { Text(tabs[selectedTab].label) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back to children")
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
                                    Tab.LIBRARY -> ListIcon
                                    Tab.JOURNAL -> Icons.Filled.Book
                                    Tab.SHELF -> Icons.Filled.Inventory2
                                },
                                contentDescription = tab.label,
                            )
                        },
                        label = { Text(tab.label) },
                    )
                }
            }
        },
    ) { padding ->
        val contentModifier = Modifier.padding(padding)
        when (tabs[selectedTab]) {
            Tab.TODAY -> TodayScreen(
                childId, childRepository, contentRepository, recommendationRepository, shelfRepository, contentModifier,
            )
            Tab.LIBRARY -> LibraryScreen(contentRepository, contentModifier)
            Tab.JOURNAL -> JournalScreen(childId, journalRepository, contentModifier)
            Tab.SHELF -> ShelfScreen(childId, shelfRepository, contentRepository, contentModifier)
        }
    }
}
