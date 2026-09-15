package mes.entity;

import java.util.Date;
import lombok.Data;

/**
 * 生产排程
 * @TableName prod_schedule
 */
@Data
public class ProdSchedule {
    /**
     * 
     */
    private String scheduleId;

    /**
     * 关联sales_plan_line
     */
    private String lineId;

    /**
     * 排产日期
     */
    private Date scheduleDate;

    /**
     * 排产数量
     */
    private Integer quantity;

    /**
     * 车间ID（预留）
     */
    private String workshopId;

    /**
     * 车间名称（冗余）
     */
    private String workshopName;

    /**
     * 是否超产 0否 1是
     */
    private Integer isOverCapacity;

    /**
     * 实际完成数（预留）
     */
    private Integer actualQuantity;

    /**
     * 合格数（预留）
     */
    private Integer qualifiedQuantity;

    /**
     * 不良数（预留）
     */
    private Integer defectQuantity;

    /**
     * 0待确认 1已确认 2已完工
     */
    private Integer status;

    /**
     * 
     */
    private String remark;

    /**
     * 乐观锁
     */
    private Integer version;

    /**
     * 
     */
    private String createBy;

    /**
     * 
     */
    private Date createTime;

    /**
     * 
     */
    private String updateBy;

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
        ProdSchedule other = (ProdSchedule) that;
        return (this.getScheduleId() == null ? other.getScheduleId() == null : this.getScheduleId().equals(other.getScheduleId()))
            && (this.getLineId() == null ? other.getLineId() == null : this.getLineId().equals(other.getLineId()))
            && (this.getScheduleDate() == null ? other.getScheduleDate() == null : this.getScheduleDate().equals(other.getScheduleDate()))
            && (this.getQuantity() == null ? other.getQuantity() == null : this.getQuantity().equals(other.getQuantity()))
            && (this.getWorkshopId() == null ? other.getWorkshopId() == null : this.getWorkshopId().equals(other.getWorkshopId()))
            && (this.getWorkshopName() == null ? other.getWorkshopName() == null : this.getWorkshopName().equals(other.getWorkshopName()))
            && (this.getIsOverCapacity() == null ? other.getIsOverCapacity() == null : this.getIsOverCapacity().equals(other.getIsOverCapacity()))
            && (this.getActualQuantity() == null ? other.getActualQuantity() == null : this.getActualQuantity().equals(other.getActualQuantity()))
            && (this.getQualifiedQuantity() == null ? other.getQualifiedQuantity() == null : this.getQualifiedQuantity().equals(other.getQualifiedQuantity()))
            && (this.getDefectQuantity() == null ? other.getDefectQuantity() == null : this.getDefectQuantity().equals(other.getDefectQuantity()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getRemark() == null ? other.getRemark() == null : this.getRemark().equals(other.getRemark()))
            && (this.getVersion() == null ? other.getVersion() == null : this.getVersion().equals(other.getVersion()))
            && (this.getCreateBy() == null ? other.getCreateBy() == null : this.getCreateBy().equals(other.getCreateBy()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateBy() == null ? other.getUpdateBy() == null : this.getUpdateBy().equals(other.getUpdateBy()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getScheduleId() == null) ? 0 : getScheduleId().hashCode());
        result = prime * result + ((getLineId() == null) ? 0 : getLineId().hashCode());
        result = prime * result + ((getScheduleDate() == null) ? 0 : getScheduleDate().hashCode());
        result = prime * result + ((getQuantity() == null) ? 0 : getQuantity().hashCode());
        result = prime * result + ((getWorkshopId() == null) ? 0 : getWorkshopId().hashCode());
        result = prime * result + ((getWorkshopName() == null) ? 0 : getWorkshopName().hashCode());
        result = prime * result + ((getIsOverCapacity() == null) ? 0 : getIsOverCapacity().hashCode());
        result = prime * result + ((getActualQuantity() == null) ? 0 : getActualQuantity().hashCode());
        result = prime * result + ((getQualifiedQuantity() == null) ? 0 : getQualifiedQuantity().hashCode());
        result = prime * result + ((getDefectQuantity() == null) ? 0 : getDefectQuantity().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getRemark() == null) ? 0 : getRemark().hashCode());
        result = prime * result + ((getVersion() == null) ? 0 : getVersion().hashCode());
        result = prime * result + ((getCreateBy() == null) ? 0 : getCreateBy().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getUpdateBy() == null) ? 0 : getUpdateBy().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", scheduleId=").append(scheduleId);
        sb.append(", lineId=").append(lineId);
        sb.append(", scheduleDate=").append(scheduleDate);
        sb.append(", quantity=").append(quantity);
        sb.append(", workshopId=").append(workshopId);
        sb.append(", workshopName=").append(workshopName);
        sb.append(", isOverCapacity=").append(isOverCapacity);
        sb.append(", actualQuantity=").append(actualQuantity);
        sb.append(", qualifiedQuantity=").append(qualifiedQuantity);
        sb.append(", defectQuantity=").append(defectQuantity);
        sb.append(", status=").append(status);
        sb.append(", remark=").append(remark);
        sb.append(", version=").append(version);
        sb.append(", createBy=").append(createBy);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateBy=").append(updateBy);
        sb.append(", updateTime=").append(updateTime);
        sb.append("]");
        return sb.toString();
    }
}