package com.montessoricopilot.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.montessoricopilot.app.data.repository.ChildRepository
import com.montessoricopilot.app.data.user.ChildEntity
import com.montessoricopilot.app.logic.ageInMonths
import com.montessoricopilot.app.ui.ViewModelFactory
import com.montessoricopilot.app.viewmodel.ChildListViewModel
import java.time.LocalDate

/**
 * App entry point. Empty state doubles as onboarding — there's no separate
 * "welcome" flow, since adding the first child *is* the whole onboarding.
 */
@Composable
fun ChildListScreen(childRepository: ChildRepository, onChildSelected: (Int) -> Unit) {
    val viewModel: ChildListViewModel = viewModel(factory = ViewModelFactory { ChildListViewModel(childRepository) })
    val children by viewModel.children.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Montessori Copilot") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Add child")
            }
        },
    ) { padding ->
        if (children.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                Text("Welcome", style = MaterialTheme.typography.headlineMedium)
                Text(
                    "Add your child's name and birth date to get age-appropriate " +
                        "Montessori activities, a journal, and a shelf tracker — " +
                        "all kept on this device.",
                    modifier = Modifier.padding(top = 12.dp),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp)) {
                items(children, key = { it.id }) { child -> ChildRow(child, onClick = { onChildSelected(child.id) }) }
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
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        onClick = onClick,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(child.name, style = MaterialTheme.typography.titleLarge)
            Text(
                "${ageInMonths(child.birthDateEpochDay)} months old",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun AddChildDialog(onDismiss: () -> Unit, onConfirm: (String, LocalDate) -> Unit) {
    var name by remember { mutableStateOf("") }
    // Simple month/year-of-birth entry keeps this dialog free of a date-picker
    // dependency; swap in a real DatePicker when polishing this screen.
    var birthYear by remember { mutableStateOf(LocalDate.now().year.toString()) }
    var birthMonth by remember { mutableStateOf((LocalDate.now().monthValue).toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add a child") },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
                OutlinedTextField(
                    value = birthYear, onValueChange = { birthYear = it },
                    label = { Text("Birth year") }, modifier = Modifier.padding(top = 8.dp),
                )
                OutlinedTextField(
                    value = birthMonth, onValueChange = { birthMonth = it },
                    label = { Text("Birth month (1-12)") }, modifier = Modifier.padding(top = 8.dp),
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val year = birthYear.toIntOrNull() ?: return@TextButton
                val month = birthMonth.toIntOrNull()?.coerceIn(1, 12) ?: return@TextButton
                if (name.isNotBlank()) onConfirm(name, LocalDate.of(year, month, 1))
            }) { Text("Add") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}
