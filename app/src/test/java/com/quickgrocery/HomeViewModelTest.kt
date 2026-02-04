package com.quickgrocery

import com.quickgrocery.data.InMemoryGroceryRepository
import com.quickgrocery.ui.home.HomeViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.flow.first
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun searchFiltersProducts() = runTest {
        val repository = InMemoryGroceryRepository()
        val viewModel = HomeViewModel(repository)

        viewModel.onSearchQueryChange("milk")
        val products = viewModel.uiState.first { it.products.isNotEmpty() }.products
        assertTrue(products.isNotEmpty())
        assertTrue(products.all { it.name.contains("milk", ignoreCase = true) })
    }
}
