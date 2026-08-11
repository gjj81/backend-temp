package mes.service.impl;

import lombok.extern.slf4j.Slf4j;
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

import java.util.Map;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;
    private final RedisTokenService redisTokenService;

    public AuthServiceImpl(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, JwtProperties jwtProperties, RedisTokenService redisTokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtProperties = jwtProperties;
        this.redisTokenService = redisTokenService;
    }
    @Override
    public Result<Map<String, Object>> login(LoginDTO loginDTO) {
        // 实现登录逻辑，生成token并返回
        Authentication authentication = authenticationManager.authenticate(  // 认证用户身份
                new UsernamePasswordAuthenticationToken(
                        loginDTO.getUsername(),
                        loginDTO.getPassword()
                )
        );

        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        String userId = securityUser.getUserId();
        String username = securityUser.getUsername();

        String token = jwtTokenProvider.generateAccessToken(userId, username);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userId, username);

        redisTokenService.saveRefreshToken(userId, refreshToken, jwtProperties.getRefreshExpiration());

        Map<String, Object> data = Map.of(// 返回数据
                "accessToken", token,
                "refreshToken", refreshToken,
                "expiration", jwtProperties.getExpiration() / 1000,
                "userId", userId,
                "username", username

        );

        log.info("用户登录成功，返回token");
        return Result.success(data);
    }

    @Override
    public Result<Map<String, Object>> refresh(String refreshToken) {
        if (jwtTokenProvider.validateToken(refreshToken) != JwtTokenProvider.TOKEN_VALID) {
            return Result.error(401,"登录过期");
        }
        if(!"refresh".equals(jwtTokenProvider.getTokenType(refreshToken))){
            return Result.error(401,"无效的token");
        }

        String userId = jwtTokenProvider.getUserId(refreshToken);
        String username = jwtTokenProvider.getUsername(refreshToken);

        if(!redisTokenService.validateRefreshToken(userId, refreshToken)){
            log.warn("RefreshToken 已失效 userId: {} refreshToken: {}",userId,refreshToken);
            return Result.error(401,"登录失效");
        }
        String newAccessToken = jwtTokenProvider.generateAccessToken(userId, username);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(userId, username);
        redisTokenService.saveRefreshToken(userId, newRefreshToken, jwtProperties.getRefreshExpiration());
        Map<String, Object> data = Map.of(
                "accessToken", newAccessToken,
                "refreshToken", newRefreshToken,
                "expiration", jwtProperties.getExpiration() / 1000
        );
        return Result.success(data);
    }
}
