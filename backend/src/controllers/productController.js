const db = require('../config/database');

// Get all products
exports.getAllProducts = async (req, res) => {
    try {
        const result = await db.query('SELECT * FROM products ORDER BY name ASC');
        res.json(result.rows);
    } catch (err) {
        console.error('Error fetching products:', err);
        res.status(500).send('Server Error');
    }
};

// Get product by ID
exports.getProductById = async (req, res) => {
    try {
        const { id } = req.params;
        const result = await db.query('SELECT * FROM products WHERE id = $1', [id]);
        if (result.rows.length > 0) {
            res.json(result.rows[0]);
        } else {
            res.status(404).send('Product not found');
        }
    } catch (err) {
        console.error('Error fetching product by ID:', err);
        res.status(500).send('Server Error');
    }
};
