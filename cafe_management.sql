-- Bảng Category
CREATE TABLE `category` (
                            `id` int NOT NULL AUTO_INCREMENT,
                            `category_name` varchar(50) NOT NULL,
                            `description` varchar(255) DEFAULT NULL,
                            PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Bảng Account (Người dùng)
CREATE TABLE `account` (
                           `id` int NOT NULL AUTO_INCREMENT,
                           `full_name` varchar(50) NOT NULL,
                           `password` varchar(255) NOT NULL,
                           `type` tinyint NOT NULL COMMENT '1=admin, 2=manager, 3=employee',
                           `email` varchar(50) DEFAULT NULL,
                           `lock_status` tinyint NOT NULL,
                           PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Bảng Tables (Bàn trong nhà hàng/quán cafe)
CREATE TABLE `tables` (
                          `id` int NOT NULL AUTO_INCREMENT,
                          `table_name` varchar(10) NOT NULL,
                          `capacity` int DEFAULT '4',
                          `floor_number` int NOT NULL,
                          `status` varchar(20) DEFAULT 'available',
                          PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Bảng Units for products
CREATE TABLE `units` (
                         `id` int NOT NULL AUTO_INCREMENT,
                         `name` varchar(20) NOT NULL,
                         PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Bảng Product (Sản phẩm)
CREATE TABLE `product` (
                           `id` int NOT NULL AUTO_INCREMENT,
                           `name` varchar(50) NOT NULL,
                           `category_id` int NOT NULL,
                           `price` decimal(10,2) NOT NULL,
                           `quantity` int DEFAULT '0',
                           `image` text,
                           `unit_id` int DEFAULT '1',
                           `description` varchar(255) DEFAULT NULL,
                           PRIMARY KEY (`id`),
                           FOREIGN KEY (`category_id`) REFERENCES `category`(`id`) ON DELETE CASCADE,
                           FOREIGN KEY (`unit_id`) REFERENCES `units`(`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `orders` (
                          `id` int NOT NULL AUTO_INCREMENT,
                          `user_id` int NOT NULL,
                          `table_id` int DEFAULT NULL,
                          `order_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                          `status` varchar(20) DEFAULT 'Pending',
                          `total_price` decimal(10,2) DEFAULT '0.00',
                          `payment_method` varchar(20) DEFAULT NULL,
                          PRIMARY KEY (`id`),
                          FOREIGN KEY (`user_id`) REFERENCES `account`(`id`) ON DELETE CASCADE,
                          FOREIGN KEY (`table_id`) REFERENCES `tables`(`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Bảng Sizes (Kích thước sản phẩm)
CREATE TABLE `sizes` (
                         `id` int NOT NULL AUTO_INCREMENT,
                         `name` varchar(50) NOT NULL,
                         `symbol` varchar(20) NOT NULL,
                         PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Bảng Product_Sizes (Liên kết sản phẩm với kích thước)
CREATE TABLE `product_sizes` (
                                 `id` INT NOT NULL AUTO_INCREMENT,
                                 `product_id` INT NOT NULL,
                                 `size_id` INT NOT NULL,
                                 `price` DECIMAL(10,2) NOT NULL COMMENT 'Giá cho từng size',
                                 PRIMARY KEY (`id`),
                                 FOREIGN KEY (`product_id`) REFERENCES `product`(`id`) ON DELETE CASCADE,
                                 FOREIGN KEY (`size_id`) REFERENCES `sizes`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Bảng Order_Detail (Chi tiết đơn hàng)
CREATE TABLE `order_detail` (
                                `id` int NOT NULL AUTO_INCREMENT,
                                `order_id` int NOT NULL,
                                `product_id` int NOT NULL,
                                `quantity` int NOT NULL DEFAULT '1',
                                `unit_price` decimal(10,2) NOT NULL,
                                PRIMARY KEY (`id`),
                                FOREIGN KEY (`order_id`) REFERENCES `orders`(`id`) ON DELETE CASCADE,
                                FOREIGN KEY (`product_id`) REFERENCES `product`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Insert Units
INSERT INTO `units` (`id`, `name`) VALUES
                                       (1, 'cup'),
                                       (2, 'piece'),
                                       (3, 'bottle');

-- Insert Categories
INSERT INTO `category` (`category_name`, `description`) VALUES
                                                            ('Coffee', 'Various coffee beverages'),
                                                            ('Tea', 'Selections of tea'),
                                                            ('Pastry', 'Fresh bakery items'),
                                                            ('Smoothie', 'Fruit and yogurt smoothies'),
                                                            ('Sandwich', 'Fresh sandwiches');

-- Insert Account data
INSERT INTO `account` (`full_name`, `password`, `type`, `email`, `lock_status`) VALUES
                                                                                    ('Admin User', 'admin123', 1, 'admin@cafeshop.com', 0),
                                                                                    ('Manager User', 'manager123', 2, 'manager@cafeshop.com', 0),
                                                                                    ('Employee One', 'employee123', 3, 'employee1@cafeshop.com', 0),
                                                                                    ('Employee Two', 'employee123', 3, 'employee2@cafeshop.com', 0);

-- Insert Tables
INSERT INTO `tables` (`table_name`, `capacity`, `floor_number`, `status`) VALUES
                                                                              ('A1', 2, 1, 'available'),
                                                                              ('A2', 2, 1, 'available'),
                                                                              ('A3', 4, 1, 'available'),
                                                                              ('B1', 4, 1, 'available'),
                                                                              ('B2', 6, 1, 'available'),
                                                                              ('C1', 8, 2, 'available'),
                                                                              ('C2', 4, 2, 'available');

-- Insert Sizes
INSERT INTO `sizes` (`name`, `symbol`) VALUES
                                           ('Small', 'S'),
                                           ('Medium', 'M'),
                                           ('Large', 'L');

-- Insert Products with unit_id
INSERT INTO `product` (`name`, `category_id`, `price`, `quantity`, `image`, `unit_id`, `description`) VALUES
                                                                                                          ('Espresso', 1, 35000, 100, 'espresso.jpg', 1, 'Strong Italian coffee'),
                                                                                                          ('Cappuccino', 1, 45000, 100, 'cappuccino.jpg', 1, 'Espresso with steamed milk and foam'),
                                                                                                          ('Latte', 1, 45000, 100, 'latte.jpg', 1, 'Espresso with lots of steamed milk'),
                                                                                                          ('Americano', 1, 40000, 100, 'americano.jpg', 1, 'Espresso with hot water'),
                                                                                                          ('Green Tea', 2, 35000, 100, 'green_tea.jpg', 1, 'Classic green tea'),
                                                                                                          ('Black Tea', 2, 35000, 100, 'black_tea.jpg', 1, 'Strong black tea'),
                                                                                                          ('Croissant', 3, 25000, 50, 'croissant.jpg', 2, 'Buttery French pastry'),
                                                                                                          ('Mango Smoothie', 4, 50000, 50, 'mango_smoothie.jpg', 1, 'Fresh mango smoothie'),
                                                                                                          ('Chicken Sandwich', 5, 55000, 30, 'chicken_sandwich.jpg', 2, 'Grilled chicken sandwich');

-- Insert Product Sizes
INSERT INTO `product_sizes` (`product_id`, `size_id`, `price`) VALUES
                                                                   (1, 1, 35000),
                                                                   (1, 2, 40000),
                                                                   (1, 3, 45000),
                                                                   (2, 1, 45000),
                                                                   (2, 2, 50000),
                                                                   (2, 3, 55000),
                                                                   (3, 1, 45000),
                                                                   (3, 2, 50000),
                                                                   (3, 3, 55000),
                                                                   (4, 1, 40000),
                                                                   (4, 2, 45000),
                                                                   (4, 3, 50000),
                                                                   (5, 1, 35000),
                                                                   (5, 2, 40000),
                                                                   (5, 3, 45000),
                                                                   (6, 1, 35000),
                                                                   (6, 2, 40000),
                                                                   (6, 3, 45000),
                                                                   (7, 1, 25000),
                                                                   (8, 2, 50000),
                                                                   (8, 3, 60000),
                                                                   (9, 1, 55000),
                                                                   (9, 2, 65000);

-- Insert Sample Orders
INSERT INTO `orders` (`user_id`, `table_id`, `order_date`, `status`, `total_price`, `payment_method`) VALUES
                                                                                                          (3, 1, '2023-11-01 08:30:00', 'Completed', 135000, 'Cash'),
                                                                                                          (3, 2, '2023-11-01 09:15:00', 'Completed', 90000, 'Card'),
                                                                                                          (4, 3, '2023-11-01 10:00:00', 'Completed', 195000, 'Cash'),
                                                                                                          (3, 4, '2023-11-02 14:30:00', 'Completed', 120000, 'Card'),
                                                                                                          (4, 5, CURRENT_TIMESTAMP, 'Pending', 115000, NULL);

-- Insert Order Details
INSERT INTO `order_detail` (`order_id`, `product_id`, `quantity`, `unit_price`) VALUES
                                                                                    (1, 1, 1, 45000),
                                                                                    (1, 2, 1, 50000),
                                                                                    (1, 7, 2, 25000),
                                                                                    (2, 4, 2, 45000),
                                                                                    (3, 3, 2, 50000),
                                                                                    (3, 8, 1, 60000),
                                                                                    (3, 9, 1, 55000),
                                                                                    (4, 5, 1, 40000),
                                                                                    (4, 6, 2, 40000),
                                                                                    (5, 2, 1, 55000),
                                                                                    (5, 8, 1, 60000);