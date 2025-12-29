#!/bin/bash

echo "=== 餐厅系统完整流程测试 ==="
echo ""

# 清理并重启服务（如果需要）
# echo "重启服务..."
# mvn spring-boot:run &

echo "等待服务启动..."
sleep 10

# 1. 顾客注册
echo "1. 测试顾客注册..."
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"test_customer","password":"123456","phone":"13800138000","nickname":"测试顾客"}'

echo ""
echo ""

# 2. 顾客登录
echo "2. 顾客登录..."
CUSTOMER_RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"test_customer","password":"123456"}')

CUSTOMER_TOKEN=$(echo $CUSTOMER_RESPONSE | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
echo "顾客登录成功"
echo ""

# 3. 查看菜单
echo "3. 查看菜单..."
curl -s -X GET http://localhost:8080/api/dishes | head -1
echo ""

# 4. 顾客下单
echo "4. 顾客下单..."
# 获取菜品ID
DISH_ID=$(curl -s -X GET http://localhost:8080/api/dishes | grep -o '"id":[0-9]*' | head -1 | cut -d':' -f2)

ORDER_REQUEST='{
  "userId": 1,
  "tableNumber": "TEST01",
  "specialInstructions": "测试订单",
  "items": [
    {
      "dishId": '$DISH_ID',
      "quantity": 2
    }
  ]
}'

ORDER_RESPONSE=$(curl -s -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $CUSTOMER_TOKEN" \
  -d "$ORDER_REQUEST")

echo "订单创建响应: $ORDER_RESPONSE"
ORDER_ID=$(echo $ORDER_RESPONSE | grep -o '"id":[0-9]*' | head -1 | cut -d':' -f2)
echo "订单ID: $ORDER_ID"
echo ""

# 5. 顾客查看通知
echo "5. 顾客查看通知（应该有新订单通知）..."
curl -s -X GET http://localhost:8080/api/notifications \
  -H "Authorization: Bearer $CUSTOMER_TOKEN" | head -1
echo ""

# 6. 顾客支付
echo "6. 顾客支付..."
curl -s -X POST http://localhost:8080/api/orders/$ORDER_ID/payment \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $CUSTOMER_TOKEN" \
  -d '{"paymentMethod": "WECHAT"}'
echo "支付完成"
echo ""

# 7. 后厨处理订单
echo "7. 后厨处理订单..."
# 后厨登录
KITCHEN_RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"kitchen","password":"123456"}')

KITCHEN_TOKEN=$(echo $KITCHEN_RESPONSE | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

# 后厨开始制作
curl -s -X POST http://localhost:8080/api/kitchen/orders/$ORDER_ID/start \
  -H "Authorization: Bearer $KITCHEN_TOKEN"

# 后厨标记为已出餐
curl -s -X POST http://localhost:8080/api/kitchen/orders/$ORDER_ID/ready \
  -H "Authorization: Bearer $KITCHEN_TOKEN"

echo "后厨处理完成"
echo ""

# 8. 顾客确认完成
echo "8. 顾客确认订单完成..."
curl -s -X PUT http://localhost:8080/api/orders/$ORDER_ID/status \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $CUSTOMER_TOKEN" \
  -d '{"status": "COMPLETED"}'

echo "订单完成"
echo ""

# 9. 查看完整流程后的状态
echo "9. 最终状态检查..."
echo "订单状态:"
curl -s -X GET http://localhost:8080/api/orders/$ORDER_ID \
  -H "Authorization: Bearer $CUSTOMER_TOKEN" | grep -o '"status":"[^"]*"' | cut -d'"' -f4

echo "顾客通知数量:"
curl -s -X GET http://localhost:8080/api/notifications/count \
  -H "Authorization: Bearer $CUSTOMER_TOKEN" | grep -o '"unreadCount":[0-9]*' | cut -d':' -f2

echo ""
echo "=== 完整流程测试完成 ==="