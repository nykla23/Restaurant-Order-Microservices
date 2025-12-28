package com.zjgsu.obl.user_service.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class OrderServiceClient {

    @Value("${order-service.url}")
    private String orderServiceUrl;

    @Value("${service.auth.internal-key}")
    private String internalApiKey;

    private final RestTemplate restTemplate;

    public OrderServiceClient() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * 通知订单服务库存变化
     */
    public void notifyInventoryChange(Long dishId, Integer newStock) {
        try {
            String url = orderServiceUrl + "/internal/dishes/" + dishId + "/stock";

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Internal-Key", internalApiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            String requestBody = String.format("{\"stock\": %d}", newStock);
            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            restTemplate.exchange(url, HttpMethod.PUT, entity, Void.class);

            log.debug("已通知订单服务菜品 {} 库存更新为 {}", dishId, newStock);
        } catch (Exception e) {
            log.error("通知订单服务失败: {}", e.getMessage());
        }
    }
}