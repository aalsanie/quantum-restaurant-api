CREATE TABLE stock_transaction (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    inventory_item_id UUID NOT NULL,
    quantity DOUBLE PRECISION NOT NULL,
    type VARCHAR(50) NOT NULL, -- PURCHASE, USAGE, ADJUSTMENT
    date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    note TEXT,
    FOREIGN KEY (inventory_item_id) REFERENCES inventory_item(id) ON DELETE CASCADE
);
