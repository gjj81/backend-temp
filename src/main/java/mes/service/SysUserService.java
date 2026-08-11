package mes.service;

import mes.common.result.Result;
import mes.dto.AddUserDTO;
import mes.entity.SysUser;

public interface SysUserService {
    int add(AddUserDTO addUserDTO);
    int update(SysUser sysUser);
    int delete(String userId);
    Result<SysUser> getById(String userId);


}
