package com.quickgrocery.data

fun sampleProducts(): List<Product> = listOf(
    Product(
        id = "p1",
        name = "Fresh Milk",
        category = "Dairy",
        price = 2.49,
        unit = "1 L",
        description = "Pasteurized full cream milk."
    ),
    Product(
        id = "p2",
        name = "Greek Yogurt",
        category = "Dairy",
        price = 1.99,
        unit = "200 g",
        description = "Thick and creamy yogurt."
    ),
    Product(
        id = "p3",
        name = "Bananas",
        category = "Fruits",
        price = 0.59,
        unit = "1 lb",
        description = "Naturally sweet bananas."
    ),
    Product(
        id = "p4",
        name = "Blueberries",
        category = "Fruits",
        price = 3.99,
        unit = "170 g",
        description = "Fresh blueberries rich in antioxidants."
    ),
    Product(
        id = "p5",
        name = "Baby Spinach",
        category = "Vegetables",
        price = 2.29,
        unit = "250 g",
        description = "Crisp and tender spinach leaves."
    ),
    Product(
        id = "p6",
        name = "Cherry Tomatoes",
        category = "Vegetables",
        price = 2.79,
        unit = "250 g",
        description = "Sweet cherry tomatoes."
    ),
    Product(
        id = "p7",
        name = "Whole Wheat Bread",
        category = "Bakery",
        price = 2.19,
        unit = "400 g",
        description = "Soft whole wheat loaf."
    ),
    Product(
        id = "p8",
        name = "Brown Eggs",
        category = "Essentials",
        price = 3.49,
        unit = "12 pack",
        description = "Farm fresh brown eggs."
    ),
    Product(
        id = "p9",
        name = "Olive Oil",
        category = "Pantry",
        price = 6.99,
        unit = "500 ml",
        description = "Extra virgin olive oil."
    ),
    Product(
        id = "p10",
        name = "Basmati Rice",
        category = "Pantry",
        price = 4.59,
        unit = "1 kg",
        description = "Long grain aromatic rice."
    )
)
