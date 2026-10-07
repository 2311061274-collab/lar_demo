-- V13: Add username column to reviews table to link review with author account
ALTER TABLE reviews
    ADD COLUMN IF NOT EXISTS username VARCHAR(100);

CREATE INDEX IF NOT EXISTS idx_reviews_username ON reviews(username);
