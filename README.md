# Quantum Restaurant API

## Introduction
The **Quantum Restaurant API** is a RESTful API designed for managing restaurant operations efficiently. It includes functionalities for handling employees, inventory, menus, orders, and more, tailored to streamline restaurant workflows.

### Features
- Employee Management: Add, update, and retrieve employee data.
- Inventory Management: Track stock and manage inventory transactions.
- Menu and Orders: Handle menu items and customer orders seamlessly.

---

## Architecture and Stack
### Architecture
- **Layered Design**: Separation of concerns across Controller, Service, and Repository layers.
- **Database First**: Entities and relationships are defined using JPA annotations.
- **Transactional Services**: Ensures data consistency across operations.

### Tech Stack
- **Java 17**: Core language.
- **Spring Boot 3.0**: Application framework.
- **Hibernate (JPA)**: ORM for database interactions.
- **PostgreSQL**: Database.
- **Swagger UI**: API documentation and testing.

---

## Setup and Run Instructions
### Prerequisites
1. Java 17 or later.
2. PostgreSQL database installed and running.
3. Maven for building the project.

### Steps
1. Clone the repository:
   ```bash
   git clone <repository_url>
   cd quantum-restaurant-api

2. Configure the database:

    Update `application.yaml` with your PostgreSQL credentials.
    Build the project:
    
    ```bash
    mvn clean install
    ```

3. Run the application:
    ````
    bash
    java -jar target/quantum-restaurant-api.jar
    ````
4. Access the API:

    API documentation: http://localhost:8080

## Available Endpoints
| Endpoint                                   | Method | Description                              | Success Response           | Failure Response          |
|-------------------------------------------|--------|------------------------------------------|----------------------------|---------------------------|
| `/api/employees/restaurants/{restaurantId}` | POST   | Add a new employee to a restaurant       | `201 Created` (Employee)   | `400 Bad Request`         |
| `/api/employees/restaurants/{restaurantId}` | GET    | Get all employees for a restaurant       | `200 OK` (List<Employee>)  | `404 Not Found`           |
| `/api/employees/{id}`                      | GET    | Get details of an employee by ID         | `200 OK` (Employee)        | `404 Not Found`           |
| `/api/employees/{id}`                      | PUT    | Update employee details                  | `200 OK` (Employee)        | `400 Bad Request`         |
| `/api/employees/{id}`                      | DELETE | Delete an employee                       | `204 No Content`           | `404 Not Found`           |
| `/api/inventory/{restaurantId}`            | POST   | Add an inventory item                    | `201 Created` (InventoryItem) | `400 Bad Request`       |
| `/api/inventory/{restaurantId}`            | GET    | Get all inventory items for a restaurant | `200 OK` (List<InventoryItem>) | `404 Not Found`       |
| `/api/inventory/item/{itemId}`             | GET    | Get details of an inventory item         | `200 OK` (InventoryItem)   | `404 Not Found`           |
| `/api/inventory/item/{itemId}`             | PUT    | Update inventory item details            | `200 OK` (InventoryItem)   | `400 Bad Request`         |
| `/api/inventory/item/{itemId}`             | DELETE | Delete an inventory item                 | `204 No Content`           | `404 Not Found`           |
| `/api/menu/{menuId}`                       | GET    | Retrieve menu items                      | `200 OK` (List<MenuItem>)  | `404 Not Found`           |
| `/api/menu/{menuId}`                       | POST   | Add a menu item to a menu                | `201 Created` (MenuItem)   | `400 Bad Request`         |
| `/api/menu/item/{menuItemId}`              | GET    | Get details of a menu item               | `200 OK` (MenuItem)        | `404 Not Found`           |
| `/api/menu/item/{menuItemId}`              | PUT    | Update menu item details                 | `200 OK` (MenuItem)        | `400 Bad Request`         |
| `/api/menu/item/{menuItemId}`              | DELETE | Delete a menu item                       | `204 No Content`           | `404 Not Found`           |
| `/api/orders/{restaurantId}`               | POST   | Create a new order                       | `201 Created` (Order)      | `400 Bad Request`         |
| `/api/orders/{restaurantId}`               | GET    | Get all orders for a restaurant          | `200 OK` (List<Order>)     | `404 Not Found`           |
| `/api/orders/{orderId}`                    | GET    | Get details of an order                  | `200 OK` (Order)           | `404 Not Found`           |
| `/api/orders/{orderId}`                    | PUT    | Update an order                          | `200 OK` (Order)           | `400 Bad Request`         |
| `/api/orders/{orderId}`                    | DELETE | Delete an order                          | `204 No Content`           | `404 Not Found`           |
| `/api/restaurants`                         | POST   | Register a new restaurant                | `201 Created` (Restaurant) | `400 Bad Request`         |
| `/api/restaurants`                         | GET    | Get a list of all restaurants            | `200 OK` (List<Restaurant>)| `404 Not Found`           |
| `/api/restaurants/{restaurantId}`          | GET    | Get details of a restaurant by ID        | `200 OK` (Restaurant)      | `404 Not Found`           |
| `/api/restaurants/{restaurantId}`          | PUT    | Update restaurant details                | `200 OK` (Restaurant)      | `400 Bad Request`         |
| `/api/restaurants/{restaurantId}`          | DELETE | Delete a restaurant                      | `204 No Content`           | `404 Not Found`           |
| `/api/restaurant-layouts/{layoutId}`       | GET    | Get details of a restaurant layout       | `200 OK` (RestaurantLayout)| `404 Not Found`           |
| `/api/restaurant-layouts/{layoutId}`       | PUT    | Update a restaurant layout               | `200 OK` (RestaurantLayout)| `400 Bad Request`         |
| `/api/restaurant-layouts`                  | POST   | Create a new restaurant layout           | `201 Created` (RestaurantLayout) | `400 Bad Request`    |
| `/api/restaurant-layouts/{layoutId}`       | DELETE | Delete a restaurant layout               | `204 No Content`           | `404 Not Found`           |


## Available Endpoints (Continued)
| Endpoint                                   | Method | Description                              | Success Response           | Failure Response          |
|-------------------------------------------|--------|------------------------------------------|----------------------------|---------------------------|
| `/api/tables/{tableId}`                    | GET    | Get details of a specific table          | `200 OK` (Table)           | `404 Not Found`           |
| `/api/tables/{tableId}`                    | PUT    | Update details of a table                | `200 OK` (Table)           | `400 Bad Request`         |
| `/api/tables/{tableId}`                    | DELETE | Delete a table                           | `204 No Content`           | `404 Not Found`           |
| `/api/tables`                              | POST   | Add a new table                          | `201 Created` (Table)      | `400 Bad Request`         |
| `/api/tables/restaurant/{restaurantId}`    | GET    | Get all tables for a restaurant          | `200 OK` (List<Table>)     | `404 Not Found`           |
| `/api/reservations/{restaurantId}`         | POST   | Create a reservation                     | `201 Created` (Reservation)| `400 Bad Request`         |
| `/api/reservations/{reservationId}`        | GET    | Get details of a reservation             | `200 OK` (Reservation)     | `404 Not Found`           |
| `/api/reservations/{reservationId}`        | PUT    | Update a reservation                     | `200 OK` (Reservation)     | `400 Bad Request`         |
| `/api/reservations/{reservationId}`        | DELETE | Cancel a reservation                     | `204 No Content`           | `404 Not Found`           |
| `/api/payments/{orderId}`                  | POST   | Process payment for an order             | `201 Created` (Payment)    | `400 Bad Request`         |
| `/api/payments/{paymentId}`                | GET    | Get payment details                      | `200 OK` (Payment)         | `404 Not Found`           |
| `/api/payments/{paymentId}`                | PUT    | Update payment details                   | `200 OK` (Payment)         | `400 Bad Request`         |
| `/api/payments/{paymentId}`                | DELETE | Cancel a payment                         | `204 No Content`           | `404 Not Found`           |

---

## Notes on Response Format
All API responses follow a standard JSON format. Here’s an example for successful and failure responses:

### Success Response
```json
{
    "status": "success",
    "data": { ... }
}
```

## License

*Copyright 2024 Ahmad Al-Sanie*

All rights reserved. Unauthorized copying, distribution, or modification of this software, via any medium, is strictly prohibited without the explicit written consent of the author.
This software is provided "as is," without warranty of any kind, express or implied, including but not limited to the warranties of merchantability, fitness for a particular purpose, or non-infringement. 
In no event shall the author be liable for any claim, damages, or other liability arising from, out of, or in connection with the software or its use.
This project is licensed under a **Proprietary License**. Unauthorized use, modification, or distribution is strictly prohibited. For more details, refer to the [LICENSE](./LICENSE) file.

