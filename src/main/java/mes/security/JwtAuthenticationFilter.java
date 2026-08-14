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
    private final RedisTokenService redisTokenService;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, RedisTokenService redisTokenService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.redisTokenService = redisTokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = resolveToken(request);
        if(!StringUtils.hasText(token)) {// 没有token，直接放行
            filterChain.doFilter(request, response);
            return;
        }

        int tokenStatus = jwtTokenProvider.validateToken(token);

        if(tokenStatus == JwtTokenProvider.TOKEN_INVALID) {
            log.warn("无效的token: {} Url: {}", token, request.getRequestURI());
            request.setAttribute("JWT_ERROR_MSG","无效的token");
            filterChain.doFilter(request, response);
            return;
        }
        if (tokenStatus == JwtTokenProvider.TOKEN_EXPIRED) {
            log.info("AccessToken已过期: uri={}", request.getRequestURI());
            request.setAttribute("JWT_ERROR_MSG", "Token已过期，请刷新");
            filterChain.doFilter(request, response);
            return;
        }

        Claims claims = jwtTokenProvider.parseSafely(token);
        String userId = claims.getSubject();
        String username = claims.get("username", String.class);
        String tokenType = claims.get("type", String.class);
        String deviceType = claims.get("deviceType", String.class);
        Long tokenVersion = claims.get("version") != null ?
                ((Number) claims.get("version")).longValue() : null;

        if (!"access".equals(tokenType)) {
            log.warn("非AccessToken被拦截: type={}, uri={}", tokenType, request.getRequestURI());
            request.setAttribute("JWT_ERROR_MSG", "请使用AccessToken访问");
            filterChain.doFilter(request, response);
            return;
        }

        if (!redisTokenService.validateTokenVersion(userId, deviceType, tokenVersion)) {
            log.warn("版本号不匹配（已登出/被踢）: userId={}, deviceType={}", userId, deviceType);
            request.setAttribute("JWT_ERROR_MSG", "Token已失效，请重新登录");
            filterChain.doFilter(request, response);
            return;
        }

        SecurityUser securityUser = new SecurityUser();
        securityUser.setUserId(userId);
        securityUser.setUsername(username);
        securityUser.setPassword("");
        securityUser.setStatus(1);
        securityUser.setAuthorities(Collections.emptyList());// 暂时赋予空权限,后续根据角色动态添加权限

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        securityUser, null, securityUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);// 设置认证信息，后续可以获取到用户信息

        filterChain.doFilter(request, response);

    }

    private String resolveToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return null;
    }
}
