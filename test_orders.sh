#!/bin/bash

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}=== 测试订单功能 ===${NC}\n"

# 1. 先登录获取token
echo -e "${GREEN}1. 用户登录${NC}"
LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"customer1","password":"123456"}')

if echo "$LOGIN_RESPONSE" | grep -q "success.*true"; then
    echo -e "登录成功"
else
    echo -e "${RED}登录失败${NC}"
    echo "$LOGIN_RESPONSE"
    exit 1
fi

# 提取token和用户ID
TOKEN=$(echo $LOGIN_RESPONSE | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
USER_ID=$(echo $LOGIN_RESPONSE | grep -o '"id":[0-9]*' | cut -d':' -f2)

echo "Token: ${TOKEN:0:30}..."
echo "用户ID: $USER_ID"
echo ""

# 2. 先获取菜品列表，选择菜品ID
echo -e "${GREEN}2. 获取菜品列表${NC}"
DISHES_RESPONSE=$(curl -s -X GET http://localhost:8080/api/dishes)
echo "菜品列表获取成功"
DISH_ID=$(echo $DISHES_RESPONSE | grep -o '"id":[0-9]*' | head -1 | cut -d':' -f2)
echo "选择菜品ID: $DISH_ID"
echo ""

# 3. 创建订单
echo -e "${GREEN}3. 创建订单${NC}"
CREATE_ORDER_REQUEST='{
  "userId": '$USER_ID',
  "tableNumber": "A12",
  "specialInstructions": "少辣，不要香菜",
  "items": [
    {
      "dishId": '$DISH_ID',
      "quantity": 2,
      "specialInstructions": "少盐"
    }
  ]
}'

ORDER_RESPONSE=$(curl -s -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "$CREATE_ORDER_REQUEST")

echo "创建订单响应: $ORDER_RESPONSE"

# 提取订单ID
ORDER_ID=$(echo $ORDER_RESPONSE | grep -o '"id":[0-9]*' | head -1 | cut -d':' -f2)
ORDER_NUMBER=$(echo $ORDER_RESPONSE | grep -o '"orderNumber":"[^"]*"' | cut -d'"' -f4)

if [ -n "$ORDER_ID" ]; then
    echo -e "订单创建成功！订单ID: $ORDER_ID, 订单号: $ORDER_NUMBER"
else
    echo -e "${RED}订单创建失败${NC}"
    exit 1
fi
echo ""

# 4. 查询订单详情
echo -e "${GREEN}4. 查询订单详情${NC}"
ORDER_DETAIL_RESPONSE=$(curl -s -X GET http://localhost:8080/api/orders/$ORDER_ID \
  -H "Authorization: Bearer $TOKEN")

echo "订单详情: $ORDER_DETAIL_RESPONSE"
echo ""

# 5. 处理支付
echo -e "${GREEN}5. 处理支付${NC}"
PAYMENT_REQUEST='{
  "paymentMethod": "WECHAT",
  "payerInfo": "微信支付"
}'

PAYMENT_RESPONSE=$(curl -s -X POST http://localhost:8080/api/orders/$ORDER_ID/payment \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "$PAYMENT_REQUEST")

echo "支付响应: $PAYMENT_RESPONSE"
echo ""

# 6. 更新订单状态（模拟后厨操作）
echo -e "${GREEN}6. 更新订单状态（后厨确认）${NC}"
STATUS_REQUEST='{"status": "CONFIRMED"}'
curl -s -X PUT http://localhost:8080/api/orders/$ORDER_ID/status \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "$STATUS_REQUEST"

echo "订单状态已更新为 CONFIRMED"
echo ""

# 7. 获取用户订单列表
echo -e "${GREEN}7. 获取用户订单列表${NC}"
USER_ORDERS_RESPONSE=$(curl -s -X GET http://localhost:8080/api/orders/user/$USER_ID \
  -H "Authorization: Bearer $TOKEN")

echo "用户订单列表: $USER_ORDERS_RESPONSE"
echo ""

# 8. 获取订单统计
echo -e "${GREEN}8. 获取订单统计${NC}"
STATS_RESPONSE=$(curl -s -X GET http://localhost:8080/api/orders/statistics \
  -H "Authorization: Bearer $TOKEN")

echo "订单统计: $STATS_RESPONSE"
echo ""

echo -e "${YELLOW}=== 订单功能测试完成 ===${NC}"