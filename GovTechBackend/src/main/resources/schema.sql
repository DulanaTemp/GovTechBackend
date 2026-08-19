-- Idempotent schema for the GovTech Smart Parking backend.
-- Runs on startup via spring.sql.init. Safe to re-run (IF NOT EXISTS / guarded enum creation).

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

DO $$ BEGIN
    CREATE TYPE ticket_status_enum AS ENUM ('ACTIVE', 'COMPLETED', 'CANCELLED');
EXCEPTION WHEN duplicate_object THEN null; END $$;

DO $$ BEGIN
    CREATE TYPE payment_status_enum AS ENUM ('PENDING', 'SUCCESS', 'FAILED');
EXCEPTION WHEN duplicate_object THEN null; END $$;

-- 1. Users
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    phone_number VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(100),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 2. OTP Verifications
CREATE TABLE IF NOT EXISTS otp_verifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    phone_number VARCHAR(20) NOT NULL,
    otp_hash VARCHAR(255) NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    is_verified BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 3. Parking Lots
CREATE TABLE IF NOT EXISTS parking_lots (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(150) NOT NULL,
    latitude DECIMAL(10, 8) NOT NULL,
    longitude DECIMAL(11, 8) NOT NULL,
    total_spots INT NOT NULL CHECK (total_spots >= 0),
    available_spots INT NOT NULL CHECK (available_spots >= 0 AND available_spots <= total_spots),
    hourly_rate DECIMAL(8, 2) NOT NULL CHECK (hourly_rate >= 0.00),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 4. Tickets (includes vehicle_number)
CREATE TABLE IF NOT EXISTS tickets (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    lot_id UUID NOT NULL REFERENCES parking_lots(id) ON DELETE RESTRICT,
    vehicle_number VARCHAR(20),
    start_time TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    end_time TIMESTAMPTZ,
    status ticket_status_enum NOT NULL DEFAULT 'ACTIVE',
    total_amount DECIMAL(8, 2) CHECK (total_amount >= 0.00),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT check_time_sequence CHECK (end_time IS NULL OR end_time >= start_time)
);

-- 5. Payments
CREATE TABLE IF NOT EXISTS payments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ticket_id UUID NOT NULL UNIQUE REFERENCES tickets(id) ON DELETE RESTRICT,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    amount DECIMAL(8, 2) NOT NULL CHECK (amount >= 0.00),
    payment_method VARCHAR(50) NOT NULL,
    status payment_status_enum NOT NULL DEFAULT 'PENDING',
    transaction_ref VARCHAR(100) UNIQUE,
    paid_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes
CREATE INDEX IF NOT EXISTS idx_users_phone ON users(phone_number);
CREATE INDEX IF NOT EXISTS idx_otp_phone ON otp_verifications(phone_number);
CREATE INDEX IF NOT EXISTS idx_parking_lots_coords ON parking_lots(latitude, longitude);
CREATE INDEX IF NOT EXISTS idx_tickets_user ON tickets(user_id);
CREATE INDEX IF NOT EXISTS idx_tickets_lot ON tickets(lot_id);
CREATE INDEX IF NOT EXISTS idx_tickets_status ON tickets(status);
CREATE INDEX IF NOT EXISTS idx_payments_ticket ON payments(ticket_id);
CREATE INDEX IF NOT EXISTS idx_payments_user ON payments(user_id);
