const db = require('../config/database');

// Get user's cart
exports.getCart = async (req, res) => {
    try {
        const userId = req.user; // Set by authMiddleware
        const result = await db.query(
            `SELECT 
                ci.product_id, 
                p.name, 
                p.description, 
                p.price, 
                p.image_url, 
                ci.quantity,
                p.stock_quantity AS available_stock
            FROM cart_items ci
            JOIN products p ON ci.product_id = p.id
            WHERE ci.user_id = $1`,
            [userId]
        );
        res.json(result.rows);
    } catch (err) {
        console.error('Error fetching cart:', err);
        res.status(500).send('Server Error');
    }
};

// Add item to cart or update quantity if exists
exports.addItemToCart = async (req, res) => {
    const { productId, quantity } = req.body;
    const userId = req.user; // Set by authMiddleware

    if (!productId || !quantity || quantity <= 0) {
        return res.status(400).json({ message: 'Product ID and a positive quantity are required.' });
    }

    try {
        // Check if product exists and has enough stock
        const productResult = await db.query('SELECT price, stock_quantity FROM products WHERE id = $1', [productId]);
        if (productResult.rows.length === 0) {
            return res.status(404).json({ message: 'Product not found.' });
        }
        const product = productResult.rows[0];
        if (product.stock_quantity < quantity) {
            return res.status(400).json({ message: `Not enough stock for ${product.name}. Available: ${product.stock_quantity}` });
        }

        // Check if item already in cart
        const existingCartItem = await db.query(
            'SELECT quantity FROM cart_items WHERE user_id = $1 AND product_id = $2',
            [userId, productId]
        );

        let newQuantity = quantity;
        if (existingCartItem.rows.length > 0) {
            newQuantity += existingCartItem.rows[0].quantity;
            if (product.stock_quantity < newQuantity) {
                return res.status(400).json({ message: `Adding this quantity exceeds available stock for ${product.name}. Max allowed: ${product.stock_quantity}` });
            }
            await db.query(
                'UPDATE cart_items SET quantity = $1, updated_at = CURRENT_TIMESTAMP WHERE user_id = $2 AND product_id = $3',
                [newQuantity, userId, productId]
            );
        } else {
            await db.query(
                'INSERT INTO cart_items (user_id, product_id, quantity) VALUES ($1, $2, $3)',
                [userId, productId, quantity]
            );
        }

        res.status(200).json({ message: 'Item added to cart successfully.' });

    } catch (err) {
        console.error('Error adding item to cart:', err);
        res.status(500).send('Server Error');
    }
};

// Update item quantity in cart
exports.updateCartItemQuantity = async (req, res) => {
    const { productId } = req.params;
    const { quantity } = req.body;
    const userId = req.user;

    if (!quantity || quantity <= 0) {
        return res.status(400).json({ message: 'A positive quantity is required.' });
    }

    try {
        // Check if product exists and has enough stock
        const productResult = await db.query('SELECT stock_quantity, name FROM products WHERE id = $1', [productId]);
        if (productResult.rows.length === 0) {
            return res.status(404).json({ message: 'Product not found.' });
        }
        const product = productResult.rows[0];
        if (product.stock_quantity < quantity) {
            return res.status(400).json({ message: `Not enough stock for ${product.name}. Available: ${product.stock_quantity}` });
        }

        const result = await db.query(
            'UPDATE cart_items SET quantity = $1, updated_at = CURRENT_TIMESTAMP WHERE user_id = $2 AND product_id = $3 RETURNING *',
            [quantity, userId, productId]
        );

        if (result.rows.length === 0) {
            return res.status(404).json({ message: 'Item not found in cart.' });
        }

        res.status(200).json({ message: 'Cart item quantity updated successfully.' });

    } catch (err) {
        console.error('Error updating cart item quantity:', err);
        res.status(500).send('Server Error');
    }
};

// Remove item from cart
exports.removeItemFromCart = async (req, res) => {
    const { productId } = req.params;
    const userId = req.user;

    try {
        const result = await db.query(
            'DELETE FROM cart_items WHERE user_id = $1 AND product_id = $2 RETURNING *',
            [userId, productId]
        );

        if (result.rows.length === 0) {
            return res.status(404).json({ message: 'Item not found in cart.' });
        }

        res.status(200).json({ message: 'Item removed from cart successfully.' });

    } catch (err) {
        console.error('Error removing item from cart:', err);
        res.status(500).send('Server Error');
    }
};
