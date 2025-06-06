# EV Connect Backend API Documentation

## Table of Contents
1. [Authentication](#authentication)
2. [User Management](#user-management)
3. [Charging Station Management](#charging-station-management)
4. [Booking Management](#booking-management)
5. [Transaction Management](#transaction-management)
6. [Rating System](#rating-system)
7. [Chat System](#chat-system)

## Authentication

### Register User
- **Endpoint**: `POST /api/v1/auth/register`
- **Request Body**:
```json
{
    "name": "string",
    "email": "string",
    "password": "string",
    "phoneNumber": "string"
}
```
- **Response**: Returns AuthResponse with JWT token
- **Usage**: Call this API when a new user signs up for the application

### Login User
- **Endpoint**: `POST /api/v1/auth/login`
- **Request Body**:
```json
{
    "email": "string",
    "password": "string"
}
```
- **Response**: Returns AuthResponse with JWT token
- **Usage**: Call this API for user login

## User Management

### Get User Profile
- **Endpoint**: `GET /api/v1/users/profile`
- **Headers**: Authorization: Bearer {token}
- **Response**: Returns user profile details
- **Usage**: Call this API to fetch logged-in user's profile

### Update User Profile
- **Endpoint**: `PUT /api/v1/users/profile`
- **Headers**: Authorization: Bearer {token}
- **Request Body**:
```json
{
    "name": "string",
    "phoneNumber": "string",
    "password": "string" // Optional
}
```
- **Usage**: Call this API when user updates their profile

### Switch User Role
- **Endpoint**: `PUT /api/v1/users/role`
- **Headers**: Authorization: Bearer {token}
- **Request Body**:
```json
{
    "role": "string" // "STATION_OWNER" or "USER"
}
```
- **Usage**: Call this API when user wants to switch between regular user and station owner roles

## Charging Station Management

### Create Charging Station
- **Endpoint**: `POST /api/v1/stations`
- **Headers**: Authorization: Bearer {token}
- **Request Body**:
```json
{
    "name": "string",
    "description": "string",
    "address": "string",
    "latitude": number,
    "longitude": number,
    "pricePerHour": number,
    "connectorType": "string",
    "powerOutput": number,
    "imageFile": binary // Optional
}
```
- **Usage**: Call this API when station owner creates a new charging station

### Get Nearby Stations
- **Endpoint**: `GET /api/v1/stations/nearby`
- **Query Parameters**:
  - latitude: number
  - longitude: number
  - radius: number (in kilometers)
- **Response**: Returns list of nearby charging stations
- **Usage**: Call this API to show available charging stations on map or list view

### Get Station Details
- **Endpoint**: `GET /api/v1/stations/{stationId}`
- **Response**: Returns detailed information about a specific station
- **Usage**: Call this API when user selects a specific station to view details

### Get User's Stations
- **Endpoint**: `GET /api/v1/stations/user`
- **Headers**: Authorization: Bearer {token}
- **Response**: Returns list of stations owned by the user
- **Usage**: Call this API in station owner's dashboard

### Enable/Disable Station
- **Endpoint**: `PUT /api/v1/stations/{stationId}/enable` or `PUT /api/v1/stations/{stationId}/disable`
- **Headers**: Authorization: Bearer {token}
- **Usage**: Call these APIs when station owner wants to enable/disable their station

## Booking Management

### Create Booking
- **Endpoint**: `POST /api/v1/bookings`
- **Headers**: Authorization: Bearer {token}
- **Request Body**:
```json
{
    "stationId": number,
    "startTime": "ISO-8601 datetime",
    "endTime": "ISO-8601 datetime"
}
```
- **Usage**: Call this API when user wants to book a charging slot

### Get User's Bookings
- **Endpoint**: `GET /api/v1/bookings`
- **Headers**: Authorization: Bearer {token}
- **Response**: Returns list of user's bookings
- **Usage**: Call this API to show user's booking history/upcoming bookings

### Get Station's Bookings
- **Endpoint**: `GET /api/v1/bookings/station/{stationId}`
- **Headers**: Authorization: Bearer {token}
- **Response**: Returns list of bookings for a specific station
- **Usage**: Call this API in station owner's dashboard to view bookings

### Cancel Booking
- **Endpoint**: `PUT /api/v1/bookings/{bookingId}/cancel`
- **Headers**: Authorization: Bearer {token}
- **Usage**: Call this API when user wants to cancel their booking

## Transaction Management

### Create Transaction
- **Endpoint**: `POST /api/v1/transactions`
- **Headers**: Authorization: Bearer {token}
- **Request Body**:
```json
{
    "bookingId": number,
    "amount": number,
    "paymentMethod": "string"
}
```
- **Usage**: Call this API when processing payment for a booking

### Get User's Transactions
- **Endpoint**: `GET /api/v1/transactions`
- **Headers**: Authorization: Bearer {token}
- **Response**: Returns list of user's transactions
- **Usage**: Call this API to show transaction history

## Rating System

### Create Rating
- **Endpoint**: `POST /api/v1/ratings`
- **Headers**: Authorization: Bearer {token}
- **Request Body**:
```json
{
    "stationId": number,
    "rating": number,
    "comment": "string"
}
```
- **Usage**: Call this API after booking completion to allow user to rate the station

### Get Station Ratings
- **Endpoint**: `GET /api/v1/ratings/station/{stationId}`
- **Response**: Returns list of ratings for a specific station
- **Usage**: Call this API to display station reviews

## Chat System

### WebSocket Connection
- **Connect URL**: `ws://domain/ws`
- **Headers**: Authorization: Bearer {token}
- **Usage**: Connect to this WebSocket endpoint when initializing chat

### Send Message
- **Destination**: `/app/chat`
- **Message Format**:
```json
{
    "recipientId": number,
    "content": "string"
}
```
- **Usage**: Send to this destination to send a new message

### Subscribe to Personal Messages
- **Destination**: `/user/queue/messages`
- **Usage**: Subscribe to this destination to receive personal messages

### Get Chat History
- **Endpoint**: `GET /api/v1/chat/history/{userId}`
- **Headers**: Authorization: Bearer {token}
- **Response**: Returns chat history with specific user
- **Usage**: Call this API when opening a chat conversation

## Error Handling
All APIs may return the following error responses:
- 400 Bad Request: Invalid input
- 401 Unauthorized: Invalid or missing token
- 403 Forbidden: Insufficient permissions
- 404 Not Found: Resource not found
- 500 Internal Server Error: Server-side error

## Authentication
- All protected endpoints require JWT token in Authorization header
- Format: `Authorization: Bearer {token}`
- Token is obtained from login/register responses
- Token expiration: 24 hours

## Best Practices
1. Always handle error responses appropriately
2. Implement token refresh mechanism
3. Implement proper loading states during API calls
4. Cache appropriate responses
5. Implement retry mechanism for failed requests
6. Maintain WebSocket connection with reconnection logic#   e v C o n n e c t  
 