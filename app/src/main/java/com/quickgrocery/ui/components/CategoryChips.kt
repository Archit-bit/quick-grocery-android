package com.quickgrocery.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BakeryDining
import androidx.compose.material.icons.filled.BreakfastDining
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.LocalGroceryStore
import androidx.compose.material.icons.filled.LocalPizza
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.quickgrocery.ui.theme.AppShapes
import com.quickgrocery.ui.theme.AppSpacing

@Composable
fun CategoryChips(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
        contentPadding = PaddingValues(horizontal = AppSpacing.lg)
    ) {
        items(categories) { category ->
            FilterChip(
                selected = category == selectedCategory,
                onClick = { onCategorySelected(category) },
                label = { Text(text = category) },
                leadingIcon = {
                    Icon(
                        imageVector = categoryIcon(category),
                        contentDescription = null
                    )
                },
                shape = AppShapes.small,
                border = null,
                colors = androidx.compose.material3.FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    selectedLabelColor = MaterialTheme.colorScheme.onSurface,
                    selectedLeadingIconColor = MaterialTheme.colorScheme.primary,
                    containerColor = MaterialTheme.colorScheme.surface,
                    labelColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    }
}

private fun categoryIcon(category: String): ImageVector {
    return when (category.lowercase()) {
        "all" -> Icons.Default.Category
        "dairy" -> Icons.Default.BreakfastDining
        "fruits" -> Icons.Default.Eco
        "vegetables" -> Icons.Default.Spa
        "bakery" -> Icons.Default.BakeryDining
        "pantry" -> Icons.Default.LocalGroceryStore
        "essentials" -> Icons.Default.LocalCafe
        "snacks" -> Icons.Default.LocalPizza
        else -> Icons.Default.LocalGroceryStore
    }
}
