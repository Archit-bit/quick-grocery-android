const db = require('../config/database');

// Place a new order
exports.placeOrder = async (req, res) => {
    const userId = req.user; // From authMiddleware
    const { shippingAddress } = req.body;

    if (!shippingAddress) {
        return res.status(400).json({ message: 'Shipping address is required to place an order.' });
    }

    const client = await db.pool.connect(); // Use transaction for atomicity

    try {
        await client.query('BEGIN');

        // 1. Get items from the user's cart
        const cartResult = await client.query(
            `SELECT ci.product_id, ci.quantity, p.price, p.stock_quantity, p.name
            FROM cart_items ci
            JOIN products p ON ci.product_id = p.id
            WHERE ci.user_id = $1`,
            [userId]
        );
        const cartItems = cartResult.rows;

        if (cartItems.length === 0) {
            await client.query('ROLLBACK');
            return res.status(400).json({ message: 'Cannot place an order with an empty cart.' });
        }

        let totalAmount = 0;
        for (const item of cartItems) {
            if (item.quantity > item.stock_quantity) {
                await client.query('ROLLBACK');
                return res.status(400).json({ message: `Not enough stock for ${item.name}. Available: ${item.stock_quantity}, Requested: ${item.quantity}` });
            }
            totalAmount += item.quantity * parseFloat(item.price);
        }

        // 2. Create the order
        const orderResult = await client.query(
            'INSERT INTO orders (user_id, total_amount, shipping_address, status) VALUES ($1, $2, $3, $4) RETURNING id, order_date, total_amount, status',
            [userId, totalAmount, shippingAddress, 'pending']
        );
        const order = orderResult.rows[0];

        // 3. Add items to order_items and update product stock
        for (const item of cartItems) {
            await client.query(
                'INSERT INTO order_items (order_id, product_id, quantity, price) VALUES ($1, $2, $3, $4)',
                [order.id, item.product_id, item.quantity, item.price]
            );
            // Decrement stock quantity
            await client.query(
                'UPDATE products SET stock_quantity = stock_quantity - $1 WHERE id = $2',
                [item.quantity, item.product_id]
            );
        }

        // 4. Clear the user's cart
        await client.query('DELETE FROM cart_items WHERE user_id = $1', [userId]);

        await client.query('COMMIT');
        res.status(201).json({ message: 'Order placed successfully!', order });

    } catch (err) {
        await client.query('ROLLBACK');
        console.error('Error placing order:', err);
        res.status(500).send('Server Error');
    } finally {
        client.release();
    }
};

// Get all orders for the authenticated user
exports.getUserOrders = async (req, res) => {
    const userId = req.user;

    try {
        const result = await db.query(
            `SELECT id, order_date, total_amount, status, shipping_address, created_at
            FROM orders
            WHERE user_id = $1
            ORDER BY order_date DESC`,
            [userId]
        );
        res.json(result.rows);
    } catch (err) {
        console.error('Error fetching user orders:', err);
        res.status(500).send('Server Error');
    }
};

// Get details for a specific order of the authenticated user
exports.getOrderDetails = async (req, res) => {
    const userId = req.user;
    const { orderId } = req.params;

    try {
        // Get order details
        const orderResult = await db.query(
            `SELECT id, order_date, total_amount, status, shipping_address, created_at
            FROM orders
            WHERE id = $1 AND user_id = $2`,
            [orderId, userId]
        );
        const order = orderResult.rows[0];

        if (!order) {
            return res.status(404).json({ message: 'Order not found or does not belong to user.' });
        }

        // Get order items
        const itemsResult = await db.query(
            `SELECT oi.product_id, p.name, p.image_url, oi.quantity, oi.price
            FROM order_items oi
            JOIN products p ON oi.product_id = p.id
            WHERE oi.order_id = $1`,
            [orderId]
        );
        order.items = itemsResult.rows;

        res.json(order);

    } catch (err) {
        console.error('Error fetching order details:', err);
        res.status(500).send('Server Error');
    }
};
