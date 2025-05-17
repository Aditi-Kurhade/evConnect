# EvConnect Backend Technical Documentation

## Table of Contents

1. [System Architecture](#system-architecture)
2. [Authentication Flow](#authentication-flow)
3. [Key Components](#key-components)
4. [Database Models](#database-models)
5. [API Endpoints](#api-endpoints)
6. [Error Handling](#error-handling)
7. [Business Logic](#business-logic)
8. [WebSocket Implementation](#websocket-implementation)

## System Architecture

The EvConnect backend is built using Spring Boot and follows a layered architecture:

```
┌───────────────┐     ┌───────────────┐     ┌───────────────┐     ┌───────────────┐
│  Controllers  │────▶│   Services    │────▶│ Repositories  │────▶│   Database    │
└───────────────┘     └───────────────┘     └───────────────┘     └───────────────┘
        │                     │                                            │
        │                     │                                            │
        │                     │                                            │
        │                     ▼                                            │
        │             ┌───────────────┐                                    │
        │             │ DTOs/Models   │◀───────────────────────────────────┘
        │             └───────────────┘
        │                     ▲
        └─────────────────────┘
```

* **Controllers**: Handle HTTP requests, validate input, and delegate to services
* **Services**: Contain business logic and coordinate between repositories
* **Repositories**: Interface with the database through JPA
* **DTOs/Models**: Data transfer objects and entity models

## Authentication Flow

### JWT-Based Authentication

```
┌─────────┐     ┌───────────────┐     ┌───────────────┐     ┌───────────────┐
│  Client │     │ AuthController│     │  AuthService  │     │JwtTokenProvider│
└────┬────┘     └───────┬───────┘     └───────┬───────┘     └───────┬───────┘
     │                  │                     │                     │
     │  Login Request   │                     │                     │
     │─────────────────▶│                     │                     │
     │                  │                     │                     │
     │                  │ Authenticate User   │                     │
     │                  │────────────────────▶│                     │
     │                  │                     │                     │
     │                  │                     │  Generate Token     │
     │                  │                     │────────────────────▶│
     │                  │                     │                     │
     │                  │                     │      Token          │
     │                  │                     │◀────────────────────│
     │                  │                     │                     │
     │  JWT Token       │◀────────────────────│                     │
     │◀─────────────────│                     │                     │
     │                  │                     │                     │
```

1. **Login/Registration**: Client sends credentials to `/auth/login` or `/auth/register`
2. **Authentication**: AuthService validates credentials and generates a JWT token
3. **Token Usage**: Client includes the token in the Authorization header for subsequent requests
4. **Validation**: JwtAuthenticationFilter validates the token for protected endpoints
5. **Logout**: Token is blacklisted in TokenBlacklistService when user logs out

## Key Components

### Controllers

1. **AuthController**: Handles authentication, registration, and logout
   - `POST /auth/register`: Register new users
   - `POST /auth/login`: Authenticate users and return JWT
   - `POST /auth/logout`: Blacklist the current JWT token

2. **UserController**: Manages user profiles and settings
   - `GET /users/profile`: Get user profile information
   - `PUT /users/profile`: Update user profile information
   - `PUT /users/password`: Change user password
   - `PUT /users/switch-role`: Switch between BORROWER and LENDER roles

3. **ChargingStationController**: Manages charging station operations
   - `POST /charging-stations`: Create new charging stations
   - `GET /charging-stations/nearby`: Find nearby charging stations
   - `GET /charging-stations/my-stations`: Get stations owned by the current user
   - `PUT /charging-stations/{id}`: Update station information
   - `PUT /charging-stations/{id}/enable|disable`: Enable/disable a station

4. **BookingController**: Handles booking operations
   - `POST /bookings`: Create a new booking
   - `GET /bookings/my-bookings`: Get bookings for the current user
   - `PUT /bookings/{id}/accept|reject|cancel`: Update booking status

5. **TransactionController**: Manages payment transactions
   - `POST /transactions`: Create a new transaction
   - `GET /transactions/my-transactions`: Get transactions for the current user
   - `POST /transactions/{id}/pay`: Process payment for a transaction
   - `GET /transactions/booking/{bookingId}`: Get transaction by booking ID

6. **ChatController**: Handles real-time messaging
   - `GET /chat/messages/{bookingId}`: Get chat history for a booking
   - WebSocket endpoints for real-time message exchange

### Services

1. **AuthService**: Handles user authentication and registration
   - Validates user credentials
   - Generates JWT tokens
   - Registers new users

2. **UserService**: Manages user profiles
   - Updates user information
   - Changes passwords
   - Handles role switching

3. **ChargingStationService**: Manages charging station operations
   - Creates and updates stations
   - Searches for nearby stations based on location
   - Enables/disables stations

4. **BookingService**: Handles booking logic
   - Creates bookings with availability checks
   - Manages booking status transitions
   - Calculates booking amounts

5. **TransactionService**: Manages payment transactions
   - Creates transactions for bookings
   - Processes payments
   - Verifies user location for in-person transactions

6. **ChatService**: Handles messaging
   - Stores and retrieves chat messages
   - Manages real-time message delivery

7. **TokenBlacklistService**: Manages logged-out JWT tokens
   - Blacklists tokens on logout
   - Checks if tokens are blacklisted
   - Periodically cleans up expired tokens

### Security Components

1. **JwtTokenProvider**: Creates and validates JWT tokens
   - Generates tokens with user information
   - Validates token signatures
   - Extracts user information from tokens

2. **JwtAuthenticationFilter**: Intercepts requests to validate JWTs
   - Extracts tokens from request headers
   - Validates tokens using JwtTokenProvider
   - Sets up Spring Security context

3. **CustomUserDetailsService**: Loads user details for authentication
   - Retrieves user information by username/email
   - Creates Spring Security UserDetails objects

## Database Models

```
┌───────────────────┐      ┌───────────────────┐      ┌───────────────────┐
│       User        │      │  ChargingStation  │      │      Booking      │
├───────────────────┤      ├───────────────────┤      ├───────────────────┤
│ id                │      │ id                │      │ id                │
│ name              │      │ name              │      │ startTime         │
│ email             │◄─────┤ user              │◄─────┤ chargingStation   │
│ phoneNumber       │      │ address           │      │ endTime           │
│ password          │      │ latitude          │      │ borrower          │
│ userType          │──┐   │ longitude         │      │ status            │
│ currentRole       │  │   │ pricePerUnit      │      │ transaction       │──┐
│ isEnabled         │  │   │ isAvailable       │      └───────────────────┘  │
└───────────────────┘  │   │ connectorType     │                             │
                       │   └───────────────────┘                             │
                       │                                                     │
                       │   ┌───────────────────┐      ┌───────────────────┐  │
                       │   │  BlacklistedToken │      │    Transaction    │  │
                       │   ├───────────────────┤      ├───────────────────┤  │
                       │   │ id                │      │ id                │  │
                       │   │ token             │      │ booking           │◄─┘
                       │   │ expiresAt         │      │ amount            │
                       │   └───────────────────┘      │ status            │
                       │                              │ paymentId         │
                       │   ┌───────────────────┐      │ borrowerLatitude  │
                       └──▶│    ChatMessage    │      │ borrowerLongitude │
                           ├───────────────────┤      └───────────────────┘
                           │ id                │
                           │ bookingId         │
                           │ sender            │
                           │ content           │
                           │ timestamp         │
                           └───────────────────┘
```

### Key Database Entities

1. **User**: Stores user information
   - Supports dual roles (BORROWER/LENDER)
   - Contains authentication credentials

2. **ChargingStation**: Represents EV charging stations
   - Contains location information 
   - Pricing and availability details
   - Linked to the owner (LENDER)

3. **Booking**: Represents charging appointments
   - Links BORROWER to ChargingStation
   - Contains time slots and status information
   - Statuses: PENDING, ACCEPTED, REJECTED, CANCELLED, COMPLETED

4. **Transaction**: Represents payment transactions
   - Linked to a Booking
   - Contains payment status and amount
   - Supports location verification

5. **ChatMessage**: Stores communication between users
   - Linked to a Booking
   - Contains sender information and message content

6. **BlacklistedToken**: Stores revoked JWT tokens
   - Used for secure logout functionality
   - Contains expiration time for cleanup

## Error Handling

The system implements a comprehensive error handling strategy through the `GlobalExceptionHandler` class:

```
┌───────────────┐     ┌───────────────┐     ┌───────────────┐
│   Exception   │────▶│ Custom Error  │────▶│  JSON Error   │
│   Thrown      │     │ Exceptions    │     │  Response     │
└───────────────┘     └───────────────┘     └───────────────┘
```

### Error Types and Handling

1. **TransactionExistsException**: Special handling for duplicate transactions
   - Returns HTTP 409 CONFLICT
   - Includes bookingId in the response
   - Uses code "TRANSACTION_EXISTS"
   - Frontend can use this to retrieve existing transaction

2. **RuntimeException**: General application errors
   - Returns HTTP 400 BAD REQUEST
   - Includes error message and path information
   - Uses code "BAD_REQUEST"

3. **General Exceptions**: Unhandled exceptions
   - Returns HTTP 500 INTERNAL SERVER ERROR
   - Includes stack trace information in dev environment
   - Uses code "INTERNAL_SERVER_ERROR"

Error responses follow a consistent format:
```json
{
  "timestamp": "2025-04-25T13:15:20.123",
  "status": 409,
  "code": "TRANSACTION_EXISTS",
  "error": "Conflict",
  "message": "Transaction already exists for this booking",
  "path": "/api/transactions",
  "bookingId": 123
}
```

## Business Logic

### Booking Flow

```
┌──────────┐     ┌───────────┐     ┌───────────┐     ┌───────────┐     ┌───────────┐
│  Create  │────▶│  Pending  │────▶│ Accepted  │────▶│Transaction│────▶│ Completed │
│  Booking │     │  Status   │     │  Status   │     │  Created  │     │  Status   │
└──────────┘     └───────────┘     └───────────┘     └───────────┘     └───────────┘
                       │                 │
                       │                 │
                       ▼                 ▼
                 ┌───────────┐    ┌───────────┐
                 │ Rejected  │    │ Cancelled │
                 │  Status   │    │  Status   │
                 └───────────┘    └───────────┘
```

1. BORROWER creates a booking request (PENDING)
2. LENDER accepts or rejects the booking
3. If accepted, BORROWER can cancel before the appointment
4. After service, LENDER creates a transaction
5. BORROWER confirms and completes payment
6. Booking status changes to COMPLETED

### Transaction Flow

```
┌──────────┐     ┌───────────┐     ┌───────────┐     ┌───────────┐
│  Create  │────▶│  Pending  │────▶│ Location  │────▶│ Completed │
│Transaction│     │  Status   │     │ Verified  │     │  Payment  │
└──────────┘     └───────────┘     └───────────┘     └───────────┘
```

1. LENDER creates a transaction for an accepted booking
2. Transaction starts in PENDING status
3. BORROWER may need to verify their location (optional)
4. BORROWER completes the payment
5. Transaction status changes to COMPLETED

## WebSocket Implementation

Real-time chat functionality is implemented using Spring's WebSocket support:

```
┌───────────┐     ┌───────────┐     ┌───────────┐     ┌───────────┐
│  Client   │     │ WebSocket │     │   Chat    │     │  Database │
│  Browser  │◀───▶│  Server   │◀───▶│  Service  │◀───▶│           │
└───────────┘     └───────────┘     └───────────┘     └───────────┘
```

1. **Connection**: Clients connect to `/api/ws` endpoint
2. **Authentication**: JWT token is validated on connection
3. **Subscription**: Clients subscribe to topics for specific bookings
4. **Messaging**: Messages are sent to `/app/chat/{bookingId}`
5. **Delivery**: Server broadcasts messages to all subscribed clients
6. **Persistence**: Messages are saved to the database for history

## API Endpoints

### Authentication

- `POST /api/auth/register`: Register a new user
- `POST /api/auth/login`: Authenticate and get JWT token
- `POST /api/auth/logout`: Invalidate current JWT token

### User Management

- `GET /api/users/profile`: Get current user profile
- `PUT /api/users/profile`: Update user profile
- `PUT /api/users/password`: Change password
- `PUT /api/users/switch-role`: Switch between BORROWER/LENDER roles

### Charging Stations

- `POST /api/charging-stations`: Create new station
- `GET /api/charging-stations/nearby`: Find nearby stations
- `GET /api/charging-stations/my-stations`: Get user's stations
- `GET /api/charging-stations/{id}`: Get station details
- `PUT /api/charging-stations/{id}`: Update station
- `PUT /api/charging-stations/{id}/enable`: Enable station
- `PUT /api/charging-stations/{id}/disable`: Disable station
- `GET /api/charging-stations/{id}/bookings`: Get station bookings
- `POST /api/charging-stations/{id}/ratings`: Rate a station

### Bookings

- `POST /api/bookings`: Create booking
- `GET /api/bookings/my-bookings`: Get user's bookings
- `GET /api/bookings/{id}`: Get booking details
- `PUT /api/bookings/{id}/accept`: Accept booking (LENDER)
- `PUT /api/bookings/{id}/reject`: Reject booking (LENDER)
- `PUT /api/bookings/{id}/cancel`: Cancel booking (BORROWER)
- `GET /api/bookings/{id}/amount`: Get booking amount

### Transactions

- `POST /api/transactions`: Create transaction
- `GET /api/transactions/my-transactions`: Get user's transactions
- `GET /api/transactions/{id}`: Get transaction details
- `GET /api/transactions/booking/{bookingId}`: Get transaction by booking
- `POST /api/transactions/{id}/confirm-location`: Verify user location
- `POST /api/transactions/{id}/pay`: Process payment

### Chat

- `GET /api/chat/messages/{bookingId}`: Get chat history
- WebSocket: `/api/ws` for connection
- Destination: `/app/chat/{bookingId}` for sending messages
- Subscription: `/topic/chat/{bookingId}` for receiving messages 