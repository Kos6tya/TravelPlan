package com.example.travelplan.ui

import android.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.travelplan.domain.Expense
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    tripTitle: String,
    totalBudget: Double,
    viewModel: TripViewModel,
    onBackClick: () -> Unit
) {
    val expenses = viewModel.expenses
    var showDialog by remember { mutableStateOf(false) }

    val expensesByCategory = remember(expenses.size) {
        expenses.groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }
    }

    val totalSpent = remember(expenses.size) {
        expenses.sumOf { it.amount }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Витрати: $tripTitle") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Text("⬅️", style = MaterialTheme.typography.titleLarge)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Text("+", style = MaterialTheme.typography.headlineMedium)
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Бюджет подорожі: $$totalBudget", style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = "Витрачено: $$totalSpent",
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (totalSpent > totalBudget) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    AndroidView(
                        factory = { context ->
                            PieChart(context).apply {
                                description.isEnabled = false
                                isDrawHoleEnabled = true
                                setHoleColor(Color.TRANSPARENT)
                                setEntryLabelColor(Color.BLACK)
                                legend.isEnabled = true
                                animateY(1000)
                            }
                        },
                        update = { chart ->
                            val entries = expensesByCategory.map { (cat, amount) ->
                                PieEntry(amount.toFloat(), cat)
                            }
                            val dataSet = PieDataSet(entries, "").apply {
                                colors = ColorTemplate.MATERIAL_COLORS.toList()
                                valueTextSize = 14f
                                valueTextColor = Color.WHITE
                            }
                            chart.data = PieData(dataSet)
                            chart.invalidate()
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                    )
                }
            }

            item {
                Text("Список усіх витрат:", style = MaterialTheme.typography.titleMedium)
            }

            items(expenses) { expense ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    ListItem(
                        headlineContent = { Text(expense.category, style = MaterialTheme.typography.bodyLarge) },
                        supportingContent = { Text(if (expense.description.isNotEmpty()) expense.description else "Без опису") },
                        trailingContent = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("$$${expense.amount}", style = MaterialTheme.typography.titleMedium)
                                IconButton(onClick = { viewModel.deleteExpense(expense) }) {
                                    Text("🗑")
                                }
                            }
                        }
                    )
                }
            }
        }

        if (showDialog) {
            AddExpenseDialog(
                onDismiss = { showDialog = false },
                onConfirm = { newExpense ->
                    viewModel.addExpense(newExpense)
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun AddExpenseDialog(
    onDismiss: () -> Unit,
    onConfirm: (Expense) -> Unit
) {
    var category by remember { mutableStateOf("Їжа") }
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Нова витрата") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Категорія (Їжа, Житло, Квитки...)") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Сума ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Опис (напр. Вечеря)") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val parsedAmount = amount.toDoubleOrNull() ?: 0.0
                    if (category.isNotBlank() && parsedAmount > 0.0) {
                        onConfirm(
                            Expense(
                                category = category,
                                amount = parsedAmount,
                                description = description
                            )
                        )
                    }
                }
            ) {
                Text("Додати")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Скасувати")
            }
        }
    )
}