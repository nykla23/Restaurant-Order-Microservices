package com.zjgsu.obl.user_service.config;
import com.zjgsu.obl.user_service.common.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    // 定义公开接口列表（不需要登录的接口）
    private static final List<String> PUBLIC_PATHS = Arrays.asList(
            "/api/auth/login",
            "/api/auth/register",
            "/api/categories",         // GET 所有分类
            "/api/dishes",             // GET 所有菜品
            "/api/dishes/popular",     // GET 热门菜品
            "/api/dishes/search",      // GET 搜索菜品
            "/api/dishes/category/",   // GET 分类菜品
            "/api/dishes/"            // GET 菜品详情（特殊处理）
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();
        String method = request.getMethod();

        log.debug("请求路径: {} {}", method, path);

        // 检查是否是公开接口
        if (isPublicPath(method, path)) {
            return true;
        }

        // 获取token
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"success\":false,\"message\":\"未提供Token\"}");
            return false;
        }

        token = token.substring(7); // 去掉"Bearer "

        // 验证token
        if (!jwtUtil.validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"success\":false,\"message\":\"Token无效或已过期\"}");
            return false;
        }

        // 将用户信息存入request
        String username = jwtUtil.getUsernameFromToken(token);
        String role = jwtUtil.getRoleFromToken(token);
        Long userId = jwtUtil.getUserIdFromToken(token);

        request.setAttribute("username", username);
        request.setAttribute("role", role);
        request.setAttribute("userId", userId);

        log.info("用户认证成功 - 用户: {}, 角色: {}, ID: {}, 路径: {}", username, role, userId, path);
        return true;
    }

    /**
     * 检查是否是公开接口
     */
    private boolean isPublicPath(String method, String path) {
        // 如果是OPTIONS请求（预检请求），放行
        if ("OPTIONS".equals(method)) {
            return true;
        }

        // 检查路径是否在公开接口列表中
        for (String publicPath : PUBLIC_PATHS) {
            if (path.equals(publicPath) && "GET".equals(method)) {
                // GET请求特定路径放行
                return true;
            }
        }

        // 特殊处理：根据ID查询菜品详情（GET /api/dishes/{id}）
        if ("GET".equals(method) && path.startsWith("/api/dishes/") && !path.endsWith("/stock")) {
            // 检查是否是数字ID（简单判断）
            String idPart = path.substring("/api/dishes/".length());
            if (idPart.matches("\\d+")) {
                return true;
            }
        }

        // 特殊处理：根据分类查询菜品（GET /api/dishes/category/{categoryId}）
        if ("GET".equals(method) && path.startsWith("/api/dishes/category/")) {
            return true;
        }

        return false;
    }
}