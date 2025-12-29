#!/bin/bash

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}=== 测试后厨管理功能 ===${NC}\n"

# 1. 先登录获取token（使用后厨账号）
echo -e "${GREEN}1. 后厨登录${NC}"
LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"kitchen","password":"123456"}')

if echo "$LOGIN_RESPONSE" | grep -q "success.*true"; then
    echo -e "登录成功"
else
    echo -e "${RED}登录失败，尝试使用顾客账号${NC}"
    LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/login \
      -H "Content-Type: application/json" \
      -d '{"username":"customer1","password":"123456"}')
fi

# 提取token
TOKEN=$(echo $LOGIN_RESPONSE | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

if [ -z "$TOKEN" ]; then
    echo -e "${RED}获取Token失败${NC}"
    exit 1
fi

echo "Token: ${TOKEN:0:30}..."
echo ""

# 2. 获取后厨仪表板数据
echo -e "${GREEN}2. 获取后厨仪表板数据${NC}"
DASHBOARD_RESPONSE=$(curl -s -X GET http://localhost:8080/api/kitchen/dashboard \
  -H "Authorization: Bearer $TOKEN")

echo "仪表板数据: $DASHBOARD_RESPONSE"
echo ""

# 3. 获取后厨订单队列
echo -e "${GREEN}3. 获取后厨订单队列${NC}"
ORDERS_RESPONSE=$(curl -s -X GET http://localhost:8080/api/kitchen/orders \
  -H "Authorization: Bearer $TOKEN")

echo "订单队列: $ORDERS_RESPONSE"

# 提取一个订单ID（如果有订单）
ORDER_ID=$(echo $ORDERS_RESPONSE | grep -o '"orderId":[0-9]*' | head -1 | cut -d':' -f2)

if [ -n "$ORDER_ID" ]; then
    echo "找到订单ID: $ORDER_ID"
    echo ""

    # 4. 开始制作订单
    echo -e "${GREEN}4. 开始制作订单${NC}"
    START_RESPONSE=$(curl -s -X POST http://localhost:8080/api/kitchen/orders/$ORDER_ID/start \
      -H "Authorization: Bearer $TOKEN")

    echo "开始制作响应: $START_RESPONSE"
    echo ""

    # 5. 标记订单为已出餐
    echo -e "${GREEN}5. 标记订单为已出餐${NC}"
    READY_RESPONSE=$(curl -s -X POST http://localhost:8080/api/kitchen/orders/$ORDER_ID/ready \
      -H "Authorization: Bearer $TOKEN")

    echo "标记已出餐响应: $READY_RESPONSE"
    echo ""
else
    echo -e "${YELLOW}没有找到待处理的订单${NC}"
    echo ""
fi

# 6. 获取不同状态的订单列表
echo -e "${GREEN}6. 获取等待制作的订单${NC}"
WAITING_RESPONSE=$(curl -s -X GET http://localhost:8080/api/kitchen/orders/waiting \
  -H "Authorization: Bearer $TOKEN")

echo "等待制作的订单: $WAITING_RESPONSE"
echo ""

echo -e "${GREEN}7. 获取正在制作的订单${NC}"
PREPARING_RESPONSE=$(curl -s -X GET http://localhost:8080/api/kitchen/orders/preparing \
  -H "Authorization: Bearer $TOKEN")

echo "正在制作的订单: $PREPARING_RESPONSE"
echo ""

echo -e "${GREEN}8. 获取已出餐的订单${NC}"
READY_RESPONSE=$(curl -s -X GET http://localhost:8080/api/kitchen/orders/ready \
  -H "Authorization: Bearer $TOKEN")

echo "已出餐的订单: $READY_RESPONSE"
echo ""

echo -e "${YELLOW}=== 后厨管理功能测试完成 ===${NC}"