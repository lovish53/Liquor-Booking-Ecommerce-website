# Liquor Booking

Full-stack liquor e-commerce booking application with a Spring Boot backend and a React frontend.

## Project Structure

- `backend/workspace/ws-1/liquor-booking-backend`
  - Spring Boot REST API
  - MySQL database integration
  - JWT authentication
  - Google OAuth support
  - Razorpay payment integration

- `frontend/workspace/ws-1/liquoar-booking-frontend`
  - React + Vite frontend
  - Customer shop, cart, checkout, and order history
  - Admin dashboard and management pages

## Main Features

- Public product catalog visible without login.
- JWT login and signup with separate pages.
- Optional Google OAuth login.
- Cart with item count in header.
- Cart page with quantity update and remove controls.
- Razorpay checkout integration.
- User order / booking history.
- Admin dashboard with revenue charts.
- Admin payment status management.
- Admin stock management.
- Admin add/update bottle details and prices.
- Admin user management.
- Admin can add users, update users, enable/disable users, and assign admin role.
- Animated premium UI with responsive design.

## Backend Setup

- Requirements:
  - Java 17
  - MySQL
  - Maven wrapper included

- Backend path:

```bash
cd backend/workspace/ws-1/liquor-booking-backend
```

- Configure database and payment values in:

```text
src/main/resources/application.properties
```

- Required values:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/liquordb?createDatabaseIfNotExist=true&allowPublicKeyRetrieval=true&useSSL=false
spring.datasource.username=YOUR_DB_USERNAME
spring.datasource.password=YOUR_DB_PASSWORD

razorpay.key-id=YOUR_RAZORPAY_KEY_ID
razorpay.key-secret=YOUR_RAZORPAY_KEY_SECRET
```

- Optional Google OAuth values:

```text
GOOGLE_CLIENT_ID
GOOGLE_CLIENT_SECRET
```

- Start backend:

```bash
./mvnw spring-boot:run
```

- On Windows:

```bash
mvnw.cmd spring-boot:run
```

- Backend runs on:

```text
http://localhost:8080
```

## Frontend Setup

- Requirements:
  - Node.js
  - npm

- Frontend path:

```bash
cd frontend/workspace/ws-1/liquoar-booking-frontend
```

- Install dependencies:

```bash
npm install
```

- Start frontend:

```bash
npm run dev
```

- Frontend runs on:

```text
http://localhost:5173
```

## Useful API Areas

- Auth:
  - `POST /api/v1/auth/login`
  - `POST /api/v1/auth/signup`

- Catalog:
  - `GET /api/v1/liquors`
  - `GET /api/v1/categories`
  - `GET /api/v1/brands`

- Cart:
  - `GET /api/v1/cart/{userId}`
  - `POST /api/v1/cart/items`
  - `PATCH /api/v1/cart/items/{cartItemId}`
  - `DELETE /api/v1/cart/items/{cartItemId}`

- Checkout and payment:
  - `POST /api/v1/checkout`
  - `POST /api/v1/payments/razorpay/orders/{orderId}`
  - `POST /api/v1/payments/razorpay/verify`

- Admin:
  - `GET /api/v1/admin/dashboard`
  - `GET /api/v1/admin/orders`
  - `GET /api/v1/admin/users`
  - `POST /api/v1/admin/users`
  - `PUT /api/v1/admin/users/{userId}`
  - `PATCH /api/v1/admin/users/{userId}`
  - `PATCH /api/v1/admin/orders/{orderId}/payment-status`

## Default Admin

- Seeded admin account:

```text
Email: admin@liquor.local
Password: Admin@123
```

