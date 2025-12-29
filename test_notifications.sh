#!/bin/bash

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}=== 测试通知模块 ===${NC}\n"

# 1. 用户登录
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

# 2. 获取通知列表
echo -e "${GREEN}2. 获取通知列表${NC}"
NOTIFICATIONS_RESPONSE=$(curl -s -X GET http://localhost:8080/api/notifications \
  -H "Authorization: Bearer $TOKEN")

echo "通知列表: $NOTIFICATIONS_RESPONSE"
echo ""

# 3. 获取通知统计
echo -e "${GREEN}3. 获取通知统计${NC}"
COUNT_RESPONSE=$(curl -s -X GET http://localhost:8080/api/notifications/count \
  -H "Authorization: Bearer $TOKEN")

echo "通知统计: $COUNT_RESPONSE"
echo ""

# 4. 创建测试订单（触发通知）
echo -e "${GREEN}4. 创建测试订单${NC}"
# 先获取一个菜品ID
DISHES_RESPONSE=$(curl -s -X GET http://localhost:8080/api/dishes)
DISH_ID=$(echo $DISHES_RESPONSE | grep -o '"id":[0-9]*' | head -1 | cut -d':' -f2)

ORDER_REQUEST='{
  "userId": '$USER_ID',
  "tableNumber": "T01",
  "items": [
    {
      "dishId": '$DISH_ID',
      "quantity": 1
    }
  ]
}'

ORDER_RESPONSE=$(curl -s -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "$ORDER_REQUEST")

ORDER_ID=$(echo $ORDER_RESPONSE | grep -o '"id":[0-9]*' | head -1 | cut -d':' -f2)

if [ -n "$ORDER_ID" ]; then
    echo "订单创建成功，订单ID: $ORDER_ID"

    # 等待一会儿让通知生成
    sleep 2

    # 再次获取通知列表
    echo -e "\n${GREEN}5. 再次获取通知列表（应该有新通知）${NC}"
    NOTIFICATIONS_RESPONSE2=$(curl -s -X GET http://localhost:8080/api/notifications \
      -H "Authorization: Bearer $TOKEN")

    echo "更新后的通知列表: $NOTIFICATIONS_RESPONSE2"
    echo ""

    # 提取一个通知ID
    NOTIFICATION_ID=$(echo $NOTIFICATIONS_RESPONSE2 | grep -o '"id":[0-9]*' | head -1 | cut -d':' -f2)

    if [ -n "$NOTIFICATION_ID" ]; then
        echo "找到通知ID: $NOTIFICATION_ID"

        # 6. 标记通知为已读
        echo -e "\n${GREEN}6. 标记通知为已读${NC}"
        MARK_RESPONSE=$(curl -s -X POST http://localhost:8080/api/notifications/$NOTIFICATION_ID/read \
          -H "Authorization: Bearer $TOKEN")

        echo "标记响应: $MARK_RESPONSE"
        echo ""

        # 7. 标记所有通知为已读
        echo -e "${GREEN}7. 标记所有通知为已读${NC}"
        MARK_ALL_RESPONSE=$(curl -s -X POST http://localhost:8080/api/notifications/read-all \
          -H "Authorization: Bearer $TOKEN")

        echo "标记所有响应: $MARK_ALL_RESPONSE"
        echo ""
    fi
else
    echo -e "${YELLOW}订单创建失败，跳过后续测试${NC}"
fi

# 8. 测试库存预警通知（需要管理员权限）
echo -e "${GREEN}8. 测试库存预警通知${NC}"
# 这里需要管理员权限，暂时跳过
echo "需要管理员权限，跳过测试"
echo ""

# 9. 测试WebSocket连接（可选）
echo -e "${GREEN}9. WebSocket测试（可选）${NC}"
echo "可以使用以下命令测试WebSocket连接："
echo "wscat -c 'ws://localhost:8080/ws'"
echo ""

echo -e "${YELLOW}=== 通知模块测试完成 ===${NC}"