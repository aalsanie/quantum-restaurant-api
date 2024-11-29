CREATE TABLE tables (
    id INT PRIMARY KEY,
    floor_id UUID NOT NULL,
    table_number INT NOT NULL,
    capacity INT NOT NULL,
    status VARCHAR(20),
    table_type VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (floor_id) REFERENCES floors(id) ON DELETE CASCADE
);
