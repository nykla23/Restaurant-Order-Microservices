-- 菜品分类表
CREATE TABLE IF NOT EXISTS categories (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '分类ID',
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    description VARCHAR(255) COMMENT '分类描述',
    sort_order INT DEFAULT 0 COMMENT '排序',
    is_active BOOLEAN DEFAULT true COMMENT '是否启用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_name (name)
);

-- 菜品表
CREATE TABLE IF NOT EXISTS dishes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '菜品ID',
    name VARCHAR(100) NOT NULL COMMENT '菜品名称',
    category_id INT NOT NULL COMMENT '分类ID',
    price DECIMAL(10,2) NOT NULL COMMENT '价格',
    description TEXT COMMENT '描述',
    image_url VARCHAR(255) COMMENT '图片URL',
    stock INT DEFAULT 0 COMMENT '库存',
    total_sold INT DEFAULT 0 COMMENT '总销量',
    is_popular BOOLEAN DEFAULT false COMMENT '是否热门',
    is_available BOOLEAN DEFAULT true COMMENT '是否可用',
    preparation_time INT DEFAULT 15 COMMENT '预估制作时间（分钟）',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_category_id (category_id),
    INDEX idx_is_popular (is_popular),
    INDEX idx_is_available (is_available),
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

-- 库存变更日志表
CREATE TABLE IF NOT EXISTS inventory_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '日志ID',
    dish_id BIGINT NOT NULL COMMENT '菜品ID',
    change_amount INT NOT NULL COMMENT '变更数量（正数增加，负数减少）',
    current_stock INT NOT NULL COMMENT '变更后库存',
    type VARCHAR(20) NOT NULL COMMENT '类型：ORDER-订单，RESTOCK-补货，ADJUST-调整',
    related_id VARCHAR(50) COMMENT '关联ID（如订单ID）',
    notes TEXT COMMENT '备注',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_dish_id (dish_id),
    FOREIGN KEY (dish_id) REFERENCES dishes(id)
);

-- 订单表
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '订单ID',
    order_number VARCHAR(50) UNIQUE NOT NULL COMMENT '订单号',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    table_number VARCHAR(20) COMMENT '桌号',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '总金额',
    discount_amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '折扣金额',
    actual_amount DECIMAL(10,2) NOT NULL COMMENT '实付金额',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态：PENDING-待确认，CONFIRMED-已确认，PREPARING-制作中，READY-已出餐，COMPLETED-已完成，CANCELLED-已取消',
    payment_status VARCHAR(20) DEFAULT 'UNPAID' COMMENT '支付状态：UNPAID-未支付，PAID-已支付，REFUNDED-已退款',
    special_instructions TEXT COMMENT '特殊要求',
    estimated_preparation_time INT COMMENT '预估制作时间（分钟）',
    completed_at TIMESTAMP NULL COMMENT '完成时间',
    cancelled_at TIMESTAMP NULL COMMENT '取消时间',
    cancelled_reason VARCHAR(255) COMMENT '取消原因',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
);

-- 订单项表
CREATE TABLE IF NOT EXISTS order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '订单项ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    dish_id BIGINT NOT NULL COMMENT '菜品ID',
    dish_name VARCHAR(100) NOT NULL COMMENT '菜品名称（快照）',
    quantity INT NOT NULL COMMENT '数量',
    unit_price DECIMAL(10,2) NOT NULL COMMENT '单价（快照）',
    subtotal DECIMAL(10,2) NOT NULL COMMENT '小计',
    special_instructions TEXT COMMENT '特殊要求',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_order_id (order_id),
    INDEX idx_dish_id (dish_id),
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (dish_id) REFERENCES dishes(id)
);

-- 支付记录表
CREATE TABLE IF NOT EXISTS payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '支付ID',
    payment_number VARCHAR(50) UNIQUE NOT NULL COMMENT '支付流水号',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    amount DECIMAL(10,2) NOT NULL COMMENT '支付金额',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态：PENDING-待支付，SUCCESS-成功，FAILED-失败，REFUNDED-已退款',
    payment_method VARCHAR(20) COMMENT '支付方式：CASH-现金，WECHAT-微信，ALIPAY-支付宝，CARD-银行卡',
    transaction_id VARCHAR(100) COMMENT '第三方交易ID',
    payer_id VARCHAR(50) COMMENT '付款人ID',
    payer_info TEXT COMMENT '付款人信息',
    paid_at TIMESTAMP NULL COMMENT '支付时间',
    refunded_at TIMESTAMP NULL COMMENT '退款时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_order_id (order_id),
    INDEX idx_payment_number (payment_number)
);