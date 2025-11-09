-- Add column to track if insufficient partners notification has been sent
-- This column is used to prevent duplicate notifications for the same booking

ALTER TABLE booking 
ADD COLUMN is_insufficient_partners_notification_sent BOOLEAN NOT NULL DEFAULT FALSE;

-- Add index for better query performance when checking bookings that need notification
CREATE INDEX idx_booking_insufficient_partners_check 
ON booking(status, start_at, is_insufficient_partners_notification_sent) 
WHERE status IN ('PENDING', 'PARTIALLY_ACCEPTED') 
AND is_insufficient_partners_notification_sent = FALSE;

