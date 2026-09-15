package mes.service.impl;

import lombok.extern.slf4j.Slf4j;
import mes.config.JwtProperties;
import mes.service.RedisTokenService;
import mes.util.JwtTokenProvider;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RedisTokenServiceImpl implements RedisTokenService {

    private final StringRedisTemplate stringRedisTemplate;
    private final JwtProperties jwtProvider;

    public RedisTokenServiceImpl(StringRedisTemplate stringRedisTemplate, JwtTokenProvider jwtTokenProvider, JwtProperties jwtProvider) {
        this.stringRedisTemplate = stringRedisTemplate;

        this.jwtProvider = jwtProvider;
    }

    private String refreshKey(String userId, String deviceType) {
        return jwtProvider.getRefreshKeyPrefix() + userId + ":" + deviceType;
    }

    private String refreshKeyPattern(String userId) {
        return jwtProvider.getRefreshKeyPrefix() + userId + ":*";
    }

    private String metaKey(String userId, String deviceType) {
        return jwtProvider.getMetaKeyPrefix() + userId + ":" + deviceType;
    }


    @Override
    public void saveRefreshToken(String userId, String deviceType, String refreshToken) {
        String key = refreshKey(userId, deviceType);
        stringRedisTemplate.opsForValue().set(key, refreshToken, jwtProvider.getRefreshExpiration(),
                TimeUnit.MILLISECONDS
        );
        log.info("RefreshToken 已存储: userId={}, deviceType={}", userId, deviceType);
    }

    @Override
    public String getRefreshToken(String userId, String deviceType) {
        return stringRedisTemplate.opsForValue().get(refreshKey(userId, deviceType));
    }

    @Override
    public void deleteRefreshToken(String userId, String deviceType) {
        stringRedisTemplate.delete(refreshKey(userId, deviceType));
        log.info("RefreshToken 已删除: userId={}, deviceType={}", userId, deviceType);
    }

    @Override
    public void deleteAllRefreshToken(String userId) {
        stringRedisTemplate.delete(refreshKeyPattern(userId));
        log.info("所有RefreshToken 已删除: userId={}", userId);
    }

    @Override
    public Long getTokenVersion(String userId, String deviceType) {
        String key = metaKey(userId, deviceType);
        String versionStr = stringRedisTemplate.opsForValue().get(key);
        if (versionStr == null) {
            return null;
        }
        return Long.parseLong(versionStr);
    }

    @Override
    public Long updateTokenVersion(String userId, String deviceType) {
        String key = metaKey(userId, deviceType);
        Long newVersion = System.currentTimeMillis();// 更新版本号为当前时间戳
        stringRedisTemplate.opsForValue().set(key, newVersion.toString(), jwtProvider.getExpiration(),
                TimeUnit.MILLISECONDS
        );
        log.info("Token版本已更新: userId={}, deviceType={}, newVersion={}", userId, deviceType, newVersion);
        return newVersion;
    }

    @Override
    public boolean validateTokenVersion(String userId, String deviceType, Long tokenVersion) {
        if (tokenVersion == null) {
            return false;
        }
        Long currentVersion = getTokenVersion(userId, deviceType);// 获取当前版本号
        if (currentVersion == null) {
            return false;
        }
        return tokenVersion.equals(currentVersion);
    }

    @Override
    public void kickSingleDevice(String userId, String deviceType) {
        updateTokenVersion(userId, deviceType);// 更新版本号，使之前的token失效
        deleteRefreshToken(userId, deviceType);// 删除refreshToken，使之前的token失效
        log.info("设备 {} 已被踢出: userId={}", deviceType, userId);
    }

    @Override
    public void kickAllDevices(String userId) {
        Set<String> refreshKeys = stringRedisTemplate.keys(refreshKeyPattern(userId));
        if (!refreshKeys.isEmpty()) {
            for (String rk : refreshKeys) {
                String deviceType = rk.substring(rk.lastIndexOf(":") + 1);
                updateTokenVersion(userId, deviceType);
            }
        }
        deleteAllRefreshToken(userId);// 删除所有refreshToken，使所有token失效
        log.info("所有设备已踢出: userId={}", userId);
    }
}
