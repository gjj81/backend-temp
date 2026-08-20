package mes.controller;

import lombok.extern.slf4j.Slf4j;
import mes.common.result.Result;
import mes.service.AuthService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AuthService authService;


    public AdminController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/kick")
    @PreAuthorize("hasAuthority('admin')")
    public Result<String> kickAllDevices(@RequestParam String userId) {
        log.info("踢出所有设备: userId={}", userId);
        return authService.kickAllDevices(userId);
    }

    @PostMapping("/kick-single")
    @PreAuthorize("hasAuthority('admin')")
    public Result<String> kickSingleDevice(@RequestParam String userId,
                                           @RequestParam String deviceType) {
        log.info("踢出单设备: userId={}, deviceType={}", userId, deviceType);
        return authService.kickSingleDevice(userId, deviceType);
    }
}