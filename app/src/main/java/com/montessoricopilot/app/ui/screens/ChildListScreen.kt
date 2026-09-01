// TopAppBar and the clickable Card overload are still experimental in
// Material 3; opt in at file level so future M3 additions here don't
// re-break the build.
@file:OptIn(ExperimentalMaterial3Api::class)

package com.montessoricopilot.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.montessoricopilot.app.R
import com.montessoricopilot.app.data.repository.ChildRepository
import com.montessoricopilot.app.data.user.ChildEntity
import com.montessoricopilot.app.logic.ageInMonths
import com.montessoricopilot.app.ui.ViewModelFactory
import com.montessoricopilot.app.ui.components.LanguageMenu
import com.montessoricopilot.app.viewmodel.ChildListViewModel
import java.time.LocalDate

/**
 * App entry point. The empty state doubles as onboarding — adding the first
 * child *is* the whole onboarding flow.
 */
@Composable
fun ChildListScreen(
    childRepository: ChildRepository,
    onChildSelected: (Int) -> Unit,
    onAttributions: () -> Unit,
) {
    val viewModel: ChildListViewModel =
        viewModel(factory = ViewModelFactory { ChildListViewModel(childRepository) })
    val children by viewModel.children.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.children_title)) },
                actions = {
                    LanguageMenu()
                    IconButton(onClick = onAttributions) {
                        Icon(
                            Icons.Filled.Info,
                            contentDescription = stringResource(R.string.attributions),
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.add_child))
            }
        },
    ) { padding ->
        if (children.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    stringResource(R.string.welcome_title),
                    style = MaterialTheme.typography.headlineMedium,
                )
                Text(
                    stringResource(R.string.welcome_body),
                    modifier = Modifier.padding(top = 12.dp),
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    stringResource(R.string.disclaimer),
                    modifier = Modifier.padding(top = 24.dp),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
            ) {
                items(children, key = { it.id }) { child ->
                    ChildRow(child, onClick = { onChildSelected(child.id) })
                }
            }
        }
    }

    if (showAddDialog) {
        AddChildDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, birthDate ->
                viewModel.addChild(name, birthDate)
                showAddDialog = false
            },
        )
    }
}

@Composable
private fun ChildRow(child: ChildEntity, onClick: () -> Unit) {
    val months = ageInMonths(child.birthDateEpochDay)
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), onClick = onClick) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(child.name, style = MaterialTheme.typography.titleLarge)
            Text(
                pluralStringResource(R.plurals.age_months, months, months),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun AddChildDialog(onDismiss: () -> Unit, onConfirm: (String, LocalDate) -> Unit) {
    var name by remember { mutableStateOf("") }
    // Month/year entry keeps this dialog free of a date-picker dependency.
    // Replaced by a real DatePicker in P1 (README known gaps).
    var birthYear by remember { mutableStateOf(LocalDate.now().year.toString()) }
    var birthMonth by remember { mutableStateOf(LocalDate.now().monthValue.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.add_a_child)) },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.child_name)) },
                    singleLine = true,
                )
                OutlinedTextField(
                    value = birthYear,
                    onValueChange = { birthYear = it },
                    label = { Text(stringResource(R.string.birth_year)) },
                    singleLine = true,
                    modifier = Modifier.padding(top = 8.dp),
                )
                OutlinedTextField(
                    value = birthMonth,
                    onValueChange = { birthMonth = it },
                    label = { Text(stringResource(R.string.birth_month)) },
                    singleLine = true,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val year = birthYear.toIntOrNull() ?: return@TextButton
                val month = birthMonth.toIntOrNull()?.coerceIn(1, 12) ?: return@TextButton
                if (name.isNotBlank()) onConfirm(name, LocalDate.of(year, month, 1))
            }) { Text(stringResource(R.string.action_add)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_cancel)) }
        },
    )
}
