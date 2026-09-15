package mes.entity;

import lombok.Data;

/**
 * 菜单权限表
 * @TableName sys_menu
 */
@Data
public class SysMenu {
    /**
     * 
     */
    private String menuId;

    /**
     * 父菜单id，0=顶级
     */
    private String parentId;

    /**
     * 菜单名称
     */
    private String menuName;

    /**
     * 类型：1-目录 2-菜单 3-按钮
     */
    private Integer menuType;

    /**
     * 图标
     */
    private String icon;

    /**
     * 前端路由路径
     */
    private String path;

    /**
     * 前端组件路径
     */
    private String component;

    /**
     * 路由参数
     */
    private String query;

    /**
     * 后端接口URL，如/api/user/list，用于动态权限拦截
     */
    private String apiUrl;

    /**
     * 权限标识，如sys:user:add
     */
    private String perms;

    /**
     * 是否显示：0-隐藏 1-显示
     */
    private Integer visible;

    /**
     * 是否缓存：0-否 1-是
     */
    private Integer isCache;

    /**
     * 0-禁用 1-正常
     */
    private Integer status;

    /**
     * 逻辑删除：0-正常 1-删除
     */
    private Integer deleted;

    /**
     * 排序
     */
    private Integer sort;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        SysMenu other = (SysMenu) that;
        return (this.getMenuId() == null ? other.getMenuId() == null : this.getMenuId().equals(other.getMenuId()))
            && (this.getParentId() == null ? other.getParentId() == null : this.getParentId().equals(other.getParentId()))
            && (this.getMenuName() == null ? other.getMenuName() == null : this.getMenuName().equals(other.getMenuName()))
            && (this.getMenuType() == null ? other.getMenuType() == null : this.getMenuType().equals(other.getMenuType()))
            && (this.getIcon() == null ? other.getIcon() == null : this.getIcon().equals(other.getIcon()))
            && (this.getPath() == null ? other.getPath() == null : this.getPath().equals(other.getPath()))
            && (this.getComponent() == null ? other.getComponent() == null : this.getComponent().equals(other.getComponent()))
            && (this.getQuery() == null ? other.getQuery() == null : this.getQuery().equals(other.getQuery()))
            && (this.getApiUrl() == null ? other.getApiUrl() == null : this.getApiUrl().equals(other.getApiUrl()))
            && (this.getPerms() == null ? other.getPerms() == null : this.getPerms().equals(other.getPerms()))
            && (this.getVisible() == null ? other.getVisible() == null : this.getVisible().equals(other.getVisible()))
            && (this.getIsCache() == null ? other.getIsCache() == null : this.getIsCache().equals(other.getIsCache()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getDeleted() == null ? other.getDeleted() == null : this.getDeleted().equals(other.getDeleted()))
            && (this.getSort() == null ? other.getSort() == null : this.getSort().equals(other.getSort()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getMenuId() == null) ? 0 : getMenuId().hashCode());
        result = prime * result + ((getParentId() == null) ? 0 : getParentId().hashCode());
        result = prime * result + ((getMenuName() == null) ? 0 : getMenuName().hashCode());
        result = prime * result + ((getMenuType() == null) ? 0 : getMenuType().hashCode());
        result = prime * result + ((getIcon() == null) ? 0 : getIcon().hashCode());
        result = prime * result + ((getPath() == null) ? 0 : getPath().hashCode());
        result = prime * result + ((getComponent() == null) ? 0 : getComponent().hashCode());
        result = prime * result + ((getQuery() == null) ? 0 : getQuery().hashCode());
        result = prime * result + ((getApiUrl() == null) ? 0 : getApiUrl().hashCode());
        result = prime * result + ((getPerms() == null) ? 0 : getPerms().hashCode());
        result = prime * result + ((getVisible() == null) ? 0 : getVisible().hashCode());
        result = prime * result + ((getIsCache() == null) ? 0 : getIsCache().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getDeleted() == null) ? 0 : getDeleted().hashCode());
        result = prime * result + ((getSort() == null) ? 0 : getSort().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", menuId=").append(menuId);
        sb.append(", parentId=").append(parentId);
        sb.append(", menuName=").append(menuName);
        sb.append(", menuType=").append(menuType);
        sb.append(", icon=").append(icon);
        sb.append(", path=").append(path);
        sb.append(", component=").append(component);
        sb.append(", query=").append(query);
        sb.append(", apiUrl=").append(apiUrl);
        sb.append(", perms=").append(perms);
        sb.append(", visible=").append(visible);
        sb.append(", isCache=").append(isCache);
        sb.append(", status=").append(status);
        sb.append(", deleted=").append(deleted);
        sb.append(", sort=").append(sort);
        sb.append("]");
        return sb.toString();
    }
}