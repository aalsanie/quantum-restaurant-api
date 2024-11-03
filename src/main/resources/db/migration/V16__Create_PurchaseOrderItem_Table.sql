CREATE TABLE purchase_order_item (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    purchase_order_id UUID NOT NULL,
    inventory_item_id UUID NOT NULL,
    quantity DOUBLE PRECISION NOT NULL,
    price_per_unit DOUBLE PRECISION NOT NULL,
    FOREIGN KEY (purchase_order_id) REFERENCES purchase_order(id) ON DELETE CASCADE,
    FOREIGN KEY (inventory_item_id) REFERENCES inventory_item(id) ON DELETE CASCADE
);
