package com.quickgrocery.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.quickgrocery.ui.components.SearchBarField
import com.quickgrocery.ui.components.formatPrice
import com.quickgrocery.ui.theme.AppShapes
import com.quickgrocery.ui.theme.AppSpacing
import com.quickgrocery.ui.theme.QuickGroceryTheme

@Composable
fun HomeTopBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        tonalElevation = 1.dp,
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .statusBarsPadding()
                .padding(horizontal = AppSpacing.lg, vertical = AppSpacing.md)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
                    DeliveryPill(text = "Delivery in 10-15 min")
                    LocationRow(location = "Deliver to: Home - Sector 21")
                }
                IconButton(onClick = onProfileClick) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            Spacer(modifier = Modifier.height(AppSpacing.md))
            SearchBarField(
                value = searchQuery,
                onValueChange = onSearchQueryChange
            )
        }
    }
}

@Composable
private fun DeliveryPill(text: String) {
    Surface(
        shape = AppShapes.small,
        color = MaterialTheme.colorScheme.primary,
        tonalElevation = 0.dp
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = AppSpacing.sm, vertical = AppSpacing.xs),
            color = Color.White,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
private fun LocationRow(location: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Default.Place,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.size(AppSpacing.xs))
        Text(
            text = location,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium
        )
        Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun PromoCarousel(
    banners: List<PromoBanner>,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.md),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = AppSpacing.lg)
    ) {
        items(banners) { banner ->
            PromoBannerCard(banner = banner)
        }
    }
}

@Composable
private fun PromoBannerCard(banner: PromoBanner) {
    Surface(
        shape = AppShapes.medium,
        tonalElevation = 1.dp,
        modifier = Modifier
            .height(120.dp)
            .width(260.dp)
            .padding(vertical = AppSpacing.xs)
    ) {
        Column(
            modifier = Modifier
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(banner.colorStart, banner.colorEnd)
                    )
                )
                .padding(AppSpacing.lg)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = banner.title,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
            Text(
                text = banner.subtitle,
                style = MaterialTheme.typography.labelMedium,
                color = Color.White
            )
            Text(
                text = banner.cta,
                style = MaterialTheme.typography.labelLarge,
                color = Color.White
            )
        }
    }
}

@Composable
fun BottomCartBar(
    itemCount: Int,
    total: Double,
    onViewCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = AppShapes.large,
        color = MaterialTheme.colorScheme.primary,
        shadowElevation = 8.dp,
        modifier = modifier
            .padding(horizontal = AppSpacing.lg, vertical = AppSpacing.md)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppSpacing.lg, vertical = AppSpacing.md),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "$itemCount items",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White
                )
                Text(
                    text = formatPrice(total),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            }
            Button(
                onClick = onViewCart,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = AppShapes.small
            ) {
                Text(
                    text = "View Cart",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BottomCartBarPreview() {
    QuickGroceryTheme {
        BottomCartBar(
            itemCount = 3,
            total = 12.48,
            onViewCart = {}
        )
    }
}

data class PromoBanner(
    val title: String,
    val subtitle: String,
    val cta: String,
    val colorStart: Color,
    val colorEnd: Color
)
