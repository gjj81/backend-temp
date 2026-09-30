package mes.service.impl;

import lombok.RequiredArgsConstructor;
import mes.entity.SysMenu;
import mes.mapper.SysMenuMapper;
import mes.service.MenuService;
import mes.vo.base.MenuTreeVO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final SysMenuMapper sysMenuMapper;

    @Override
    public MenuResult loadUserMenus(String userId) { // 加载用户菜单
        List<SysMenu> menus = sysMenuMapper.selectMenusByUserId(userId);
        if (menus == null || menus.isEmpty()) {
            return new MenuResult(new ArrayList<>(), new ArrayList<>());
        }

        List<MenuTreeVO> tree = buildTree(menus);
        List<String> permissions = extractPermissions(menus);
        return new MenuResult(tree, permissions);
    }

    private List<MenuTreeVO> buildTree(List<SysMenu> menus) {
        Map<String, List<MenuTreeVO>> parentMap = menus.stream()
                .map(this::toVO)
                .collect(Collectors.groupingBy(
                        vo -> vo.getParentId() == null ? "0" : vo.getParentId()));

        List<MenuTreeVO> roots = parentMap.getOrDefault("0", new ArrayList<>());
        for (MenuTreeVO root : roots) {
            buildChildren(root, parentMap);
        }
        return roots;
    }

    private void buildChildren(MenuTreeVO parent, Map<String, List<MenuTreeVO>> parentMap) { // 递归构建子菜单
        List<MenuTreeVO> children = parentMap.get(parent.getMenuId());
        if (children != null) {
            parent.setChildren(children);
            for (MenuTreeVO child : children) {
                buildChildren(child, parentMap);
            }
        }
    }

    private MenuTreeVO toVO(SysMenu menu) { // 转换实体为VO
        MenuTreeVO vo = new MenuTreeVO();
        vo.setMenuId(menu.getMenuId());
        vo.setParentId(menu.getParentId());
        vo.setMenuName(menu.getMenuName());
        vo.setMenuType(menu.getMenuType());
        vo.setIcon(menu.getIcon());
        vo.setPath(menu.getPath());
        vo.setComponent(menu.getComponent());
        vo.setQuery(menu.getQuery());
        vo.setPerms(menu.getPerms());
        vo.setIsCache(menu.getIsCache());
        vo.setVisible(menu.getVisible());
        vo.setSort(menu.getSort());
        return vo;
    }

    private List<String> extractPermissions(List<SysMenu> menus) { // 提取权限列表
        return menus.stream()
                .filter(m -> m.getPerms() != null && !m.getPerms().isEmpty())
                .map(SysMenu::getPerms)
                .distinct()
                .collect(Collectors.toList());
    }
}
