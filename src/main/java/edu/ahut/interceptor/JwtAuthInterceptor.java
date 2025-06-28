package edu.ahut.interceptor;

import edu.ahut.context.UserContext;
import edu.ahut.entity.User;
import edu.ahut.service.UserService;
import edu.ahut.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtAuthInterceptor implements HandlerInterceptor {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UserService userService;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取请求头中的Authorization
        String authHeader = request.getHeader("Authorization");
        
        // 验证请求头中是否有token
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            
            // 验证token是否有效
            if (jwtUtil.validateToken(token)) {
                // 从token中获取用户ID
                Long userId = jwtUtil.getUserIdFromToken(token);
                // 根据用户ID获取用户信息
                User user = userService.getUserById(userId);
                
                if (user != null) {
                    // 将token存入ThreadLocal
                    UserContext.setCurrentToken(token);
                    
                    // 将用户ID添加到请求属性中，方便控制器使用
                    request.setAttribute("userId", userId);
                    
                    return true;
                }
            }
        }
        
        // 如果认证失败，返回401
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("{\"message\":\"请先登录\"}");
        return false;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 请求完成后清理ThreadLocal
        UserContext.clear();
    }
} 