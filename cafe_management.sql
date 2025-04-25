-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: Apr 25, 2025 at 03:24 PM
-- Server version: 8.0.30
-- PHP Version: 8.1.10

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `cafe_management`
--

-- --------------------------------------------------------

--
-- Table structure for table `account`
--

CREATE TABLE `account` (
                           `id` int NOT NULL,
                           `full_name` varchar(50) NOT NULL,
                           `password` varchar(255) NOT NULL,
                           `type` tinyint NOT NULL COMMENT '1=admin, 2=manager, 3=employee',
                           `email` varchar(50) DEFAULT NULL,
                           `lock_status` tinyint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `account`
--

INSERT INTO `account` (`id`, `full_name`, `password`, `type`, `email`, `lock_status`) VALUES
                                                                                          (3, 'Employee One', 'employee123', 3, 'employee1@cafeshop.com', 0),
                                                                                          (4, 'Employee Two', 'employee123', 3, 'employee2@cafeshop.com', 0),
                                                                                          (5, 'Admin', '$2a$12$7L/fA1kDfnVlhI4DGqUUdeiQMG/c81CMn1bmcwuzOzr/HyKMBa3zi', 1, 'nguyenngocduc@gmail.com', 0);

-- --------------------------------------------------------

--
-- Table structure for table `category`
--

CREATE TABLE `category` (
                            `id` int NOT NULL,
                            `category_name` varchar(50) NOT NULL,
                            `description` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `category`
--

INSERT INTO `category` (`id`, `category_name`, `description`) VALUES
                                                                  (1, 'Coffee', 'Various coffee beverages'),
                                                                  (2, 'Tea', 'Selections of tea'),
                                                                  (3, 'Pastry', 'Fresh bakery items'),
                                                                  (4, 'Smoothie', 'Fruit and yogurt smoothies');

-- --------------------------------------------------------

--
-- Table structure for table `orders`
--

CREATE TABLE `orders` (
                          `id` int NOT NULL,
                          `user_id` int NOT NULL,
                          `table_id` int DEFAULT NULL,
                          `order_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                          `status` varchar(20) DEFAULT 'Pending',
                          `total_price` decimal(10,2) DEFAULT '0.00',
                          `payment_method` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `orders`
--

INSERT INTO `orders` (`id`, `user_id`, `table_id`, `order_date`, `status`, `total_price`, `payment_method`) VALUES
                                                                                                                (1, 3, NULL, '2023-11-01 01:30:00', 'Completed', '5.63', 'Cash'),
                                                                                                                (2, 3, 2, '2023-11-01 02:15:00', 'Completed', '3.75', 'Card'),
                                                                                                                (3, 3, 3, '2023-11-01 03:00:00', 'Completed', '8.13', 'Cash'),
                                                                                                                (4, 3, 4, '2023-11-02 07:30:00', 'Completed', '5.00', 'Card'),
                                                                                                                (5, 4, 5, '2025-04-12 14:15:59', 'Pending', '4.79', NULL),
                                                                                                                (6, 3, NULL, '2025-04-16 15:44:31', 'Pending', '5.41', 'Unknown');

-- --------------------------------------------------------

--
-- Table structure for table `order_detail`
--

CREATE TABLE `order_detail` (
                                `id` int NOT NULL,
                                `order_id` int NOT NULL,
                                `product_id` int NOT NULL,
                                `quantity` int NOT NULL DEFAULT '1',
                                `unit_price` decimal(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `order_detail`
--

INSERT INTO `order_detail` (`id`, `order_id`, `product_id`, `quantity`, `unit_price`) VALUES
                                                                                          (2, 1, 2, 1, '2.08'),
                                                                                          (3, 1, 7, 2, '1.04'),
                                                                                          (4, 2, 4, 2, '1.88'),
                                                                                          (5, 3, 3, 2, '2.08'),
                                                                                          (6, 3, 8, 1, '2.50'),
                                                                                          (8, 4, 5, 1, '1.67'),
                                                                                          (9, 4, 6, 2, '1.67'),
                                                                                          (10, 5, 2, 1, '2.29'),
                                                                                          (11, 5, 8, 1, '2.50'),
                                                                                          (13, 6, 2, 1, '1.88'),
                                                                                          (14, 6, 4, 1, '1.67');

-- --------------------------------------------------------

--
-- Table structure for table `product`
--

CREATE TABLE `product` (
                           `id` int NOT NULL,
                           `name` varchar(50) NOT NULL,
                           `category_id` int NOT NULL,
                           `price` decimal(10,2) NOT NULL,
                           `quantity` int DEFAULT '0',
                           `image` text,
                           `unit_id` int DEFAULT '1',
                           `description` varchar(255) DEFAULT NULL,
                           `status` tinyint NOT NULL DEFAULT '1' COMMENT '1=active (shown), 0=inactive (hidden)'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `product`
--

INSERT INTO `product` (`id`, `name`, `category_id`, `price`, `quantity`, `image`, `unit_id`, `description`, `status`) VALUES
                                                                                                                          (2, 'Cappuccino', 1, '1.88', 100, 'cappuccino.jpg', 1, 'Espresso with steamed milk and foam', 1),
                                                                                                                          (3, 'Latte', 1, '1.88', 100, 'latte.jpg', 1, 'Espresso with lots of steamed milk', 1),
                                                                                                                          (4, 'Americano', 1, '1.67', 100, 'americano.jpg', 1, 'Espresso with hot water', 1),
                                                                                                                          (5, 'Green Tea', 2, '1.46', 100, 'green_tea.jpg', 1, 'Classic green tea', 1),
                                                                                                                          (6, 'Black Tea', 2, '1.46', 100, 'black_tea.jpg', 1, 'Strong black tea', 1),
                                                                                                                          (7, 'Croissant', 3, '1.04', 50, 'croissant.jpg', 2, 'Buttery French pastry', 1),
                                                                                                                          (8, 'Mango Smoothie', 4, '2.08', 50, 'mango_smoothie.jpg', 1, 'Fresh mango smoothie', 1);

-- --------------------------------------------------------

--
-- Table structure for table `product_sizes`
--

CREATE TABLE `product_sizes` (
                                 `id` int NOT NULL,
                                 `product_id` int NOT NULL,
                                 `size_id` int NOT NULL,
                                 `price` decimal(10,2) NOT NULL COMMENT 'Giá cho từng size'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `product_sizes`
--

INSERT INTO `product_sizes` (`id`, `product_id`, `size_id`, `price`) VALUES
                                                                         (4, 2, 1, '1.88'),
                                                                         (5, 2, 2, '2.08'),
                                                                         (6, 2, 3, '2.29'),
                                                                         (7, 3, 1, '1.88'),
                                                                         (8, 3, 2, '2.08'),
                                                                         (9, 3, 3, '2.29'),
                                                                         (10, 4, 1, '1.67'),
                                                                         (11, 4, 2, '1.88'),
                                                                         (12, 4, 3, '2.08'),
                                                                         (13, 5, 1, '1.46'),
                                                                         (14, 5, 2, '1.67'),
                                                                         (15, 5, 3, '1.88'),
                                                                         (16, 6, 1, '1.46'),
                                                                         (17, 6, 2, '1.67'),
                                                                         (18, 6, 3, '1.88'),
                                                                         (19, 7, 1, '1.04'),
                                                                         (20, 8, 2, '2.08'),
                                                                         (21, 8, 3, '2.50');

-- --------------------------------------------------------

--
-- Table structure for table `sizes`
--

CREATE TABLE `sizes` (
                         `id` int NOT NULL,
                         `name` varchar(50) NOT NULL,
                         `symbol` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `sizes`
--

INSERT INTO `sizes` (`id`, `name`, `symbol`) VALUES
                                                 (1, 'Small', 'S'),
                                                 (2, 'Medium', 'M'),
                                                 (3, 'Large', 'L');

-- --------------------------------------------------------

--
-- Table structure for table `tables`
--

CREATE TABLE `tables` (
                          `id` int NOT NULL,
                          `table_name` varchar(10) NOT NULL,
                          `capacity` int DEFAULT '4',
                          `floor_number` int NOT NULL,
                          `status` varchar(20) DEFAULT 'available'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `tables`
--

INSERT INTO `tables` (`id`, `table_name`, `capacity`, `floor_number`, `status`) VALUES
                                                                                    (2, 'A2', 2, 1, 'available'),
                                                                                    (3, 'A3', 4, 1, 'available'),
                                                                                    (4, 'B1', 4, 1, 'available'),
                                                                                    (5, 'B2', 6, 1, 'available'),
                                                                                    (6, 'C1', 8, 2, 'available'),
                                                                                    (7, 'C2', 4, 2, 'available');

-- --------------------------------------------------------

--
-- Table structure for table `units`
--

CREATE TABLE `units` (
                         `id` int NOT NULL,
                         `name` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `units`
--

INSERT INTO `units` (`id`, `name`) VALUES
                                       (1, 'cup'),
                                       (2, 'piece'),
                                       (3, 'bottle');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `account`
--
ALTER TABLE `account`
    ADD PRIMARY KEY (`id`);

--
-- Indexes for table `category`
--
ALTER TABLE `category`
    ADD PRIMARY KEY (`id`);

--
-- Indexes for table `orders`
--
ALTER TABLE `orders`
    ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `table_id` (`table_id`);

--
-- Indexes for table `order_detail`
--
ALTER TABLE `order_detail`
    ADD PRIMARY KEY (`id`),
  ADD KEY `order_id` (`order_id`),
  ADD KEY `product_id` (`product_id`);

--
-- Indexes for table `product`
--
ALTER TABLE `product`
    ADD PRIMARY KEY (`id`),
  ADD KEY `category_id` (`category_id`),
  ADD KEY `unit_id` (`unit_id`);

--
-- Indexes for table `product_sizes`
--
ALTER TABLE `product_sizes`
    ADD PRIMARY KEY (`id`),
  ADD KEY `product_id` (`product_id`),
  ADD KEY `size_id` (`size_id`);

--
-- Indexes for table `sizes`
--
ALTER TABLE `sizes`
    ADD PRIMARY KEY (`id`);

--
-- Indexes for table `tables`
--
ALTER TABLE `tables`
    ADD PRIMARY KEY (`id`);

--
-- Indexes for table `units`
--
ALTER TABLE `units`
    ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `account`
--
ALTER TABLE `account`
    MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `category`
--
ALTER TABLE `category`
    MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT for table `orders`
--
ALTER TABLE `orders`
    MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `order_detail`
--
ALTER TABLE `order_detail`
    MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- AUTO_INCREMENT for table `product`
--
ALTER TABLE `product`
    MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `product_sizes`
--
ALTER TABLE `product_sizes`
    MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=24;

--
-- AUTO_INCREMENT for table `sizes`
--
ALTER TABLE `sizes`
    MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `tables`
--
ALTER TABLE `tables`
    MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `units`
--
ALTER TABLE `units`
    MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `orders`
--
ALTER TABLE `orders`
    ADD CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `account` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `orders_ibfk_2` FOREIGN KEY (`table_id`) REFERENCES `tables` (`id`) ON DELETE SET NULL;

--
-- Constraints for table `order_detail`
--
ALTER TABLE `order_detail`
    ADD CONSTRAINT `order_detail_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `order_detail_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `product`
--
ALTER TABLE `product`
    ADD CONSTRAINT `product_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `product_ibfk_2` FOREIGN KEY (`unit_id`) REFERENCES `units` (`id`) ON DELETE SET NULL;

--
-- Constraints for table `product_sizes`
--
ALTER TABLE `product_sizes`
    ADD CONSTRAINT `product_sizes_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `product_sizes_ibfk_2` FOREIGN KEY (`size_id`) REFERENCES `sizes` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
