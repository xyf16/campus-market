-- 创建数据库
CREATE DATABASE IF NOT EXISTS campus_market 
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE campus_market;

-- 用户表
CREATE TABLE `user` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `username` VARCHAR(50) NOT NULL UNIQUE,
    `password` VARCHAR(100) NOT NULL,
    `nickname` VARCHAR(50),
    `phone` VARCHAR(20),
    `avatar` VARCHAR(200),
    `school` VARCHAR(100),
    `role` TINYINT DEFAULT 0,
    `status` TINYINT DEFAULT 1,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 分类表
CREATE TABLE `category` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `name` VARCHAR(50) NOT NULL,
    `parent_id` INT DEFAULT 0,
    `sort` INT DEFAULT 0
);

-- 商品表
CREATE TABLE `product` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `title` VARCHAR(100) NOT NULL,
    `description` TEXT,
    `price` DECIMAL(10,2) NOT NULL,
    `original_price` DECIMAL(10,2),
    `category_id` INT,
    `images` TEXT,
    `status` TINYINT DEFAULT 1,
    `user_id` BIGINT NOT NULL,
    `view_count` INT DEFAULT 0,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
);

-- 订单表
CREATE TABLE `order` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `order_no` VARCHAR(32) NOT NULL UNIQUE,
    `product_id` BIGINT NOT NULL,
    `buyer_id` BIGINT NOT NULL,
    `seller_id` BIGINT NOT NULL,
    `amount` DECIMAL(10,2) NOT NULL,
    `status` TINYINT DEFAULT 0,
    `remark` VARCHAR(200),
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`product_id`) REFERENCES `product`(`id`)
);

-- 插入分类数据
INSERT INTO `category` (`name`, `sort`) VALUES
('数码产品', 1),
('书籍教材', 2),
('生活用品', 3),
('服饰鞋包', 4),
('运动户外', 5),
('其他', 99);