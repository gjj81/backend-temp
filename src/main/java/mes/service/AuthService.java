package mes.service;

import mes.common.result.Result;
import mes.dto.LoginDTO;

import java.util.Map;

public interface AuthService {
    Result<Map<String,Object>> login(LoginDTO loginDTO,String deviceType);
    Result<Map<String,Object>> refresh(String refreshToken,String deviceType);
    Result<Map<String,Object>> logout(String accessToken);
    Result<String> kickAllDevices(String userId);
    Result<String> kickSingleDevice(String userId, String deviceType);
}
