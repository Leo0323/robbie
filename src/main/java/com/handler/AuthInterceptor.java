package com.handler;
import com.service.UserService;
import com.vo.Users;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
@Component
//AuthInterceptor为自定义拦截器
public class AuthInterceptor implements HandlerInterceptor {
    @Resource
    private UserService userService;
    //如果不再WebMvcConfig 中配置拦截器，preHandle方法永远不会被执行
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        System.out.println("Interceptor called: " + request.getRequestURI());
// 先放行预检请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return true;
        }

        String auth = request.getHeader("Authorization");
        if (auth == null || auth.isEmpty()) {
            response.setStatus(401);
            return false;
        }
        String token;
// 前端如果传的是 Bearer xxx
        if (auth.startsWith("Bearer ")) {
            token = auth.substring(7);
        } else {
            // 前端直接传 token
            token = auth;
        }
        Users loginUser = userService.getLoginUser(token);
        if (loginUser == null) {
            response.setStatus(401);
            System.out.println("Authorization header = " + auth);
            return false;
        }
        System.out.println("Authorization header = " + auth);
        request.setAttribute("loginUser", loginUser);
        return true;
    }
        // 获取 session 中用户
//        Users loginUser = (Users) request.getSession().getAttribute("loginUser");
//
//        if (loginUser == null) {
//            response.setStatus(401);
//            return false;
//        }
//
//        request.setAttribute("loginUser", loginUser);
//        return true;
//    }
}
