-- Script tạo bảng cho hệ thống combo sản phẩm

-- Bảng combos: Lưu thông tin về combo sản phẩm
CREATE TABLE IF NOT EXISTS `combos` (
    `id` int NOT NULL AUTO_INCREMENT,
    `name` varchar(100) NOT NULL,
    `description` varchar(255) DEFAULT NULL,
    `original_price` decimal(10,2) NOT NULL DEFAULT '0.00',
    `discount_percent` decimal(5,2) NOT NULL DEFAULT '5.00',
    `final_price` decimal(10,2) NOT NULL DEFAULT '0.00',
    `status` tinyint NOT NULL DEFAULT '1' COMMENT '1=active, 0=inactive',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Bảng combo_products: Lưu quan hệ giữa combo và sản phẩm
CREATE TABLE IF NOT EXISTS `combo_products` (
    `id` int NOT NULL AUTO_INCREMENT,
    `combo_id` int NOT NULL,
    `product_id` int NOT NULL,
    PRIMARY KEY (`id`),
    KEY `combo_id` (`combo_id`),
    KEY `product_id` (`product_id`),
    CONSTRAINT `combo_products_ibfk_1` FOREIGN KEY (`combo_id`) REFERENCES `combos` (`id`) ON DELETE CASCADE,
    CONSTRAINT `combo_products_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Thêm cột combo_id vào bảng order_detail để lưu thông tin khi khách hàng đặt combo
ALTER TABLE `order_detail`
ADD COLUMN IF NOT EXISTS `combo_id` int DEFAULT NULL,
ADD CONSTRAINT `order_detail_ibfk_3` FOREIGN KEY (`combo_id`) REFERENCES `combos` (`id`) ON DELETE SET NULL;

-- Dữ liệu mẫu cho bảng combos
INSERT INTO `combos` (`name`, `description`, `original_price`, `discount_percent`, `final_price`, `status`) VALUES
('Breakfast Combo', 'Cappuccino và Croissant', 2.92, 10.00, 2.63, 1),
('Tea Time Combo', 'Green Tea và Black Tea', 2.92, 5.00, 2.77, 1),
('Coffee Lover', 'Espresso, Americano và Latte', 5.01, 15.00, 4.26, 1);

-- Dữ liệu mẫu cho bảng combo_products
INSERT INTO `combo_products` (`combo_id`, `product_id`) VALUES
(1, 2), -- Cappuccino trong Breakfast Combo
(1, 7), -- Croissant trong Breakfast Combo
(2, 5), -- Green Tea trong Tea Time Combo
(2, 6), -- Black Tea trong Tea Time Combo
(3, 1), -- Espresso trong Coffee Lover
(3, 3), -- Latte trong Coffee Lover
(3, 4); -- Americano trong Coffee Lover