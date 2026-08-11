package mes.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);// 设置响应状态码为401
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String filterPath = (String) request.getAttribute("JWT_ERROR_MSG");
        String msg = filterPath != null ? filterPath : "认证失败，请重新登录";

        Map<String, Object> body = new LinkedHashMap<>();// 保持插入顺序
        body.put("code", HttpServletResponse.SC_UNAUTHORIZED);// 设置响应状态码为401
        body.put("msg", msg);
        body.put("success", false);
        body.put("timestamp", System.currentTimeMillis());

        PrintWriter out = response.getWriter();
        out.write(objectMapper.writeValueAsString(body));
        out.flush();
        out.close();
    }
}
