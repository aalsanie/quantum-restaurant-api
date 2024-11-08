CREATE TABLE tables (
    id INT PRIMARY KEY,
    layout_id UUID REFERENCES restaurant_layouts(id),
    table_number INT NOT NULL,
    capacity INT NOT NULL,
    status VARCHAR(20),
    table_type VARCHAR(20), -- New field for distinguishing table types
    floor_number INT,       -- New field for floor number
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
