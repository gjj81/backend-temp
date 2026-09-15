package mes.controller;

import lombok.extern.slf4j.Slf4j;
import mes.common.enums.DeviceTypeEnum;
import mes.common.result.Result;
import mes.dto.LoginDTO;

import mes.service.AuthService;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping ("/api/auth")
public class AuthController {

    private final AuthService authService;
    private DeviceTypeEnum deviceType;


    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public Result<Map<String,Object>> login(@RequestBody LoginDTO loginDTO,@RequestHeader(name = "X-Device-Type") String deviceType) {
        log.info("用户尝试登录");
        return authService.login(loginDTO, deviceType);

    }

    @PostMapping("/refresh")
    public Result<Map<String, Object>> refresh(
            @RequestHeader("X-Refresh-Token") String refreshToken,
            @RequestHeader(name = "X-Device-Type") String deviceType) {
        log.info("刷新Token: deviceType={}", deviceType);
        return authService.refresh(refreshToken, deviceType);
    }

    @PostMapping("/logout")
    public Result<Map<String, Object>> logout(
            @RequestHeader("Authorization") String authorization) {
        String accessToken = authorization.replace("Bearer ", "");
        log.info("用户登出");
        return authService.logout(accessToken);
    }



}
