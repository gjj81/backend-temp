package mes.entity;

import java.util.Date;
import lombok.Data;

/**
 * 车间
 * @TableName base_workshop
 */
@Data
public class BaseWorkshop {
    /**
     * 
     */
    private String workshopId;

    /**
     * 车间编号
     */
    private String workshopCode;

    /**
     * 车间名称，如：机加车间
     */
    private String workshopName;

    /**
     * 1-启用 0-停用
     */
    private Integer status;

    /**
     * 
     */
    private Integer deleted;

    /**
     * 
     */
    private Date createTime;

    /**
     * 
     */
    private Date updateTime;

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
        BaseWorkshop other = (BaseWorkshop) that;
        return (this.getWorkshopId() == null ? other.getWorkshopId() == null : this.getWorkshopId().equals(other.getWorkshopId()))
            && (this.getWorkshopCode() == null ? other.getWorkshopCode() == null : this.getWorkshopCode().equals(other.getWorkshopCode()))
            && (this.getWorkshopName() == null ? other.getWorkshopName() == null : this.getWorkshopName().equals(other.getWorkshopName()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getDeleted() == null ? other.getDeleted() == null : this.getDeleted().equals(other.getDeleted()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getWorkshopId() == null) ? 0 : getWorkshopId().hashCode());
        result = prime * result + ((getWorkshopCode() == null) ? 0 : getWorkshopCode().hashCode());
        result = prime * result + ((getWorkshopName() == null) ? 0 : getWorkshopName().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getDeleted() == null) ? 0 : getDeleted().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", workshopId=").append(workshopId);
        sb.append(", workshopCode=").append(workshopCode);
        sb.append(", workshopName=").append(workshopName);
        sb.append(", status=").append(status);
        sb.append(", deleted=").append(deleted);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append("]");
        return sb.toString();
    }
}