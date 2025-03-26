-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: Mar 02, 2025 at 12:19 PM
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
                           `full_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                           `password` varchar(255) NOT NULL,
                           `type` tinyint NOT NULL COMMENT '1=admin, 2=manager, 3=employee',
                           `email` varchar(50) DEFAULT NULL,
                           `lock_status` tinyint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `account`
--

INSERT INTO `account` (`id`, `full_name`, `password`, `type`, `email`, `lock_status`) VALUES
                                                                                          (1, 'John Doe', '123', 1, 'john@example.com', 0),
                                                                                          (2, 'Jane Smith', '$2y$10$Fwq2beROd0yHP9dtbHxifuFvZdDDNucBMbidaN39E6qwKBaV0ByQm\n', 2, 'jane@example.com', 0),
                                                                                          (3, 'Bob Lee', '$2y$10$Fwq2beROd0yHP9dtbHxifuFvZdDDNucBMbidaN39E6qwKBaV0ByQm', 3, 'bob@example.com', 1),
                                                                                          (4, 'Alice Wong', '$2y$10$Fwq2beROd0yHP9dtbHxifuFvZdDDNucBMbidaN39E6qwKBaV0ByQm', 3, 'alice@example.com', 0),
                                                                                          (5, 'Tom Brown', '$2y$10$Fwq2beROd0yHP9dtbHxifuFvZdDDNucBMbidaN39E6qwKBaV0ByQm', 3, 'tom@example.com', 0);

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
                                                                  (1, 'Beverages', 'Various drinks such as coffee, tea, juices, smoothies'),
                                                                  (2, 'Pastries', 'Various pastries served with drinks'),
                                                                  (3, 'Snacks', 'Light snacks such as chips, spring rolls, finger foods'),
                                                                  (4, 'Combos', 'Combo deals of drinks and food with special offers'),
                                                                  (5, 'Promotions', 'Special promotional programs for customers');

-- --------------------------------------------------------

--
-- Table structure for table `customers`
--

CREATE TABLE `customers` (
                             `id` int NOT NULL,
                             `name` varchar(50) NOT NULL,
                             `age` int DEFAULT NULL,
                             `phone` varchar(20) DEFAULT NULL,
                             `email` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `customers`
--

INSERT INTO `customers` (`id`, `name`, `age`, `phone`, `email`) VALUES
                                                                    (1, 'John Doe', 28, '1234567890', 'john.doe@example.com'),
                                                                    (2, 'Jane Smith', 32, '0987654321', 'jane.smith@example.com'),
                                                                    (3, 'Michael Brown', 25, '1122334455', 'michael.brown@example.com'),
                                                                    (4, 'Emily Davis', 22, '2233445566', 'emily.davis@example.com'),
                                                                    (5, 'David Wilson', 35, '3344556677', 'david.wilson@example.com');

-- --------------------------------------------------------

--
-- Table structure for table `orders`
--

CREATE TABLE `orders` (
                          `id` int NOT NULL,
                          `user_id` int NOT NULL,
                          `table_id` int NOT NULL,
                          `order_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                          `status` varchar(20) DEFAULT 'Pending',
                          `total_price` decimal(10,2) DEFAULT '0.00',
                          `payment_method` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `orders`
--

INSERT INTO `orders` (`id`, `user_id`, `table_id`, `order_date`, `status`, `total_price`, `payment_method`) VALUES
                                                                                                                (7, 1, 1, '2025-02-25 03:30:00', 'Pending', '150.00', 'Cash'),
                                                                                                                (8, 2, 2, '2025-02-25 04:00:00', 'Completed', '250.00', 'Credit Card'),
                                                                                                                (9, 3, 3, '2025-02-25 05:15:00', 'Cancelled', '0.00', 'Cash'),
                                                                                                                (10, 4, 4, '2025-02-25 06:00:00', 'Processing', '320.00', 'E-Wallet'),
                                                                                                                (11, 5, 5, '2025-02-25 07:45:00', 'Completed', '200.00', 'Credit Card');

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
                                                                                          (21, 7, 6, 2, '50000.00'),
                                                                                          (22, 8, 7, 1, '60000.00'),
                                                                                          (23, 9, 7, 3, '40000.00'),
                                                                                          (24, 10, 8, 2, '70000.00'),
                                                                                          (25, 10, 9, 5, '30000.00');

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
                           `unit_id` int NOT NULL,
                           `description` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `product`
--

INSERT INTO `product` (`id`, `name`, `category_id`, `price`, `quantity`, `image`, `unit_id`, `description`) VALUES
                                                                                                                (6, 'Espresso', 1, '50000.00', 100, 'espresso.jpg', 1, 'Strong and rich coffee'),
                                                                                                                (7, 'Cappuccino', 1, '60000.00', 80, 'cappuccino.jpg', 1, 'Coffee with steamed milk and foam'),
                                                                                                                (8, 'Green Tea', 2, '40000.00', 50, 'green_tea.jpg', 2, 'Refreshing green tea'),
                                                                                                                (9, 'Cheesecake', 3, '70000.00', 30, 'cheesecake.jpg', 3, 'Creamy cheesecake with a graham crust'),
                                                                                                                (10, 'Croissant', 3, '30000.00', 40, 'croissant.jpg', 3, 'Flaky and buttery croissant');

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
                                                                    (1, 'Table 1', 4, 1, 'Available'),
                                                                    (2, 'Table 2', 2, 1, 'Occupied'),
                                                                    (3, 'Table 3', 6, 2, 'Reserved'),
                                                                    (4, 'Table 4', 4, 2, 'Available'),
                                                                    (5, 'Table 5', 8, 3, 'Occupied');

-- --------------------------------------------------------

--
-- Table structure for table `units`
--

CREATE TABLE `units` (
                         `id` int NOT NULL,
                         `unit_name` varchar(50) NOT NULL,
                         `symbol` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `units`
--

INSERT INTO `units` (`id`, `unit_name`, `symbol`) VALUES
                                                      (1, 'Cup', 'cup'),
                                                      (2, 'Bottle', 'btl'),
                                                      (3, 'Piece', 'pcs'),
                                                      (4, 'Glass', 'gls'),
                                                      (5, 'Can', 'can');

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
-- Indexes for table `customers`
--
ALTER TABLE `customers`
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
    MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `category`
--
ALTER TABLE `category`
    MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `customers`
--
ALTER TABLE `customers`
    MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `orders`
--
ALTER TABLE `orders`
    MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT for table `order_detail`
--
ALTER TABLE `order_detail`
    MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=26;

--
-- AUTO_INCREMENT for table `product`
--
ALTER TABLE `product`
    MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `tables`
--
ALTER TABLE `tables`
    MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `units`
--
ALTER TABLE `units`
    MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `orders`
--
ALTER TABLE `orders`
    ADD CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `account` (`id`),
  ADD CONSTRAINT `orders_ibfk_2` FOREIGN KEY (`table_id`) REFERENCES `tables` (`id`);

--
-- Constraints for table `order_detail`
--
ALTER TABLE `order_detail`
    ADD CONSTRAINT `order_detail_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`),
  ADD CONSTRAINT `order_detail_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`);

--
-- Constraints for table `product`
--
ALTER TABLE `product`
    ADD CONSTRAINT `product_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`),
  ADD CONSTRAINT `product_ibfk_2` FOREIGN KEY (`unit_id`) REFERENCES `units` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
