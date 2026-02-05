# Quick Grocery

A native Android quick-commerce app with a Node.js backend, deployed on Railway.

## 🚀 Live Demo

- **Backend API**: `https://athletic-endurance-production.up.railway.app`
- **Owner Dashboard**: `https://athletic-endurance-production.up.railway.app/owner`

## ✨ Features

**Customer (Android App)**
- Browse products by category
- Search products
- Add to cart with quantity controls
- Place orders with delivery address
- View order history

**Owner (Web Dashboard)**
- View and manage all orders
- Update order status (pending → processing → delivered)
- Manage inventory and pricing

## 🛠 Tech Stack

| Component | Technology |
|-----------|------------|
| Android App | Kotlin, Jetpack Compose, Material 3, MVVM, Hilt, Coroutines/Flow |
| Backend | Node.js, Express 5, PostgreSQL |
| Hosting | Railway (Backend + PostgreSQL) |
| Auth | JWT tokens |

---

## 📱 Install the Android App

### Option 1: From Source (Android Studio)

1. Clone this repository
2. Open in Android Studio
3. Update `gradle.properties` with your backend URL:
   ```properties
   API_BASE_URL=https://your-backend.up.railway.app/
   ```
4. Build and run on device/emulator

### Option 2: Install APK via USB

```bash
# Connect phone with USB debugging enabled
adb devices

# Build and install
./gradlew assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

---

## 🌐 Deploy Backend to Railway

### 1. Create Railway Project

1. Go to [railway.app](https://railway.app) and sign in with GitHub
2. Click **New Project** → **Deploy from GitHub repo**
3. Select this repository
4. Set **Root Directory** to: `backend`

### 2. Add PostgreSQL Database

1. In your Railway project, click **+ New** → **Database** → **PostgreSQL**
2. Wait for provisioning

### 3. Configure Environment Variables

In the backend service, add these variables:

| Variable | Value |
|----------|-------|
| `DATABASE_URL` | (auto-linked from PostgreSQL) |
| `JWT_SECRET` | `your_random_secret_key` |
| `NODE_ENV` | `production` |

### 4. Generate Domain

1. Go to **Settings** → **Networking**
2. Click **Generate Domain**
3. Note your URL: `https://your-app.up.railway.app`

### 5. Initialize Database

Connect to Railway PostgreSQL (via DBeaver or psql) and run:

```sql
-- Run schema
\i backend/db/schema.sql

-- Add sample categories
INSERT INTO categories (name) VALUES 
('Dairy'), ('Fruits'), ('Vegetables'), ('Bakery'), 
('Beverages'), ('Snacks'), ('Grains'), ('Meat');

-- Add sample products
INSERT INTO products (name, description, price, category_id, stock_quantity) VALUES 
('Fresh Milk', 'Pasteurized full cream milk', 2.49, 1, 100),
('Bananas', 'Fresh organic bananas', 1.29, 2, 200),
('Baby Spinach', 'Fresh baby spinach leaves', 2.29, 3, 60),
('Whole Wheat Bread', 'Freshly baked bread', 2.99, 4, 40),
('Orange Juice', '100% pure orange juice', 3.99, 5, 70),
('Potato Chips', 'Classic salted chips', 2.49, 6, 90),
('Basmati Rice', 'Long grain rice, 1 kg', 4.99, 7, 60),
('Chicken Breast', 'Fresh boneless chicken', 8.99, 8, 40);
```

### 6. Create Owner Account

```sql
-- Create owner (password: owner123)
INSERT INTO users (username, email, password_hash, full_name, role) 
VALUES (
    'owner', 
    'owner@quickgrocery.com', 
    '$2b$10$rICGQv4eHn9c.NhRGRq5vuLpN.TmMMxKCg4Y2EoHzVxlZJzKBXdCi',
    'Store Owner',
    'owner'
);
```

Login at `https://your-app.up.railway.app/owner` with:
- Email: `owner@quickgrocery.com`
- Password: `owner123`

---

## 🔧 Local Development

### Backend

```bash
cd backend
npm install

# Create .env file
cat > .env << EOF
PORT=3000
DB_USER=quickgrocery_user
DB_HOST=localhost
DB_DATABASE=quickgrocery_db
DB_PASSWORD=your_password
DB_PORT=5432
JWT_SECRET=dev_secret_key
EOF

# Start server
npm start
```

### Android App

Update `gradle.properties`:
```properties
API_BASE_URL=http://YOUR_LAN_IP:3000/
```

Build and run via Android Studio or:
```bash
./gradlew assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

---

## 📂 Project Structure

```
├── app/                    # Android app (Kotlin/Compose)
│   └── src/main/java/com/quickgrocery/
│       ├── data/           # Data models, repository interface
│       ├── di/             # Hilt dependency injection
│       ├── network/        # API service, network repository
│       └── ui/             # Compose screens and viewmodels
├── backend/                # Node.js backend
│   ├── db/schema.sql       # Database schema
│   ├── public/owner/       # Owner dashboard (static HTML)
│   └── src/
│       ├── config/         # Database configuration
│       ├── controllers/    # Route handlers
│       ├── middleware/     # Auth middleware
│       └── routes/         # API routes
└── gradle.properties       # Android build config
```

---

## 📄 License

MIT
