# Godfellas Tattoo Studio POS System API

## Overview
This API provides comprehensive financial tracking, inventory management, and artist management for Godfellas Tattoo Studio.

## Key Features
- **Automated Financial Calculations** - All percentages and splits calculated automatically
- **Daily Income & Expense Tracking** - Real-time profit calculations
- **Inventory Management** - Stock tracking with low-stock alerts
- **Artist Management** - Resident/Guest categorization with filtering
- **Dashboard Summary** - Quick overview of daily operations

## API Endpoints

### Financial Management
- `GET /api/financials/daily/{date}` - Get daily financial summary
- `GET /api/financials/today` - Get today's financials
- `POST /api/financials/income/{date}?source={source}&amount={amount}` - Update income source

### Artist Management
- `GET /api/artists` - Get all artists
- `GET /api/artists/active` - Get active artists only
- `GET /api/artists/residents` - Get resident artists
- `GET /api/artists/guests` - Get guest artists
- `POST /api/artists` - Add new artist
- `PUT /api/artists/{id}` - Update artist
- `DELETE /api/artists/{id}` - Deactivate artist

### Inventory Management
- `GET /api/inventory` - Get all active inventory items
- `GET /api/inventory/low-stock` - Get low stock alerts
- `POST /api/inventory` - Add new inventory item
- `PUT /api/inventory/{id}/stock?quantity={qty}` - Update stock quantity
- `PUT /api/inventory/{id}/restock?additionalQuantity={qty}` - Restock item

### Expense Tracking
- `GET /api/expenses` - Get all expenses
- `GET /api/expenses/today` - Get today's expenses
- `POST /api/expenses` - Add new expense

### Billing
- `POST /api/billing` - Create new billing (auto-updates daily financials)
- `GET /api/billing` - Get all billings

### Dashboard
- `GET /api/dashboard/summary` - Get complete dashboard summary

## Income Sources
- `customeradvance` - Customer advance payments
- `tattooing` - Tattoo services
- `tattooremoval` - Tattoo removal services
- `piercing` - Piercing services
- `productsale` - Product sales
- `etc` - Other income

## Automated Calculations
- **13% Studio Cut** - Automatically calculated from total earnings
- **Artist Payment** - Remaining amount after studio cut
- **Dimu Payment** - 50% of net payment
- **Tattoo Removal Split** - 30%/70% split
- **Daily Profit** - Total Income - Total Expenses

## Artist Categories
- `RESIDENT` - Permanent studio artists
- `GUEST` - Temporary/visiting artists

## Sample Requests

### Add Artist
```json
POST /api/artists
{
  "name": "John Doe",
  "category": "RESIDENT",
  "style": "Traditional",
  "yearsOfExperience": 5,
  "specialization": "Black & Grey",
  "hourlyRate": 150.00,
  "contactInfo": "john@example.com"
}
```

### Update Income
```
POST /api/financials/income/2024-01-15?source=tattooing&amount=500.00
```

### Add Expense
```json
POST /api/expenses
{
  "category": "Supplies",
  "description": "Tattoo needles",
  "amount": 75.00,
  "paymentMethod": "Cash"
}
```