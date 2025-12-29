#!/bin/bash

# 完整的餐厅系统集成测试
# 包括：顾客下单 -> 后厨处理 -> 订单完成

echo "=== 餐厅系统集成测试 ==="
echo ""

# 1. 顾客登录
echo "1. 顾客登录..."
CUSTOMER_TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"customer1","password":"123456"}' | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

echo "顾客Token获取成功"
echo ""

# 2. 查看菜单
echo "2. 查看菜单..."
DISHES=$(curl -s -X GET http://localhost:8080/api/dishes)
DISH_ID=$(echo $DISHES | grep -o '"id":[0-9]*' | head -1 | cut -d':' -f2)
echo "选择菜品ID: $DISH_ID"
echo ""

# 3. 顾客下单
echo "3. 顾客下单..."
ORDER_REQUEST='{
  "userId": 1,
  "tableNumber": "A01",
  "specialInstructions": "赶时间，请尽快",
  "items": [
    {
      "dishId": '$DISH_ID',
      "quantity": 1
    }
  ]
}'

ORDER_RESPONSE=$(curl -s -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $CUSTOMER_TOKEN" \
  -d "$ORDER_REQUEST")

ORDER_ID=$(echo $ORDER_RESPONSE | grep -o '"id":[0-9]*' | head -1 | cut -d':' -f2)
echo "订单创建成功，订单ID: $ORDER_ID"
echo ""

# 4. 顾客支付
echo "4. 顾客支付..."
PAYMENT_RESPONSE=$(curl -s -X POST http://localhost:8080/api/orders/$ORDER_ID/payment \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $CUSTOMER_TOKEN" \
  -d '{"paymentMethod": "CASH"}')

echo "支付完成"
echo ""

# 5. 后厨登录
echo "5. 后厨登录..."
KITCHEN_TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"kitchen","password":"123456"}' | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

echo "后厨Token获取成功"
echo ""

# 6. 后厨查看订单队列
echo "6. 后厨查看订单队列..."
KITCHEN_ORDERS=$(curl -s -X GET http://localhost:8080/api/kitchen/orders \
  -H "Authorization: Bearer $KITCHEN_TOKEN")

echo "后厨订单队列: $KITCHEN_ORDERS"
echo ""

# 7. 后厨开始制作
echo "7. 后厨开始制作订单..."
curl -s -X POST http://localhost:8080/api/kitchen/orders/$ORDER_ID/start \
  -H "Authorization: Bearer $KITCHEN_TOKEN" > /dev/null

echo "开始制作订单"
echo ""

# 8. 后厨标记订单为已出餐
echo "8. 后厨标记订单为已出餐..."
curl -s -X POST http://localhost:8080/api/kitchen/orders/$ORDER_ID/ready \
  -H "Authorization: Bearer $KITCHEN_TOKEN" > /dev/null

echo "订单已出餐"
echo ""

# 9. 更新订单状态为已完成
echo "9. 更新订单状态为已完成..."
curl -s -X PUT http://localhost:8080/api/orders/$ORDER_ID/status \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $CUSTOMER_TOKEN" \
  -d '{"status": "COMPLETED"}' > /dev/null

echo "订单已完成"
echo ""

# 10. 查看订单最终状态
echo "10. 查看订单最终状态..."
FINAL_ORDER=$(curl -s -X GET http://localhost:8080/api/orders/$ORDER_ID \
  -H "Authorization: Bearer $CUSTOMER_TOKEN")

echo "订单最终状态: $FINAL_ORDER"
echo ""

echo "=== 集成测试完成 ==="
echo "整个流程：顾客下单 -> 支付 -> 后厨处理 -> 订单完成"