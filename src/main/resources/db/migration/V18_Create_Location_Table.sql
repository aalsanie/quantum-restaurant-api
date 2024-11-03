CREATE TABLE location (
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    restaurant_id UUID PRIMARY KEY REFERENCES restaurants(id)
);
