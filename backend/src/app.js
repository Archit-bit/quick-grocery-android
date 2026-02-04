const express = require('express');
const path = require('path');
const dotenv = require('dotenv');
const productRoutes = require('./routes/productRoutes');
const authRoutes = require('./routes/authRoutes');
const categoryRoutes = require('./routes/categoryRoutes');
const cartRoutes = require('./routes/cartRoutes');
const orderRoutes = require('./routes/orderRoutes'); // Import order routes
const adminRoutes = require('./routes/adminRoutes');

dotenv.config(); // Load environment variables from .env file

const app = express();
const port = process.env.PORT || 3000;
const ownerDir = path.join(__dirname, '../public/owner');

app.use(express.json()); // Enable JSON body parsing
app.use(express.static('public'));
app.use('/owner', express.static(ownerDir, { index: 'index.html' }));

// Basic route
app.get('/', (req, res) => {
    res.send('Quick Grocery Backend is running!');
});
app.get(['/owner', '/owner/'], (req, res) => {
    res.sendFile(path.join(ownerDir, 'index.html'));
});

// Auth routes
app.use('/api/auth', authRoutes);

// Product routes
app.use('/api/products', productRoutes);

// Category routes
app.use('/api/categories', categoryRoutes);

// Cart routes
app.use('/api/cart', cartRoutes);

// Order routes
app.use('/api/orders', orderRoutes); // Use order routes
// Admin routes
app.use('/api/admin', adminRoutes);

app.listen(port, () => {
    console.log(`Server is running on port ${port}`);
});
