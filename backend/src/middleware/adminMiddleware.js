const db = require('../config/database');

module.exports = async (req, res, next) => {
    try {
        const userId = req.user;
        if (!userId) {
            return res.status(401).json({ message: 'Unauthorized' });
        }
        const result = await db.query('SELECT role FROM users WHERE id = $1', [userId]);
        if (result.rows.length === 0) {
            return res.status(401).json({ message: 'User not found' });
        }
        const role = result.rows[0].role;
        if (role !== 'owner') {
            return res.status(403).json({ message: 'Owner access required' });
        }
        next();
    } catch (err) {
        console.error('Error in admin middleware:', err);
        res.status(500).send('Server Error');
    }
};
