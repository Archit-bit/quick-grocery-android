const db = require('../config/database');

// Get all categories
exports.getAllCategories = async (req, res) => {
    try {
        const result = await db.query('SELECT * FROM categories ORDER BY name ASC');
        res.json(result.rows);
    } catch (err) {
        console.error('Error fetching categories:', err);
        res.status(500).send('Server Error');
    }
};

// Get category by ID
exports.getCategoryById = async (req, res) => {
    try {
        const { id } = req.params;
        const result = await db.query('SELECT * FROM categories WHERE id = $1', [id]);
        if (result.rows.length > 0) {
            res.json(result.rows[0]);
        } else {
            res.status(404).send('Category not found');
        }
    } catch (err) {
        console.error('Error fetching category by ID:', err);
        res.status(500).send('Server Error');
    }
};
