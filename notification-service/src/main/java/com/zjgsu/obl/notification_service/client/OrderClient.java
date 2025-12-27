package com.zjgsu.obl.notification_service.client;

import com.zjgsu.obl.notification_service.dto.OrderDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class OrderClient {
    private final RestTemplate restTemplate;
    private final String baseUrl;

    @Autowired
    public OrderClient(RestTemplate restTemplate, String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }
    public OrderDTO getOrderById(Long id) {
        return restTemplate.getForObject(baseUrl + "api/orders/" + id, OrderDTO.class);
    }
}
