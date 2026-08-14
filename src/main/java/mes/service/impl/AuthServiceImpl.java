package mes.service.impl;

import lombok.extern.slf4j.Slf4j;
import mes.common.enums.DeviceTypeEnum;
import mes.common.result.Result;
import mes.config.JwtProperties;
import mes.dto.LoginDTO;
import mes.security.SecurityUser;
import mes.service.AuthService;
import mes.service.RedisTokenService;
import mes.util.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;
    private final RedisTokenService redisTokenService;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           JwtTokenProvider jwtTokenProvider,
                           JwtProperties jwtProperties,
                           RedisTokenService redisTokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtProperties = jwtProperties;
        this.redisTokenService = redisTokenService;
    }


    @Override
    public Result<Map<String, Object>> login(LoginDTO loginDTO,String deviceType) {
        if (!DeviceTypeEnum.isValid(deviceType)) {
            return Result.error(400, "不支持的设备类型: " + deviceType);
        }
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDTO.getUsername(),
                        loginDTO.getPassword())
        );
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        String userId = securityUser.getUserId();
        String username = securityUser.getUsername();

        Long tokenVersion = redisTokenService.updateTokenVersion(userId, deviceType);
        // ③ 用同一个版本号生成双Token
        String accessToken = jwtTokenProvider.generateAccessToken(
                userId, username, tokenVersion, deviceType);
        String refreshToken = jwtTokenProvider.generateRefreshToken(
                userId, username, tokenVersion, deviceType);

        redisTokenService.saveRefreshToken(userId, deviceType, refreshToken);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("accessToken", accessToken);
        data.put("refreshToken", refreshToken);
        data.put("expiresIn", jwtProperties.getExpiration() / 1000);
        data.put("userId", userId);
        data.put("username", username);
        data.put("deviceType", deviceType);

        log.info("用户登录成功: userId={}, deviceType={}", userId, deviceType);
        return Result.success(data);
    }

    @Override
    public Result<Map<String, Object>> refresh(String refreshToken, String deviceType) {
        if (!DeviceTypeEnum.isValid(deviceType)) {
            return Result.error(400, "不支持的设备类型: " + deviceType);
        }
        if (jwtTokenProvider.validateToken(refreshToken) != JwtTokenProvider.TOKEN_VALID) {
            return Result.error(401, "登录已过期，请重新登录");
        }

        // ② 验证类型必须是refresh
        if (!"refresh".equals(jwtTokenProvider.getTokenType(refreshToken))) {
            return Result.error(401, "无效的Token");
        }
        String userId = jwtTokenProvider.getUserId(refreshToken);
        String username = jwtTokenProvider.getUsername(refreshToken);

        String storedToken = redisTokenService.getRefreshToken(userId, deviceType);
        if (storedToken == null || !storedToken.equals(refreshToken)) {
            log.warn("RefreshToken不匹配（已失效/被替换）: userId={}, deviceType={}", userId, deviceType);
            return Result.error(401, "登录已失效，请重新登录");
        }
        Long newVersion = redisTokenService.updateTokenVersion(userId, deviceType);

        String newAccessToken = jwtTokenProvider.generateAccessToken(
                userId, username, newVersion, deviceType);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(
                userId, username, newVersion, deviceType);

        // ⑥ 保存新RefreshToken
        redisTokenService.saveRefreshToken(userId, deviceType, newRefreshToken);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("accessToken", newAccessToken);
        data.put("refreshToken", newRefreshToken);
        data.put("expiresIn", jwtProperties.getExpiration() / 1000);
        data.put("userId", userId);
        data.put("username", username);
        data.put("deviceType", deviceType);

        log.info("Token刷新成功: userId={}, deviceType={}", userId, deviceType);
        return Result.success(data);

    }

    @Override
    public Result<Map<String, Object>> logout(String accessToken) {
        // ① 解析Token（过期也能解析）
        String userId = jwtTokenProvider.getUserId(accessToken);
        String deviceType = jwtTokenProvider.getDeviceType(accessToken);

        if (userId == null || deviceType == null) {
            return Result.error(400, "无效的Token");
        }

        // ② 踢出当前设备：更新版本号 + 删除RefreshToken
        redisTokenService.kickSingleDevice(userId, deviceType);

        log.info("用户登出: userId={}, deviceType={}", userId, deviceType);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("message", "已登出");
        return Result.success(data);
    }

    @Override
    public Result<String> kickAllDevices(String userId) {
        redisTokenService.kickAllDevices(userId);
        log.info("管理员踢出所有设备: userId={}", userId);
        return Result.success("已踢出所有设备");
    }

    @Override
    public Result<String> kickSingleDevice(String userId, String deviceType) {
        redisTokenService.kickSingleDevice(userId, deviceType);
        log.info("管理员踢出单设备: userId={}, deviceType={}", userId, deviceType);
        return Result.success("已踢出设备: " + deviceType);
    }
}