package com.zjgsu.obl.notification_service.client;

import com.zjgsu.obl.notification_service.dto.DishDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class DishClient {
    @Autowired
    private  RestTemplate restTemplate;

    @Value("${order-service.url}")
    private  String baseUrl;

    @Autowired
    public DishClient(RestTemplate restTemplate,@Value("${order-service.url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public DishDTO getDishById(Long dishId) {
        return restTemplate.getForObject(baseUrl + "api/dishes/" + dishId, DishDTO.class);
    }
}
