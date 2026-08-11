package mes.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import mes.service.RedisTokenService;
import mes.util.JwtTokenProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = resolveToken(request);

        if(StringUtils.hasText(token) ){
            final int tokenStatus = jwtTokenProvider.validateToken(token);

            if (tokenStatus == JwtTokenProvider.TOKEN_EXPIRED) {
                log.warn("Access Token 无效（过期/签名错/篡改）uri={}", request.getRequestURI());
                request.setAttribute("JWT_ERROR_MSG", "请重新登录");
            }

            else if (tokenStatus == JwtTokenProvider.TOKEN_INVALID) {
                log.warn("Access Token 已在黑名单中，拒绝访问 token: {}", token);
                request.setAttribute("JWT_ERROR_MSG", "请重新登录");
            }
            else if (tokenStatus == JwtTokenProvider.TOKEN_VALID){
                Claims claims = jwtTokenProvider.parseToken(token);
                String username = claims.get("username", String.class);
                UserDetails userDetails = User.builder()// 这里构建了一个UserDetails对象，可以根据实际情况调整
                        .username(username)
                        .password("")// 这里传入的是空字符串，因为我们已经通过token解析出了用户信息
                        .authorities(Collections.emptyList())// 这里可以根据实际情况添加权限信息
                        .roles("USER")// 这里传入的是角色信息，可以根据实际情况调整
                        .build();
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(// 这里传入的是UserDetails对象，而不是用户名和密码
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);// 将认证信息放入SecurityContextHolder中
            }

        }
        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) { // 去掉Bearer前缀
            return bearerToken.substring(7); // 返回实际的token字符串
        }
        return null;
    }
}
