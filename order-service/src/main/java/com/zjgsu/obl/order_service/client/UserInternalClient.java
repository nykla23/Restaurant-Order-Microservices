package com.zjgsu.obl.order_service.client;

import com.zjgsu.obl.order_service.common.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

@Component
@Slf4j
public class UserInternalClient {

    @Value("${user-service.internal-url}")  // 直接连接user-service的地址
    private String userServiceUrl;

    @Value("${service.auth.internal-key}")
    private String internalApiKey;

    private final RestTemplate restTemplate;

    public UserInternalClient() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * 获取用户总数
     */
    public long getTotalUsers() {
        try {
            String url = userServiceUrl + "/internal/users/count";

            HttpHeaders headers = createInternalHeaders();
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<ApiResponse> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, ApiResponse.class);

            if (response.getStatusCode().is2xxSuccessful() &&
                    response.getBody() != null &&
                    response.getBody().isSuccess()) {
                Number count = (Number) response.getBody().getData();
                return count != null ? count.longValue() : 0L;
            }

            log.warn("获取用户总数失败: {}", response.getBody());
            return 0L;

        } catch (Exception e) {
            log.error("调用用户服务失败: {}", e.getMessage());
            return 0L;  // 返回默认值，不影响主要功能
        }
    }

    /**
     * 获取时间范围内新增用户数
     */
    public long getNewUsers(Date startDate, Date endDate) {
        try {
            // 格式化日期参数
            String start = String.valueOf(startDate.getTime());
            String end = String.valueOf(endDate.getTime());

            String url = userServiceUrl + "/internal/users/count/new?startDate=" + start + "&endDate=" + end;

            HttpHeaders headers = createInternalHeaders();
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<ApiResponse> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, ApiResponse.class);

            if (response.getStatusCode().is2xxSuccessful() &&
                    response.getBody() != null &&
                    response.getBody().isSuccess()) {
                Number count = (Number) response.getBody().getData();
                return count != null ? count.longValue() : 0L;
            }

            log.warn("获取新增用户数失败: {}", response.getBody());
            return 0L;

        } catch (Exception e) {
            log.error("调用用户服务失败: {}", e.getMessage());
            return 0L;  // 返回默认值
        }
    }

    private HttpHeaders createInternalHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Internal-Key", internalApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}