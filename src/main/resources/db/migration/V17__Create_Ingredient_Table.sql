CREATE TABLE Ingredient (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    menu_item_id UUID REFERENCES MenuItem(id),
    inventory_item_id UUID REFERENCES InventoryItem(id),
    quantity DOUBLE PRECISION NOT NULL
);
