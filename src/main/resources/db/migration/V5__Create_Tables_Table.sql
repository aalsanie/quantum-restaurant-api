CREATE TABLE tables (
    id INT PRIMARY KEY,
    layout_id UUID REFERENCES restaurant_layouts(id),
    table_number INT NOT NULL,
    capacity INT NOT NULL,
    status VARCHAR(20),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);