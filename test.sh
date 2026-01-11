#!/bin/bash
# 核心用例1：用户登录（验证返回Token）
echo "===== 测试用户登录 ====="
LOGIN_RESP=$(curl -X POST -H "Content-Type: application/json" -d '{"username":"test001","password":"123456"}' http://localhost:8081/api/v1/users/login)
echo "登录响应：$LOGIN_RESP"
# 提取Token（如果返回200）
if echo $LOGIN_RESP | grep -q "\"code\":200"; then
    TOKEN=$(echo $LOGIN_RESP | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
    echo "获取到Token：$TOKEN"

    # 核心用例2：创建订单（带Token）
    echo -e "\n===== 测试创建订单 ====="
    ORDER_RESP=$(curl -X POST -H "Content-Type: application/json" -H "Authorization: Bearer $TOKEN" -d '{"dishId":1001,"quantity":2,"userId":1001}' http://localhost:8080/api/v1/orders/create)
    echo "订单创建响应：$ORDER_RESP"

    # 核心用例3：熔断降级（关闭order-service后测试）
    echo -e "\n===== 测试熔断降级 ====="
    docker-compose stop order-service # 停止订单服务
    NOTIFY_RESP=$(curl http://localhost:8082/api/v1/notification/order/2001)
    echo "通知服务降级响应：$NOTIFY_RESP"
    docker-compose start order-service # 恢复订单服务
fi