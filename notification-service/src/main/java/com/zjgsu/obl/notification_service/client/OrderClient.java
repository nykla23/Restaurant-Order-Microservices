package com.zjgsu.obl.notification_service.client;

import com.zjgsu.obl.notification_service.dto.OrderDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class OrderClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String internalApiKey;

    public OrderClient(RestTemplate restTemplate,
                       @Value("${order-service.internal-url}") String baseUrl,
                       @Value("${service.auth.internal-key}") String internalApiKey) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
        this.internalApiKey = internalApiKey;
    }

    public OrderDTO getOrderById(Long id) {
        try {
            String url = baseUrl + "/internal/orders/" + id;

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Internal-Key", internalApiKey);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<OrderDTO> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, OrderDTO.class);

            return response.getBody();

        } catch (Exception e) {
            log.error("获取订单信息失败: orderId={}, error={}", id, e.getMessage());
            return null; // 返回null，让调用方处理
        }
    }
}