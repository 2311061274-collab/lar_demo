-- V11: Add revisit date, doctor diagnosis, daily follow-up tracking and user-customer links

-- 1. Add follow-up & diagnosis fields to appointments
ALTER TABLE appointments
    ADD COLUMN IF NOT EXISTS revisit_date DATE,
    ADD COLUMN IF NOT EXISTS revisit_notes TEXT,
    ADD COLUMN IF NOT EXISTS diagnosis TEXT,
    ADD COLUMN IF NOT EXISTS prescription TEXT,
    ADD COLUMN IF NOT EXISTS requires_daily_followup BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS followup_days INT NOT NULL DEFAULT 3,
    ADD COLUMN IF NOT EXISTS last_followup_at TIMESTAMP;

-- 2. Create pet_daily_followups table for owners to log post-visit recovery
CREATE TABLE IF NOT EXISTS pet_daily_followups (
    id BIGSERIAL PRIMARY KEY,
    appointment_id BIGINT NOT NULL,
    pet_id BIGINT NOT NULL,
    customer_id BIGINT NOT NULL,
    day_number INT NOT NULL,
    log_date DATE NOT NULL,
    eating_status VARCHAR(50),
    temperature_status VARCHAR(50),
    energy_status VARCHAR(50),
    symptoms_notes TEXT NOT NULL,
    clinic_feedback TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_followup_appointment
        FOREIGN KEY (appointment_id)
            REFERENCES appointments(id)
            ON DELETE CASCADE,

    CONSTRAINT fk_followup_pet
        FOREIGN KEY (pet_id)
            REFERENCES pets(id)
            ON DELETE CASCADE,

    CONSTRAINT fk_followup_customer
        FOREIGN KEY (customer_id)
            REFERENCES customers(id)
            ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_followup_appointment ON pet_daily_followups(appointment_id);
CREATE INDEX IF NOT EXISTS idx_followup_customer ON pet_daily_followups(customer_id);

-- 3. Enhance users table to link with customer profile
ALTER TABLE users
    ADD COLUMN IF NOT EXISTS customer_id BIGINT,
    ADD COLUMN IF NOT EXISTS phone VARCHAR(30),
    ADD COLUMN IF NOT EXISTS full_name VARCHAR(255);

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'fk_users_customer'
    ) THEN
        ALTER TABLE users
            ADD CONSTRAINT fk_users_customer
                FOREIGN KEY (customer_id)
                    REFERENCES customers(id)
                    ON DELETE SET NULL;
    END IF;
END $$;
