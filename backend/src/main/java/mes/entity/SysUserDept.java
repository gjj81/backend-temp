package mes.entity;

import lombok.Data;

/**
 * 用户部门关联
 * @TableName sys_user_dept
 */
@Data
public class SysUserDept {
    /**
     * 用户id
     */
    private String userId;

    /**
     * 部门id
     */
    private String deptId;

    /**
     * 0-兼 1-主
     */
    private Integer isPrimary;

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
        SysUserDept other = (SysUserDept) that;
        return (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getDeptId() == null ? other.getDeptId() == null : this.getDeptId().equals(other.getDeptId()))
            && (this.getIsPrimary() == null ? other.getIsPrimary() == null : this.getIsPrimary().equals(other.getIsPrimary()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getDeptId() == null) ? 0 : getDeptId().hashCode());
        result = prime * result + ((getIsPrimary() == null) ? 0 : getIsPrimary().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", userId=").append(userId);
        sb.append(", deptId=").append(deptId);
        sb.append(", isPrimary=").append(isPrimary);
        sb.append("]");
        return sb.toString();
    }
}