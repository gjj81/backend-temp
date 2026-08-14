package mes.entity;

import lombok.Data;

/**
 * 角色部门数据权限
 * @TableName sys_role_dept
 */
@Data
public class SysRoleDept {
    /**
     * 
     */
    private String roleId;

    /**
     * 
     */
    private String deptId;

    /**
     * 1-全部 2-本部门 3-本级子 4-本人
     */
    private Integer dataScope;

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
        SysRoleDept other = (SysRoleDept) that;
        return (this.getRoleId() == null ? other.getRoleId() == null : this.getRoleId().equals(other.getRoleId()))
            && (this.getDeptId() == null ? other.getDeptId() == null : this.getDeptId().equals(other.getDeptId()))
            && (this.getDataScope() == null ? other.getDataScope() == null : this.getDataScope().equals(other.getDataScope()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getRoleId() == null) ? 0 : getRoleId().hashCode());
        result = prime * result + ((getDeptId() == null) ? 0 : getDeptId().hashCode());
        result = prime * result + ((getDataScope() == null) ? 0 : getDataScope().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", roleId=").append(roleId);
        sb.append(", deptId=").append(deptId);
        sb.append(", dataScope=").append(dataScope);
        sb.append("]");
        return sb.toString();
    }
}