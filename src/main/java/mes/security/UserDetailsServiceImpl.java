package mes.security;

import lombok.extern.slf4j.Slf4j;
import mes.entity.SysUser;
import mes.mapper.SysRoleMapper;
import mes.mapper.SysUserMapper;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final SysUserMapper sysUserMapper;

    private final SysRoleMapper sysRoleMapper;


    public UserDetailsServiceImpl(SysUserMapper sysUserMapper, SysRoleMapper sysRoleMapper) {
        this.sysUserMapper = sysUserMapper;
        this.sysRoleMapper = sysRoleMapper;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser sysUser = sysUserMapper.selectByUsername(username);
        if (sysUser == null) {
            log.info("用户不存在");
            throw new UsernameNotFoundException("用户不存在");
        }
        List<String> roleCodes = sysRoleMapper.selectCodeList(sysUser.getUserId());// 角色编码列表

        SecurityUser securityUser = new SecurityUser();
        securityUser.setUserId(sysUser.getUserId());
        securityUser.setUsername(sysUser.getUsername());
        securityUser.setPassword(sysUser.getPassword());
        securityUser.setStatus(sysUser.getStatus());

        List<SimpleGrantedAuthority> authorities = roleCodes.stream()
                .map(roleCode -> new SimpleGrantedAuthority("ROLE_" + roleCode))
                .toList();


        securityUser.setAuthorities(authorities);
        return securityUser;
    }
}
