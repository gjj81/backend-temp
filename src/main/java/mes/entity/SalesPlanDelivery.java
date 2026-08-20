package mes.entity;

import java.util.Date;
import lombok.Data;

/**
 * 交货节点
 * @TableName sales_plan_delivery
 */
@Data
public class SalesPlanDelivery {
    /**
     * 
     */
    private String deliveryId;

    /**
     * 关联计划行
     */
    private String lineId;

    /**
     * 节点名称，如18号前
     */
    private String nodeName;

    /**
     * 
     */
    private Date deliveryDate;

    /**
     * 计划交货数量
     */
    private Integer planQuantity;

    /**
     * 实际交货数量（预留）
     */
    private Integer actualQuantity;

    /**
     * 0待交货 1已确认 2已发货
     */
    private Integer status;

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
        SalesPlanDelivery other = (SalesPlanDelivery) that;
        return (this.getDeliveryId() == null ? other.getDeliveryId() == null : this.getDeliveryId().equals(other.getDeliveryId()))
            && (this.getLineId() == null ? other.getLineId() == null : this.getLineId().equals(other.getLineId()))
            && (this.getNodeName() == null ? other.getNodeName() == null : this.getNodeName().equals(other.getNodeName()))
            && (this.getDeliveryDate() == null ? other.getDeliveryDate() == null : this.getDeliveryDate().equals(other.getDeliveryDate()))
            && (this.getPlanQuantity() == null ? other.getPlanQuantity() == null : this.getPlanQuantity().equals(other.getPlanQuantity()))
            && (this.getActualQuantity() == null ? other.getActualQuantity() == null : this.getActualQuantity().equals(other.getActualQuantity()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
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
        result = prime * result + ((getDeliveryId() == null) ? 0 : getDeliveryId().hashCode());
        result = prime * result + ((getLineId() == null) ? 0 : getLineId().hashCode());
        result = prime * result + ((getNodeName() == null) ? 0 : getNodeName().hashCode());
        result = prime * result + ((getDeliveryDate() == null) ? 0 : getDeliveryDate().hashCode());
        result = prime * result + ((getPlanQuantity() == null) ? 0 : getPlanQuantity().hashCode());
        result = prime * result + ((getActualQuantity() == null) ? 0 : getActualQuantity().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
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
        sb.append(", deliveryId=").append(deliveryId);
        sb.append(", lineId=").append(lineId);
        sb.append(", nodeName=").append(nodeName);
        sb.append(", deliveryDate=").append(deliveryDate);
        sb.append(", planQuantity=").append(planQuantity);
        sb.append(", actualQuantity=").append(actualQuantity);
        sb.append(", status=").append(status);
        sb.append(", version=").append(version);
        sb.append(", createBy=").append(createBy);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateBy=").append(updateBy);
        sb.append(", updateTime=").append(updateTime);
        sb.append("]");
        return sb.toString();
    }
}