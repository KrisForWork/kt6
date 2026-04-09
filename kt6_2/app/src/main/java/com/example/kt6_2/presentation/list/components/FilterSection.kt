package com.example.kt6_2.presentation.list.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
    val isFilterExpanded by viewModel.isFilterExpanded.collectAsState()
    val categoryDropdownExpanded by viewModel.categoryDropdownExpanded.collectAsState()
    val categories = viewModel.categories
    val categoryDisplayNames = viewModel.categoryDisplayNames

    Column(modifier = modifier) {
        // Filter toggle button
        FilterChip(
            selected = isFilterExpanded,
            onClick = { viewModel.toggleFilterExpanded() },
            label = { Text("Filters") },
            leadingIcon = {
                Icon(
                    Icons.Default.Menu,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        )

        // Expanded filter section
        if (isFilterExpanded) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Year input
                    OutlinedTextField(
                        value = selectedYear,
                        onValueChange = { viewModel.updateYear(it) },
                        label = { Text("Year (1901-${viewModel.currentYear})") },
                        placeholder = { Text("Any year") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Category dropdown
                    ExposedDropdownMenuBox(
                        expanded = categoryDropdownExpanded,
                        onExpandedChange = { viewModel.toggleCategoryDropdown() }
                    ) {
                        OutlinedTextField(
                            value = categoryDisplayNames[selectedCategory] ?: "Any category",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Category") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = categoryDropdownExpanded
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )

                        ExposedDropdownMenu(
                            expanded = categoryDropdownExpanded,
                            onDismissRequest = { viewModel.toggleCategoryDropdown() }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Any category") },
                                onClick = {
                                    viewModel.updateCategory("")
                                    viewModel.toggleCategoryDropdown()
                                }
                            )
                            categories.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(categoryDisplayNames[category] ?: category) },
                                    onClick = {
                                        viewModel.updateCategory(category)
                                        viewModel.toggleCategoryDropdown()
                                    }
                                )
                            }
                        }
                    }

                    // Clear filters button
                    if (selectedYear.isNotEmpty() || selectedCategory.isNotEmpty()) {
                        Button(
                            onClick = { viewModel.clearFilters() },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary
                            )
                        ) {
                            Icon(Icons.Default.Clear, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Clear Filters")
                        }
                    }
                }
            }
        }
    }
}