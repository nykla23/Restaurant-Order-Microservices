-- 创建微服务数据库
CREATE DATABASE IF NOT EXISTS ro_user_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS ro_order_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS ro_notification_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS nacos_config CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 创建应用用户并授权
CREATE USER IF NOT EXISTS 'restaurant_app'@'%' IDENTIFIED BY 'restaurant_password';
GRANT ALL PRIVILEGES ON ro_user_db.* TO 'restaurant_app'@'%';
GRANT ALL PRIVILEGES ON ro_order_db.* TO 'restaurant_app'@'%';
GRANT ALL PRIVILEGES ON ro_notification_db.* TO 'restaurant_app'@'%';
GRANT ALL PRIVILEGES ON nacos_config.* TO 'restaurant_app'@'%';
FLUSH PRIVILEGES;