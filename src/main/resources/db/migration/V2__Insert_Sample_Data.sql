-- Insert a sample restaurant (Pending status initially)
INSERT INTO restaurants (name, email, password_hash, status)
VALUES ('The Fancy Fork', 'fancyfork@example.com', 'hashedpassword', 'PENDING');

-- After approval, update status
UPDATE restaurants SET status = 'APPROVED' WHERE email = 'fancyfork@example.com';

-- Insert a layout for the restaurant
INSERT INTO restaurant_layouts (restaurant_id, num_floors)
VALUES (
    (SELECT id FROM restaurants WHERE email = 'fancyfork@example.com'),
    2
);