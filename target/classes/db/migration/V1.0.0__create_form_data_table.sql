CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE form_data (
    id VARCHAR(36) PRIMARY KEY DEFAULT uuid_generate_v4()::text,
    original_filename VARCHAR(255) NOT NULL,
    extracted_fields TEXT,
    status VARCHAR(20) NOT NULL,
    processed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    error_message TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_form_data_status ON form_data(status);
CREATE INDEX idx_form_data_processed_at ON form_data(processed_at);
CREATE INDEX idx_form_data_filename ON form_data(original_filename);

CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_form_data_updated_at
    BEFORE UPDATE ON form_data
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();