USE book_store;

CREATE TABLE users (
    user_id        INT AUTO_INCREMENT PRIMARY KEY,
    username       VARCHAR(50) NOT NULL UNIQUE,
    email          VARCHAR(100) NOT NULL UNIQUE,
    password_hash  VARCHAR(255) NOT NULL,
    role           ENUM('customer', 'manager') NOT NULL DEFAULT 'customer',
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE books (
    book_id        INT AUTO_INCREMENT PRIMARY KEY,
    title          VARCHAR(255) NOT NULL,
    author         VARCHAR(255) NOT NULL,
    price_buy      DECIMAL(10, 2) NOT NULL,
    price_rent     DECIMAL(10, 2) NOT NULL,
    is_available   BOOLEAN DEFAULT TRUE, 
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE orders (
    order_id       INT AUTO_INCREMENT PRIMARY KEY,
    user_id        INT NOT NULL,
    payment_status ENUM('Pending', 'Paid') DEFAULT 'Pending',
    total_amount   DECIMAL(10, 2) NOT NULL,
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE order_items (
    order_item_id  INT AUTO_INCREMENT PRIMARY KEY,
    order_id       INT NOT NULL,
    book_id        INT NOT NULL,
    item_type      ENUM('buy', 'rent') NOT NULL,
    price          DECIMAL(10, 2) NOT NULL,

    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES books(book_id)
);

