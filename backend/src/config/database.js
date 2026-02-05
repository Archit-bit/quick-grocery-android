const { Pool } = require('pg');
const dotenv = require('dotenv');

dotenv.config();

const isProduction = process.env.NODE_ENV === 'production';

// Railway provides DATABASE_URL; fallback to individual vars for local dev
const poolConfig = process.env.DATABASE_URL
    ? {
        connectionString: process.env.DATABASE_URL,
        ssl: isProduction ? { rejectUnauthorized: false } : false,
        max: 20,
        idleTimeoutMillis: 30000,
        connectionTimeoutMillis: 10000,
    }
    : {
        user: process.env.DB_USER,
        host: process.env.DB_HOST,
        database: process.env.DB_DATABASE,
        password: process.env.DB_PASSWORD,
        port: process.env.DB_PORT,
    };

const pool = new Pool(poolConfig);

// Connection logging
pool.on('connect', () => {
    console.log('✅ Database connected successfully');
});

pool.on('error', (err, client) => {
    console.error('❌ Unexpected database error:', err.message);
    if (isProduction) {
        console.error('Database connection lost. The pool will attempt to reconnect.');
    } else {
        process.exit(-1);
    }
});

// Test connection on startup
const testConnection = async () => {
    try {
        const client = await pool.connect();
        console.log(`📦 Database pool initialized (${isProduction ? 'production' : 'development'} mode)`);
        client.release();
    } catch (err) {
        console.error('❌ Failed to connect to database:', err.message);
        if (isProduction) {
            console.log('⏳ Will retry connection when queries are made...');
        }
    }
};

testConnection();

module.exports = {
    query: (text, params) => pool.query(text, params),
    pool,
};

