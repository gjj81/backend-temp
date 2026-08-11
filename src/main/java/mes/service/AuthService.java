package mes.service;

import mes.common.result.Result;
import mes.dto.LoginDTO;

import java.util.Map;

public interface AuthService {
    Result<Map<String,Object>> login(LoginDTO loginDTO);
    Result<Map<String,Object>> refresh(String refreshToken);
}
