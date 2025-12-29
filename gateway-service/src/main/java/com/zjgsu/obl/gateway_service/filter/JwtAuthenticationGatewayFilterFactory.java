package com.zjgsu.obl.gateway_service.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class JwtAuthenticationGatewayFilterFactory extends AbstractGatewayFilterFactory<JwtAuthenticationGatewayFilterFactory.Config> {

    @Value("${jwt.secret}")
    private String jwtSecret;

    public JwtAuthenticationGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return new GatewayFilter() {
            @Override
            public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
                ServerHttpRequest request = exchange.getRequest();

                // 如果配置为不需要认证，直接放行
                if (!config.isRequired()) {
                    return chain.filter(exchange);
                }

                // 获取Authorization头
                String authHeader = request.getHeaders().getFirst("Authorization");

                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                    log.warn("缺少或无效的Authorization头");
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                }

                // 提取Token
                String token = authHeader.substring(7);

                try {
                    // 验证和解析Token
                    Claims claims = parseToken(token);

                    // 提取用户信息
                    String userId = claims.get("userId", String.class);
                    String username = claims.getSubject();
                    String role = claims.get("role", String.class);

                    log.debug("认证用户: {} (ID: {}, Role: {})", username, userId, role);

                    // 将用户信息添加到请求头，传递给下游服务
                    ServerHttpRequest mutatedRequest = request.mutate()
                            .header("X-User-Id", userId)
                            .header("X-User-Name", username)
                            .header("X-User-Role", role)
                            .build();

                    return chain.filter(exchange.mutate().request(mutatedRequest).build());

                } catch (Exception e) {
                    log.error("Token验证失败: {}", e.getMessage());
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                }
            }
        };
    }

    private Claims parseToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public static class Config {
        private boolean required = true;

        public boolean isRequired() {
            return required;
        }

        public void setRequired(boolean required) {
            this.required = required;
        }
    }
}