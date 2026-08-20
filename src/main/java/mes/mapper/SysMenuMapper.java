package mes.mapper;

import mes.entity.SysMenu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author g2026
* @description 针对表【sys_menu(菜单权限)】的数据库操作Mapper
* @createDate 2026-08-14 09:16:09
* @Entity mes.entity.SysMenu
*/
@Mapper
public interface SysMenuMapper {


    List<String> selectPermsByUserId(String userId);// 根据用户ID查询权限列表

    List<SysMenu> selectUrlPermissions();// 查询所有URL权限菜单

    List<SysMenu> selectMenusByUserId(String userId);// 根据用户ID查询菜单列表


}
