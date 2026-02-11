package com.blog.config;

import com.blog.controller.AuthController;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(1)
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String method = req.getMethod();
        String path = req.getRequestURI();

        boolean requiresAuth = path.startsWith("/posts")
                && ("POST".equals(method) || "PUT".equals(method) || "DELETE".equals(method));

        if (requiresAuth) {
            String authHeader = req.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")
                    || !AuthController.isValidToken(authHeader.substring(7))) {
                res.setStatus(401);
                res.setContentType("application/json");
                res.getWriter().write("{\"error\":\"Unauthorized\"}");
                return;
            }
        }

        chain.doFilter(request, response);
    }
}
