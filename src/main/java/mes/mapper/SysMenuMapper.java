package mes.mapper;

import mes.entity.SysMenu;

import java.util.List;

/**
* @author g2026
* @description 针对表【sys_menu(菜单权限)】的数据库操作Mapper
* @createDate 2026-08-14 09:16:09
* @Entity mes.entity.SysMenu
*/
public interface SysMenuMapper {


    List<String> selectPermsByUserId(String userId);

    List<SysMenu> selectUrlPermissions();

}
