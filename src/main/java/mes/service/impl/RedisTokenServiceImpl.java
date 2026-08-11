package mes.service.impl;

import lombok.extern.slf4j.Slf4j;
import mes.service.RedisTokenService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RedisTokenServiceImpl implements RedisTokenService {

    private final StringRedisTemplate redisRedisTemplate;

    private static final String REFRESH_KEY = "mes:refresh:";
    private static final String BLACKLIST_KEY = "mes:blacklist:";

    public RedisTokenServiceImpl(StringRedisTemplate redisTokenService) {
        this.redisRedisTemplate = redisTokenService;
    }

    @Override
    public void saveRefreshToken(String userId, String refreshToken, long expirationMillis) {
        String key = REFRESH_KEY + userId;
        redisRedisTemplate.opsForValue().set(key, refreshToken, expirationMillis, TimeUnit.MILLISECONDS);
        log.info("Refresh 已经存入 redis userId: {}", userId);
    }

    @Override
    public boolean validateRefreshToken(String userId, String refreshToken) {
        String key = REFRESH_KEY + userId;
        String stored = redisRedisTemplate.opsForValue().get(key);
        log.info("Refresh 验证 userId: {} refreshToken: {}", userId, stored);
        return stored != null && stored.equals(refreshToken);
    }

    @Override
    public void deleteRefreshToken(String userId) {
        String key = REFRESH_KEY + userId;
        redisRedisTemplate.delete(key);
        log.info("Refresh 已从 redis 删除 userId: {}", userId);
    }

    @Override
    public void blacklistAccessToken(String accessToken, long remainingMillis) {
        if(remainingMillis > 0){
            String key = BLACKLIST_KEY + accessToken;
            redisRedisTemplate.opsForValue().set(key, "1", remainingMillis, TimeUnit.MILLISECONDS);// 加入黑名单，设置过期时间
            log.info("Access 已加入黑名单 userId: {}", accessToken);
        }
    }

    @Override
    public boolean validateAccessToken(String accessToken) {
        return redisRedisTemplate.hasKey(BLACKLIST_KEY + accessToken);
    }

    @Override
    public boolean hasRefreshToken(String userId) {
        return redisRedisTemplate.hasKey(REFRESH_KEY + userId);// 如果userId对应的refreshToken存在，则返回true
    }
    @Override
    public boolean isBlacklisted(String accessToken) {
        return redisRedisTemplate.hasKey(BLACKLIST_KEY + accessToken);// 如果token已经在黑名单中，则返回true
    }
}
