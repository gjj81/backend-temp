package mes.service;

import mes.vo.base.MenuTreeVO;

import java.util.List;

public interface MenuService {

    MenuResult loadUserMenus(String userId);

    record MenuResult(List<MenuTreeVO> menus, List<String> permissions) {}
}
