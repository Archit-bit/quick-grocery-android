# Quick Grocery (MVP)

Minimal native Android quick-commerce app built with Kotlin, Jetpack Compose, MVVM, Repository pattern, Coroutines/Flow, Hilt, and Navigation Compose.

**Features**
- Home: search, category chips, product grid with Add
- Product detail: info, quantity stepper, add to cart
- Cart: item list with quantity controls, total, place order dialog
- Auth: login/register
- Orders: place order + status
- Owner dashboard (web): manage orders + inventory

**Tech Stack**
- Kotlin + Gradle Kotlin DSL
- Jetpack Compose (Material 3)
- Hilt DI
- Coroutines/Flow
- Navigation Compose

## Open In Android Studio
1. Open Android Studio.
2. Select **Open** and choose this folder:
   `/home/fuzzyenigma/Desktop/Inventory/Android/Quick Grocery`
3. Let Gradle sync complete.

## Run On Emulator
1. Open **Device Manager** in Android Studio.
2. Create or start an emulator (API 24+).
3. Press **Run**.

## Install On Real Phone (USB)
1. Enable **Developer options** and **USB debugging** on the device.
2. Connect the device via USB and accept the RSA prompt.
3. Run:

```bash
adb devices
./gradlew assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## Troubleshooting ADB
- **unauthorized**: Reconnect USB and accept the RSA prompt on the phone.
- **no device**: Ensure USB debugging is enabled and try a different cable/port.
- **missing platform-tools**: Install Android SDK Platform-Tools and ensure `adb` is in your PATH.

## Tests
```bash
./gradlew test
```

## Backend (Required)
The Android app now uses the backend in `backend/` (Express + PostgreSQL).

1. Install Node.js and PostgreSQL.
2. Create your database + user, then run the schema:

```bash
psql -U quickgrocery_user -d quickgrocery_db -f backend/db/schema.sql
```

3. Set environment variables in `backend/.env`:

```bash
PORT=3000
DB_USER=quickgrocery_user
DB_HOST=localhost
DB_DATABASE=quickgrocery_db
DB_PASSWORD=your_secure_db_password
DB_PORT=5432
JWT_SECRET=a_very_long_and_random_secret_key_for_jwt
```

4. Install dependencies and start the server:

```bash
cd backend
npm install
npm start
```

5. Make sure the app points to your LAN IP in `gradle.properties`:

```
API_BASE_URL=http://192.168.29.29:3000/
```

## Owner Web Dashboard
Open the owner dashboard in a browser:

```
http://192.168.29.29:3000/owner
```

Use an account with `role = 'owner'`. Promote an existing user in PostgreSQL:

```sql
UPDATE users SET role = 'owner' WHERE email = 'owner@example.com';
```

The dashboard lets the owner:
- View all orders and order items
- Update order status
- Update product price and stock

## End-to-End Check (Phone + Backend)
1. Start the backend (`npm run dev`).
2. Launch the Android app on your phone.
3. Register or log in.
4. Add items to cart and place an order.
5. Open `http://192.168.29.29:3000/owner` and log in as the owner.
6. Confirm the new order appears and update its status.
