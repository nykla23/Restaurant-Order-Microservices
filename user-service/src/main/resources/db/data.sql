-- 1. 订单相关通知
INSERT INTO notifications (user_id, title, content, type, related_type, related_id, status, priority, created_at, read_at, expires_at) VALUES
(1001, '订单确认', '您的订单 ORD20231015001 已确认，正在准备中。', 'ORDER', 'ORDER', 1, 'READ', 'NORMAL', '2023-10-15 12:31:00', '2023-10-15 12:32:00', '2023-11-15 12:31:00'),
(1002, '订单制作中', '您的订单 ORD20231015002 正在制作中，请耐心等待。', 'ORDER', 'ORDER', 2, 'READ', 'NORMAL', '2023-10-15 18:46:00', '2023-10-15 18:50:00', '2023-11-15 18:46:00'),
(1003, '待支付提醒', '您的订单 ORD20231016001 等待支付，请尽快完成支付。', 'ORDER', 'ORDER', 3, 'UNREAD', 'HIGH', '2023-10-16 11:21:00', NULL, '2023-10-17 11:21:00'),
(1004, '订单已确认', '您的订单 ORD20231016002 已确认，开始备餐。', 'ORDER', 'ORDER', 4, 'READ', 'NORMAL', '2023-10-16 19:11:00', '2023-10-16 19:15:00', '2023-11-16 19:11:00'),
(1005, '订单取消', '您的订单 ORD20231017001 已取消，退款将原路返回。', 'ORDER', 'ORDER', 5, 'READ', 'NORMAL', '2023-10-17 14:20:00', '2023-10-17 14:25:00', '2023-11-17 14:20:00'),
(NULL, '订单支付成功', '用户 1001 支付订单 ORD20231015001 成功，金额 138.00 元。', 'ORDER', 'ORDER', 1, 'READ', 'LOW', '2023-10-15 12:36:00', '2023-10-15 12:40:00', '2023-11-15 12:36:00');

-- 2. 系统通知
INSERT INTO notifications (user_id, title, content, type, related_type, related_id, status, priority, created_at, read_at, expires_at) VALUES
(NULL, '系统维护通知', '系统将于今晚 02:00-04:00 进行维护升级，期间服务可能短暂中断。', 'SYSTEM', NULL, NULL, 'UNREAD', 'NORMAL', '2023-10-17 10:00:00', NULL, '2023-10-18 10:00:00'),
(NULL, '新功能上线', '订单实时追踪功能已上线，您可以在订单详情中查看制作进度。', 'SYSTEM', NULL, NULL, 'UNREAD', 'NORMAL', '2023-10-16 09:00:00', NULL, '2023-11-16 09:00:00'),
(1001, '账号安全提醒', '您的账号在异地登录，如非本人操作请立即修改密码。', 'SYSTEM', NULL, NULL, 'READ', 'HIGH', '2023-10-15 14:30:00', '2023-10-15 14:35:00', '2023-10-16 14:30:00');

-- 3. 库存通知
INSERT INTO notifications (user_id, title, content, type, related_type, related_id, status, priority, created_at, read_at, expires_at) VALUES
(NULL, '库存预警', '菜品"提拉米苏"库存不足，当前库存：18，建议及时补货。', 'INVENTORY', 'DISH', 7, 'UNREAD', 'URGENT', '2023-10-17 10:00:00', NULL, '2023-10-18 10:00:00'),
(NULL, '库存补货提醒', '菜品"宫保鸡丁"已补货，补货数量：20，当前库存：50。', 'INVENTORY', 'DISH', 3, 'READ', 'NORMAL', '2023-10-15 09:05:00', '2023-10-15 09:10:00', '2023-10-16 09:05:00'),
(NULL, '库存耗尽', '菜品"夫妻肺片"库存已耗尽，请立即补货。', 'INVENTORY', 'DISH', 2, 'READ', 'URGENT', '2023-10-14 16:20:00', '2023-10-14 16:25:00', '2023-10-15 16:20:00');

-- 4. 促销通知
INSERT INTO notifications (user_id, title, content, type, related_type, related_id, status, priority, created_at, read_at, expires_at) VALUES
(NULL, '周末特惠', '本周末全场菜品88折，会员再享95折优惠！', 'PROMOTION', NULL, NULL, 'UNREAD', 'NORMAL', '2023-10-13 09:00:00', NULL, '2023-10-16 23:59:00'),
(NULL, '新品上市', '新菜品"麻辣香锅"现已上市，欢迎品尝！', 'PROMOTION', 'DISH', NULL, 'UNREAD', 'NORMAL', '2023-10-12 10:00:00', NULL, '2023-10-19 23:59:00'),
(1001, '生日专享优惠', '亲爱的用户，祝您生日快乐！赠送您一张8折优惠券，有效期30天。', 'PROMOTION', NULL, NULL, 'READ', 'NORMAL', '2023-10-15 00:00:00', '2023-10-15 09:00:00', '2023-11-15 23:59:00'),
(1002, '专属优惠券', '您有一张满100减20的优惠券即将到期，请尽快使用。', 'PROMOTION', NULL, NULL, 'UNREAD', 'HIGH', '2023-10-17 09:00:00', NULL, '2023-10-20 23:59:00');

-- 5. 混合通知（已读/未读，不同优先级）
INSERT INTO notifications (user_id, title, content, type, related_type, related_id, status, priority, created_at, read_at, expires_at) VALUES
(NULL, '餐厅营业时间调整', '即日起，餐厅营业时间调整为 10:00-22:00。', 'SYSTEM', NULL, NULL, 'READ', 'NORMAL', '2023-10-10 08:00:00', '2023-10-10 12:00:00', '2023-10-20 23:59:00'),
(1003, '订单超时提醒', '您的订单 ORD20231016001 已超过30分钟未支付，即将自动取消。', 'ORDER', 'ORDER', 3, 'UNREAD', 'URGENT', '2023-10-16 11:50:00', NULL, '2023-10-16 12:20:00'),
(NULL, '厨师推荐', '本周厨师推荐菜品："水煮鱼"，麻辣鲜香，不容错过！', 'PROMOTION', 'DISH', 4, 'UNREAD', 'LOW', '2023-10-16 11:00:00', NULL, '2023-10-23 23:59:00');

-- 更新一些已读通知的阅读时间
UPDATE notifications SET read_at = DATE_ADD(created_at, INTERVAL 5 MINUTE)
WHERE status = 'READ' AND read_at IS NULL AND created_at < NOW();

-- 为部分通知设置过期时间（如果还未设置）
UPDATE notifications SET expires_at = DATE_ADD(created_at, INTERVAL 7 DAY)
WHERE expires_at IS NULL AND type IN ('PROMOTION', 'SYSTEM');

UPDATE notifications SET expires_at = DATE_ADD(created_at, INTERVAL 30 DAY)
WHERE expires_at IS NULL AND type = 'ORDER';