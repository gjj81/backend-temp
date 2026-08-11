package mes.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import mes.config.JwtProperties;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {
    public static final int TOKEN_VALID = 0;        // 合法
    public static final int TOKEN_EXPIRED = 1;      // ✅ 已过期 = 登录过期（你要区分的这个）
    public static final int TOKEN_INVALID = 2;

    private final JwtProperties jwtProperties;
    private final SecretKey secretKey; // 密钥


    public JwtTokenProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.secretKey  = Keys.hmacShaKeyFor(
                jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8)
        );
    }

    public String generateAccessToken(String userId , String username ) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getExpiration());
        return Jwts.builder()
                .subject(userId)
                .claim("username", username)
                .claim("type", "access") // 自定义字段
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    public String generateRefreshToken(String userId , String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getRefreshExpiration());
        return Jwts.builder()
                .subject(userId)
                .claim("type", "refresh") // 自定义字段
                .claim("username", username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    public Claims parseToken(String token) {// 解析token 返回
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getUserId(String token) {
        return parseToken(token).getSubject();
    }

    public String getUsername(String token) {
        return parseToken(token).get("username", String.class);
    }

    public String getTokenType(String token) {
        return parseToken(token).get("type", String.class);
    }

    public int validateToken(String token) {
        try {
            parseToken(token);
            return TOKEN_VALID;
        } catch (ExpiredJwtException e) {
            log.error("jwt 已经过期");
            return TOKEN_EXPIRED;// 已过期
        } catch (Exception e) {
            log.error("jwt 验证失败");
            return TOKEN_INVALID; // 无效token
        }
    }

    public long getRemainingTime(String token) {
        Claims claims = parseToken(token);
        return claims.getExpiration().getTime() - System.currentTimeMillis();// 剩余时间
    }

    public boolean isExpiringSoon(String token) {
        return getRemainingTime(token) <= 5*60*1000; // 5分钟过期时间
    }

}
