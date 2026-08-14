package mes.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
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

    // ==================== 校验状态常量 ====================
    public static final int TOKEN_VALID   = 0; // 合法（签名对+没过期）
    public static final int TOKEN_EXPIRED = 1; // 过期（时间到了）
    public static final int TOKEN_INVALID = 2; // 无效（签名错/被篡改/格式错）

    // ==================== 注入 & 初始化 ====================
    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;

    public JwtTokenProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.secretKey = Keys.hmacShaKeyFor(
                jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8)// 密钥
        );
    }

    // ==================== 生成 Token ====================
    public String generateAccessToken(String userId, String username,Long tokenVersion ,String deviceType) {
        Date now = new Date();
        Date expire = new Date(now.getTime() + jwtProperties.getExpiration());
        return Jwts.builder()
                .subject(userId)
                .claim("username", username)
                .claim("type", "access")
                .claim("version", tokenVersion)
                .claim("deviceType", deviceType)
                .issuedAt(now)
                .expiration(expire)
                .signWith(secretKey)
                .compact();
    }

    public String generateRefreshToken(String userId,String username,Long tokenVersion,
                                       String deviceType) {
        Date now = new Date();
        Date expire = new Date(now.getTime() + jwtProperties.getRefreshExpiration());
        return Jwts.builder()
                .subject(userId)
                .claim("username", username)
                .claim("type", "refresh")
                .claim("version", tokenVersion)
                .claim("deviceType", deviceType)
                .issuedAt(now)
                .expiration(expire)
                .signWith(secretKey)
                .compact();
    }


    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Claims parseSafely(String token) {
        try {
            return parseToken(token);
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        } catch (Exception e) {
            log.warn("JWT 解析失败: {}", e.getMessage());
            return null;
        }
    }

    public String getUserId(String token) {
        Claims claims = parseSafely(token);
        return claims != null ? claims.getSubject() : null;
    }
    public String getUsername(String token) {
        Claims claims = parseSafely(token);
        return claims != null ? claims.get("username",String.class) : null;
    }
    public String getTokenType(String token) {
        Claims claims = parseSafely(token);
        return claims != null ? claims.get("type",String.class) : null;
    }
    public Long getTokenVersion(String token) {
        Claims claims = parseSafely(token);
        return claims != null ? claims.get("version",Long.class) : null;
    }
    public String getDeviceType(String token) {
        Claims claims = parseSafely(token);
        return claims != null ? claims.get("deviceType",String.class) : null;
    }
    public int validateToken(String token) {
        Claims claims = parseSafely(token);
        if (claims == null) {
            return TOKEN_INVALID;
        } else if (claims.getExpiration().before(new Date())) {
            return TOKEN_EXPIRED;
        } else {
            return TOKEN_VALID;
        }
    }


}