-- V16: Add admin confirmation for daily follow-up tracking with default value
ALTER TABLE pet_daily_followups
    ADD COLUMN IF NOT EXISTS confirmed_by_admin BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS confirmed_at TIMESTAMP,
    ADD COLUMN IF NOT EXISTS confirmed_by_username VARCHAR(100);
