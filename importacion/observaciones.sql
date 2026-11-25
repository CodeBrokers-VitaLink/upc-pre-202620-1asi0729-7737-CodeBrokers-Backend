-- Apply once before starting production (ddl-auto=validate).
CREATE TABLE IF NOT EXISTS doctor_observations (
 id varchar(255) NOT NULL PRIMARY KEY,
 patient_id varchar(255), doctor_id varchar(255), doctor_name varchar(255),
 text varchar(2000) NOT NULL, created_at datetime(6), attended_at datetime(6),
 INDEX idx_observation_patient (patient_id, created_at)
);
CREATE TABLE IF NOT EXISTS observation_notifications (
 id varchar(255) NOT NULL PRIMARY KEY,
 recipient_id varchar(255), observation_id varchar(255), patient_id varchar(255),
 doctor_name varchar(255), created_at datetime(6), read_at datetime(6),
 email_status varchar(255), email_attempts integer NOT NULL,
 INDEX idx_notification_recipient (recipient_id, created_at),
 INDEX idx_notification_email (email_status, created_at)
);
