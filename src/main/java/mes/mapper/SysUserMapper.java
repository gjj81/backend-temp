package mes.mapper;

import mes.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

/**
* @author g2026
* @description 针对表【sys_user(用户表)】的数据库操作Mapper
* @createDate 2026-08-07 15:05:03
* @Entity mes.entity.SysUser
*/
@Mapper
public interface SysUserMapper {

    public SysUser selectByUsername(String username);

    public int addUser(SysUser user);

}
