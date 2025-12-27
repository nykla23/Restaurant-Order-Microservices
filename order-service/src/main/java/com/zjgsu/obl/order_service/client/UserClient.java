package com.zjgsu.obl.order_service.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.format.DateTimeFormatter;
import java.util.Date;

@Component
public class UserClient {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${user-service.url}")
    private String userServiceUrl;

    public void getUserById(Long userId) {
        String url = userServiceUrl + "api/users/" + userId;
        try {
            restTemplate.getForEntity(url, Void.class);
        }catch (Exception e) {
            throw new RuntimeException("用户不存在");
        }
    }

    public long countUsers() {
        String url = userServiceUrl + "/api/users/count";
        try {
            return restTemplate.getForObject(url, Long.class);
        } catch (Exception e) {
            throw new RuntimeException("获取用户总数失败");
        }
    }

    public long countNewUsers(Date startDate, Date endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String start = startDate.toInstant().atZone(java.time.ZoneId.systemDefault()).format(formatter);
        String end = endDate.toInstant().atZone(java.time.ZoneId.systemDefault()).format(formatter);

        String url = userServiceUrl + "/count/new?startDate=" + start + "&endDate=" + end;
        try {
            return restTemplate.getForObject(url, Long.class);
        } catch (Exception e) {
            throw new RuntimeException("获取新用户总数失败");
        }
    }


}
