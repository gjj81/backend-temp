package mes.controller;

import lombok.extern.slf4j.Slf4j;
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

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public Result<Map<String,Object>> login(@RequestBody LoginDTO loginDTO) {
        log.info("用户尝试登录");
        return authService.login(loginDTO);

    }

    @PostMapping("/refresh")
    public Result<Map<String,Object>> refresh(@RequestHeader("X-Refresh-Token") String refreshToken) {
        return authService.refresh(refreshToken);
    }




}
