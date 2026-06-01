package com.example.market.interceptor;

import com.example.market.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取请求头中的token
        String token = request.getHeader("Authorization");

        System.out.println("收到请求: " + request.getRequestURI());
        System.out.println("Authorization header: " + token);

        // 检查token是否存在
        if (token == null || token.isEmpty()) {
            response.setStatus(401);
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("未登录");
            return false;
        }

        // 去掉Bearer前缀（如果存在）
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        System.out.println("解析后的token: " + token);

        // 验证token
        if (!jwtUtil.verify(token)) {
            response.setStatus(401);
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("token无效");
            return false;
        }

        // 获取用户ID并存入request
        Long userId = jwtUtil.getUserId(token);
        System.out.println("获取到的userId: " + userId);
        request.setAttribute("userId", userId);

        return true;
    }
}