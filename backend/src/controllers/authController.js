const db = require('../config/database');
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const dotenv = require('dotenv');

dotenv.config();

// Register a new user
exports.register = async (req, res) => {
    const { username, email, password, full_name, address, phone_number } = req.body;

    try {
        // Check if user already exists
        const userExists = await db.query('SELECT * FROM users WHERE email = $1 OR username = $2', [email, username]);
        if (userExists.rows.length > 0) {
            return res.status(400).json({ message: 'User with this email or username already exists.' });
        }

        // Hash password
        const salt = await bcrypt.genSalt(10);
        const password_hash = await bcrypt.hash(password, salt);

        // Save user to database
        const result = await db.query(
            'INSERT INTO users (username, email, password_hash, full_name, address, phone_number) VALUES ($1, $2, $3, $4, $5, $6) RETURNING id, username, email',
            [username, email, password_hash, full_name, address, phone_number]
        );

        const newUser = result.rows[0];

        // Generate JWT
        const token = jwt.sign({ id: newUser.id }, process.env.JWT_SECRET, { expiresIn: '1h' });

        res.status(201).json({
            message: 'User registered successfully',
            token,
            user: {
                id: newUser.id,
                username: newUser.username,
                email: newUser.email,
            },
        });

    } catch (err) {
        console.error('Error during user registration:', err);
        res.status(500).send('Server Error');
    }
};

// Log in a user
exports.login = async (req, res) => {
    const { email, password } = req.body;

    try {
        // Check if user exists
        const userResult = await db.query('SELECT * FROM users WHERE email = $1', [email]);
        const user = userResult.rows[0];

        if (!user) {
            return res.status(400).json({ message: 'Invalid credentials' });
        }

        // Compare password
        const isMatch = await bcrypt.compare(password, user.password_hash);
        if (!isMatch) {
            return res.status(400).json({ message: 'Invalid credentials' });
        }

        // Generate JWT
        const token = jwt.sign({ id: user.id }, process.env.JWT_SECRET, { expiresIn: '1h' });

        res.json({
            message: 'Logged in successfully',
            token,
            user: {
                id: user.id,
                username: user.username,
                email: user.email,
            },
        });

    } catch (err) {
        console.error('Error during user login:', err);
        res.status(500).send('Server Error');
    }
};
