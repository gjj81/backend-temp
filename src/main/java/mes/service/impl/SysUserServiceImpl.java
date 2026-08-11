package mes.service.impl;

import mes.common.result.Result;
import mes.dto.AddUserDTO;
import mes.entity.SysUser;
import mes.mapper.SysUserMapper;
import mes.service.SysUserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SysUserServiceImpl implements SysUserService {
    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;

    public SysUserServiceImpl(SysUserMapper sysUserMapper, PasswordEncoder passwordEncoder) {
        this.sysUserMapper = sysUserMapper;
        this.passwordEncoder = passwordEncoder;
    }
    @Override
    public int add(AddUserDTO addUserDTO) {
        // 实现添加用户逻辑
        SysUser sysUser = new SysUser();
        sysUser.setUserId(UUID.randomUUID().toString());// 生成UUID作为用户ID
        sysUser.setUsername(addUserDTO.getUsername());
        String encryptedPassword = passwordEncoder.encode(addUserDTO.getPassword());// 加密密码
        sysUser.setPassword(encryptedPassword);// 设置加密后的密码
        sysUser.setRealName(addUserDTO.getRealName());
        sysUser.setPhone(addUserDTO.getPhone());
        sysUser.setEmail(addUserDTO.getEmail());
        sysUser.setDeptId(addUserDTO.getDeptId());
        sysUser.setStatus(addUserDTO.getStatus());

        return sysUserMapper.addUser(sysUser);
    }

    @Override
    public int update(SysUser sysUser) {
        return 0;
    }

    @Override
    public int delete(String userId) {
        return 0;
    }

    @Override
    public Result<SysUser> getById(String userId) {
        return null;
    }
}
