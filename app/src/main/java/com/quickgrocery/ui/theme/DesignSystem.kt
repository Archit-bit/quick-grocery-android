package com.quickgrocery.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

object AppSpacing {
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 20.dp
}

object AppShapes {
    val small = RoundedCornerShape(12.dp)
    val medium = RoundedCornerShape(16.dp)
    val large = RoundedCornerShape(20.dp)
}

val QuickGroceryShapes = Shapes(
    small = AppShapes.small,
    medium = AppShapes.medium,
    large = AppShapes.large
)
