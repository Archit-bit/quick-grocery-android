package com.quickgrocery.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.quickgrocery.ui.theme.AppShapes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = AppShapes.medium,
        shadowElevation = 6.dp,
        tonalElevation = 1.dp,
        modifier = modifier
    ) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = null)
            },
            placeholder = { Text(text = "Search milk, eggs, bread") },
            colors = TextFieldDefaults.colors()
        )
    }
}
