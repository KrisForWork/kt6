// app/src/main/java/com/example/kt6_2/presentation/list/components/FilterSection.kt
package com.example.kt6_2.presentation.list.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.kt6_2.presentation.list.LaureatesListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterSection(
    viewModel: LaureatesListViewModel,
    modifier: Modifier = Modifier
) {
    val selectedYear by viewModel.selectedYear.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val categoryDropdownExpanded by viewModel.categoryDropdownExpanded.collectAsState()
    val isFilterExpanded by viewModel.isFilterExpanded.collectAsState()

    Column(modifier = modifier) {
        // Заголовок фильтров
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Filters",
                style = MaterialTheme.typography.titleSmall
            )
            TextButton(onClick = { viewModel.toggleFilterExpanded() }) {
                Text(if (isFilterExpanded) "Hide" else "Show")
            }
        }

        // Содержимое фильтров
        if (isFilterExpanded) {
            Spacer(modifier = Modifier.height(8.dp))

            // Выбор года
            OutlinedTextField(
                value = selectedYear,
                onValueChange = { viewModel.updateYear(it) },
                label = { Text("Year (e.g., 2023)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Выбор категории
            ExposedDropdownMenuBox(
                expanded = categoryDropdownExpanded,
                onExpandedChange = { viewModel.toggleCategoryDropdown() }
            ) {
                OutlinedTextField(
                    value = viewModel.categoryDisplayNames[selectedCategory] ?: selectedCategory,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = categoryDropdownExpanded,
                    onDismissRequest = { viewModel.toggleCategoryDropdown() }
                ) {
                    viewModel.categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(viewModel.categoryDisplayNames[category] ?: category) },
                            onClick = {
                                viewModel.updateCategory(category)
                                viewModel.toggleCategoryDropdown()
                            }
                        )
                    }
                    DropdownMenuItem(
                        text = { Text("All categories") },
                        onClick = {
                            viewModel.updateCategory("")
                            viewModel.toggleCategoryDropdown()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Кнопка очистки
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = { viewModel.clearFilters() }) {
                    Text("Clear filters")
                }
            }
        }
    }
}