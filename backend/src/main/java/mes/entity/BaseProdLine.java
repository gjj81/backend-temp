package mes.entity;

import java.util.Date;
import lombok.Data;

/**
 * 产线
 * @TableName base_prod_line
 */
@Data
public class BaseProdLine {
    /**
     * 产线ID（与销售行 line_id 区分）
     */
    private String prodLineId;

    /**
     * 所属车间
     */
    private String workshopId;

    /**
     * 产线编号
     */
    private String lineCode;

    /**
     * 产线名称，如：1号凸轮轴线
     */
    private String lineName;

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
        BaseProdLine other = (BaseProdLine) that;
        return (this.getProdLineId() == null ? other.getProdLineId() == null : this.getProdLineId().equals(other.getProdLineId()))
            && (this.getWorkshopId() == null ? other.getWorkshopId() == null : this.getWorkshopId().equals(other.getWorkshopId()))
            && (this.getLineCode() == null ? other.getLineCode() == null : this.getLineCode().equals(other.getLineCode()))
            && (this.getLineName() == null ? other.getLineName() == null : this.getLineName().equals(other.getLineName()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getDeleted() == null ? other.getDeleted() == null : this.getDeleted().equals(other.getDeleted()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getProdLineId() == null) ? 0 : getProdLineId().hashCode());
        result = prime * result + ((getWorkshopId() == null) ? 0 : getWorkshopId().hashCode());
        result = prime * result + ((getLineCode() == null) ? 0 : getLineCode().hashCode());
        result = prime * result + ((getLineName() == null) ? 0 : getLineName().hashCode());
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
        sb.append(", prodLineId=").append(prodLineId);
        sb.append(", workshopId=").append(workshopId);
        sb.append(", lineCode=").append(lineCode);
        sb.append(", lineName=").append(lineName);
        sb.append(", status=").append(status);
        sb.append(", deleted=").append(deleted);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append("]");
        return sb.toString();
    }
}