-- Bảng Combo (Lưu thông tin combo)
CREATE TABLE `combo` (
    `id` int NOT NULL AUTO_INCREMENT,
    `name` varchar(100) NOT NULL,
    `description` text,
    `price` decimal(10,2) NOT NULL COMMENT 'Giá combo sau khi giảm',
    `original_price` decimal(10,2) NOT NULL COMMENT 'Tổng giá gốc các sản phẩm',
    `status` varchar(20) DEFAULT 'active' COMMENT 'active/inactive',
    `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Bảng Combo_Detail (Chi tiết sản phẩm trong combo)
CREATE TABLE `combo_detail` (
    `id` int NOT NULL AUTO_INCREMENT,
    `combo_id` int NOT NULL,
    `product_id` int NOT NULL,
    `product_size_id` int DEFAULT NULL,
    `quantity` int NOT NULL DEFAULT '1',
    PRIMARY KEY (`id`),
    FOREIGN KEY (`combo_id`) REFERENCES `combo`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`product_id`) REFERENCES `product`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`product_size_id`) REFERENCES `product_sizes`(`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Thêm trường combo_id vào bảng order_detail
ALTER TABLE `order_detail`
ADD COLUMN `combo_id` int DEFAULT NULL,
ADD FOREIGN KEY (`combo_id`) REFERENCES `combo`(`id`) ON DELETE SET NULL;