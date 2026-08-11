package mes.mapper;

import mes.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author g2026
* @description 针对表【sys_role(角色表)】的数据库操作Mapper
* @createDate 2026-08-07 15:05:30
* @Entity mes.entity.SysRole
*/
@Mapper
public interface SysRoleMapper {
    public List<String> selectCodeList(String userId);



}
