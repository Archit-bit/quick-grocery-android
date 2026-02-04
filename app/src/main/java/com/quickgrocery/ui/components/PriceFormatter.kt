package com.quickgrocery.ui.components

import java.util.Locale

fun formatPrice(price: Double): String {
    return String.format(Locale.US, "$%.2f", price)
}
