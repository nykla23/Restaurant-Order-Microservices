-- 1. 插入菜品分类数据
INSERT INTO categories (name, description, sort_order, is_active) VALUES
('开胃前菜', '各类凉菜、沙拉、小吃', 1, true),
('主菜', '各种热炒、炖菜、主食', 2, true),
('汤羹类', '各种汤品和羹类', 3, true),
('饮品', '饮料、酒水、茶饮', 4, true),
('甜品', '各类甜点、冰淇淋', 5, true);

-- 2. 插入菜品数据
INSERT INTO dishes (name, category_id, price, description, image_url, stock, total_sold, is_popular, is_available, preparation_time) VALUES
('凉拌黄瓜', 1, 12.00, '爽口黄瓜配蒜泥酱汁', '/images/cucumber.jpg', 50, 120, true, true, 5),
('夫妻肺片', 1, 38.00, '经典川菜凉菜', '/images/fuqifeipian.jpg', 30, 85, true, true, 10),
('宫保鸡丁', 2, 48.00, '香辣可口的经典川菜', '/images/kungpao.jpg', 40, 150, true, true, 15),
('水煮鱼', 2, 78.00, '麻辣鲜香的水煮鱼片', '/images/boiledfish.jpg', 25, 90, true, true, 20),
('番茄鸡蛋汤', 3, 18.00, '家常番茄鸡蛋汤', '/images/tomatosoup.jpg', 100, 200, false, true, 10),
('珍珠奶茶', 4, 15.00, '经典台式珍珠奶茶', '/images/bubbletea.jpg', 80, 300, true, true, 5),
('提拉米苏', 5, 28.00, '意式经典甜品', '/images/tiramisu.jpg', 20, 60, true, true, 5);

-- 3. 插入订单数据
INSERT INTO orders (order_number, user_id, table_number, total_amount, discount_amount, actual_amount, status, payment_status, special_instructions, estimated_preparation_time, created_at) VALUES
('ORD20231015001', 1001, 'A12', 138.00, 0.00, 138.00, 'COMPLETED', 'PAID', '少放辣椒', 25, '2023-10-15 12:30:00'),
('ORD20231015002', 1002, 'B05', 62.00, 5.00, 57.00, 'PREPARING', 'PAID', '米饭多些', 20, '2023-10-15 18:45:00'),
('ORD20231016001', 1003, NULL, 48.00, 0.00, 48.00, 'PENDING', 'UNPAID', '打包带走', 15, '2023-10-16 11:20:00'),
('ORD20231016002', 1004, 'C08', 106.00, 10.00, 96.00, 'CONFIRMED', 'UNPAID', '不要香菜', 30, '2023-10-16 19:10:00'),
('ORD20231017001', 1005, 'D03', 28.00, 0.00, 28.00, 'CANCELLED', 'UNPAID', NULL, 5, '2023-10-17 14:15:00');

-- 4. 插入订单项数据
INSERT INTO order_items (order_id, dish_id, dish_name, quantity, unit_price, subtotal, special_instructions) VALUES
(1, 3, '宫保鸡丁', 1, 48.00, 48.00, '少放辣椒'),
(1, 4, '水煮鱼', 1, 78.00, 78.00, '少放辣椒'),
(1, 5, '番茄鸡蛋汤', 1, 18.00, 18.00, NULL),
(2, 3, '宫保鸡丁', 1, 48.00, 48.00, '米饭多些'),
(2, 7, '提拉米苏', 1, 28.00, 28.00, NULL),
(3, 3, '宫保鸡丁', 1, 48.00, 48.00, '打包带走'),
(4, 1, '凉拌黄瓜', 1, 12.00, 12.00, '不要香菜'),
(4, 4, '水煮鱼', 1, 78.00, 78.00, '不要香菜'),
(4, 6, '珍珠奶茶', 1, 15.00, 15.00, NULL),
(5, 7, '提拉米苏', 1, 28.00, 28.00, NULL);

-- 5. 插入支付记录数据
INSERT INTO payments (payment_number, order_id, amount, status, payment_method, transaction_id, paid_at, created_at) VALUES
('PAY20231015001', 1, 138.00, 'SUCCESS', 'WECHAT', 'WX202310151230001', '2023-10-15 12:35:00', '2023-10-15 12:31:00'),
('PAY20231015002', 2, 57.00, 'SUCCESS', 'ALIPAY', 'AL202310151845002', '2023-10-15 18:47:00', '2023-10-15 18:45:00'),
('PAY20231016001', 3, 48.00, 'PENDING', NULL, NULL, NULL, '2023-10-16 11:20:00'),
('PAY20231016002', 4, 96.00, 'PENDING', NULL, NULL, NULL, '2023-10-16 19:10:00'),
('PAY20231017001', 5, 28.00, 'FAILED', 'CASH', NULL, NULL, '2023-10-17 14:15:00');

-- 6. 插入库存变更日志数据
INSERT INTO inventory_logs (dish_id, change_amount, current_stock, type, related_id, notes, created_at) VALUES
(1, -5, 45, 'ORDER', 'ORD20231015001', '订单消耗', '2023-10-15 12:30:00'),
(3, -10, 30, 'ORDER', 'ORD20231015002', '订单消耗', '2023-10-15 18:45:00'),
(3, 20, 50, 'RESTOCK', 'RESTOCK001', '补货入库', '2023-10-15 09:00:00'),
(4, -5, 20, 'ORDER', 'ORD20231016002', '订单消耗', '2023-10-16 19:10:00'),
(7, -2, 18, 'ORDER', 'ORD20231017001', '订单消耗（后取消）', '2023-10-17 14:15:00'),
(7, 2, 20, 'ADJUST', 'ADJUST001', '取消订单返还库存', '2023-10-17 14:20:00');

-- 7. 更新部分订单状态相关时间
UPDATE orders SET
  completed_at = '2023-10-15 13:00:00',
  updated_at = '2023-10-15 13:00:00'
WHERE id = 1;

UPDATE orders SET
  cancelled_at = '2023-10-17 14:20:00',
  cancelled_reason = '顾客临时有事离开',
  updated_at = '2023-10-17 14:20:00'
WHERE id = 5;

-- 8. 更新菜品销量数据（基于已完成的订单）
UPDATE dishes d SET
  total_sold = (
    SELECT SUM(oi.quantity)
    FROM order_items oi
    JOIN orders o ON oi.order_id = o.id
    WHERE oi.dish_id = d.id AND o.status = 'COMPLETED'
  )
WHERE d.id IN (1, 3, 4, 5, 7);

-- 9. 更新支付表的payer信息
UPDATE payments SET
  payer_id = 'USER1001',
  payer_info = '{"name": "张三", "phone": "13800138001"}',
  updated_at = NOW()
WHERE id = 1;

UPDATE payments SET
  payer_id = 'USER1002',
  payer_info = '{"name": "李四", "phone": "13800138002"}',
  updated_at = NOW()
WHERE id = 2;