// app/src/main/java/com/example/kt6_2/presentation/list/LaureatesListViewModel.kt
package com.example.kt6_2.presentation.list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kt6_2.domain.model.NobelCategories
import com.example.kt6_2.domain.usecase.GetLaureatesUseCase
import com.example.kt6_2.utils.NetworkResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class LaureatesListViewModel(
    private val getLaureatesUseCase: GetLaureatesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<LaureatesListState>(LaureatesListState.Loading)
    val state: StateFlow<LaureatesListState> = _state.asStateFlow()

    private val _selectedYear = MutableStateFlow<String>("")
    val selectedYear: StateFlow<String> = _selectedYear.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String>("")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _isFilterExpanded = MutableStateFlow(false)
    val isFilterExpanded: StateFlow<Boolean> = _isFilterExpanded.asStateFlow()

    private val _categoryDropdownExpanded = MutableStateFlow(false)
    val categoryDropdownExpanded: StateFlow<Boolean> = _categoryDropdownExpanded.asStateFlow()

    private var searchJob: Job? = null
    private var hasLoadedData = false  // Флаг для отслеживания загрузки

    val categories = NobelCategories.ALL
    val categoryDisplayNames = NobelCategories.DISPLAY_NAMES

    val currentYear: Int
        get() = Calendar.getInstance().get(Calendar.YEAR)

    init {
        // НЕ вызываем loadData() в init - ждём когда пользователь авторизуется
        Log.d("LaureatesVM", "ViewModel created, waiting for auth")
    }

    // Метод для загрузки данных после авторизации
    fun loadInitialData() {
        if (!hasLoadedData) {
            Log.d("LaureatesVM", "loadInitialData called")
            loadData()
        }
    }

    fun updateYear(year: String) {
        _selectedYear.value = year
        debounceLoadData()
    }

    fun updateCategory(category: String) {
        _selectedCategory.value = category
        debounceLoadData()
    }

    fun toggleFilterExpanded() {
        _isFilterExpanded.value = !_isFilterExpanded.value
    }

    fun toggleCategoryDropdown() {
        _categoryDropdownExpanded.value = !_categoryDropdownExpanded.value
    }

    fun clearFilters() {
        _selectedYear.value = ""
        _selectedCategory.value = ""
        loadData()
    }

    fun retry() {
        loadData()
    }

    private fun debounceLoadData() {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500)
            loadData()
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            Log.d("LaureatesVM", "loadData started")
            _state.value = LaureatesListState.Loading

            val year = _selectedYear.value.toIntOrNull()
            val category = _selectedCategory.value.ifEmpty { null }
            Log.d("LaureatesVM", "year=$year, category=$category")

            when (val result = getLaureatesUseCase(year = year, category = category)) {
                is NetworkResult.Success -> {
                    hasLoadedData = true
                    Log.d("LaureatesVM", "Success: ${result.data.size} prizes")
                    _state.value = LaureatesListState.Success(result.data)
                }
                is NetworkResult.Error -> {
                    Log.e("LaureatesVM", "Error: ${result.message}")
                    _state.value = LaureatesListState.Error(result.message)
                }
                NetworkResult.Loading -> {
                    Log.d("LaureatesVM", "Loading")
                    _state.value = LaureatesListState.Loading
                }
            }
        }
    }
}