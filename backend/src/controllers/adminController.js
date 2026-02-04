const db = require('../config/database');

exports.getAllOrders = async (req, res) => {
    try {
        const result = await db.query(
            `SELECT o.id, o.order_date, o.total_amount, o.status, o.shipping_address,
                    u.id AS user_id, u.email, u.username,
                    COALESCE(SUM(oi.quantity), 0) AS total_quantity,
                    COUNT(oi.id) AS item_count
             FROM orders o
             JOIN users u ON o.user_id = u.id
             LEFT JOIN order_items oi ON oi.order_id = o.id
             GROUP BY o.id, u.id
             ORDER BY o.order_date DESC`
        );
        res.json(result.rows);
    } catch (err) {
        console.error('Error fetching all orders:', err);
        res.status(500).send('Server Error');
    }
};

exports.getOrderDetails = async (req, res) => {
    try {
        const { orderId } = req.params;
        const orderResult = await db.query(
            `SELECT o.id, o.order_date, o.total_amount, o.status, o.shipping_address,
                    u.id AS user_id, u.email, u.username
             FROM orders o
             JOIN users u ON o.user_id = u.id
             WHERE o.id = $1`,
            [orderId]
        );

        if (orderResult.rows.length === 0) {
            return res.status(404).json({ message: 'Order not found' });
        }

        const itemsResult = await db.query(
            `SELECT oi.product_id, p.name, oi.quantity, oi.price
             FROM order_items oi
             JOIN products p ON oi.product_id = p.id
             WHERE oi.order_id = $1`,
            [orderId]
        );

        const order = orderResult.rows[0];
        order.items = itemsResult.rows;
        res.json(order);
    } catch (err) {
        console.error('Error fetching order details:', err);
        res.status(500).send('Server Error');
    }
};

exports.getInventory = async (req, res) => {
    try {
        const result = await db.query(
            `SELECT p.id, p.name, p.description, p.price, p.stock_quantity,
                    c.name AS category_name
             FROM products p
             LEFT JOIN categories c ON p.category_id = c.id
             ORDER BY p.name ASC`
        );
        res.json(result.rows);
    } catch (err) {
        console.error('Error fetching inventory:', err);
        res.status(500).send('Server Error');
    }
};

exports.updateOrderStatus = async (req, res) => {
    const { orderId } = req.params;
    const { status } = req.body;
    const allowed = ['pending', 'processing', 'delivered', 'cancelled'];

    if (!allowed.includes(status)) {
        return res.status(400).json({ message: 'Invalid status' });
    }

    try {
        const result = await db.query(
            'UPDATE orders SET status = $1, updated_at = CURRENT_TIMESTAMP WHERE id = $2 RETURNING *',
            [status, orderId]
        );

        if (result.rows.length === 0) {
            return res.status(404).json({ message: 'Order not found' });
        }

        res.json(result.rows[0]);
    } catch (err) {
        console.error('Error updating order status:', err);
        res.status(500).send('Server Error');
    }
};

exports.updateInventory = async (req, res) => {
    const { productId } = req.params;
    const { stockQuantity, price } = req.body;

    if (stockQuantity == null && price == null) {
        return res.status(400).json({ message: 'stockQuantity or price required' });
    }

    try {
        const fields = [];
        const values = [];
        let idx = 1;

        if (stockQuantity != null) {
            fields.push(`stock_quantity = $${idx++}`);
            values.push(stockQuantity);
        }
        if (price != null) {
            fields.push(`price = $${idx++}`);
            values.push(price);
        }

        values.push(productId);
        const query = `UPDATE products SET ${fields.join(', ')}, updated_at = CURRENT_TIMESTAMP WHERE id = $${idx} RETURNING *`;

        const result = await db.query(query, values);
        if (result.rows.length === 0) {
            return res.status(404).json({ message: 'Product not found' });
        }

        res.json(result.rows[0]);
    } catch (err) {
        console.error('Error updating inventory:', err);
        res.status(500).send('Server Error');
    }
};
