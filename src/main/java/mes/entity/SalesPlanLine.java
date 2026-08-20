package mes.entity;

import java.util.Date;
import lombok.Data;

/**
 * 计划产品行
 * @TableName sales_plan_line
 */
@Data
public class SalesPlanLine {
    /**
     * 
     */
    private String lineId;

    /**
     * 关联计划主表
     */
    private String planId;

    /**
     * 产品（可毛坯可成品）
     */
    private String productId;

    /**
     * 
     */
    private String productName;

    /**
     * 订单总量
     */
    private Integer totalQuantity;

    /**
     * 次月预测
     */
    private Integer forecastQuantity;

    /**
     * 期初库存快照
     */
    private Integer openingInventory;

    /**
     * 已排产量（排产后累加）
     */
    private Integer scheduledQuantity;

    /**
     * 已完工量（预留）
     */
    private Integer finishedQuantity;

    /**
     * 已发货量（预留）
     */
    private Integer deliveredQuantity;

    /**
     * 0待排程 1已排程 2生产中 3已完成
     */
    private Integer status;

    /**
     * 
     */
    private Integer deleted;

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
        SalesPlanLine other = (SalesPlanLine) that;
        return (this.getLineId() == null ? other.getLineId() == null : this.getLineId().equals(other.getLineId()))
            && (this.getPlanId() == null ? other.getPlanId() == null : this.getPlanId().equals(other.getPlanId()))
            && (this.getProductId() == null ? other.getProductId() == null : this.getProductId().equals(other.getProductId()))
            && (this.getProductName() == null ? other.getProductName() == null : this.getProductName().equals(other.getProductName()))
            && (this.getTotalQuantity() == null ? other.getTotalQuantity() == null : this.getTotalQuantity().equals(other.getTotalQuantity()))
            && (this.getForecastQuantity() == null ? other.getForecastQuantity() == null : this.getForecastQuantity().equals(other.getForecastQuantity()))
            && (this.getOpeningInventory() == null ? other.getOpeningInventory() == null : this.getOpeningInventory().equals(other.getOpeningInventory()))
            && (this.getScheduledQuantity() == null ? other.getScheduledQuantity() == null : this.getScheduledQuantity().equals(other.getScheduledQuantity()))
            && (this.getFinishedQuantity() == null ? other.getFinishedQuantity() == null : this.getFinishedQuantity().equals(other.getFinishedQuantity()))
            && (this.getDeliveredQuantity() == null ? other.getDeliveredQuantity() == null : this.getDeliveredQuantity().equals(other.getDeliveredQuantity()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getDeleted() == null ? other.getDeleted() == null : this.getDeleted().equals(other.getDeleted()))
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
        result = prime * result + ((getLineId() == null) ? 0 : getLineId().hashCode());
        result = prime * result + ((getPlanId() == null) ? 0 : getPlanId().hashCode());
        result = prime * result + ((getProductId() == null) ? 0 : getProductId().hashCode());
        result = prime * result + ((getProductName() == null) ? 0 : getProductName().hashCode());
        result = prime * result + ((getTotalQuantity() == null) ? 0 : getTotalQuantity().hashCode());
        result = prime * result + ((getForecastQuantity() == null) ? 0 : getForecastQuantity().hashCode());
        result = prime * result + ((getOpeningInventory() == null) ? 0 : getOpeningInventory().hashCode());
        result = prime * result + ((getScheduledQuantity() == null) ? 0 : getScheduledQuantity().hashCode());
        result = prime * result + ((getFinishedQuantity() == null) ? 0 : getFinishedQuantity().hashCode());
        result = prime * result + ((getDeliveredQuantity() == null) ? 0 : getDeliveredQuantity().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getDeleted() == null) ? 0 : getDeleted().hashCode());
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
        sb.append(", lineId=").append(lineId);
        sb.append(", planId=").append(planId);
        sb.append(", productId=").append(productId);
        sb.append(", productName=").append(productName);
        sb.append(", totalQuantity=").append(totalQuantity);
        sb.append(", forecastQuantity=").append(forecastQuantity);
        sb.append(", openingInventory=").append(openingInventory);
        sb.append(", scheduledQuantity=").append(scheduledQuantity);
        sb.append(", finishedQuantity=").append(finishedQuantity);
        sb.append(", deliveredQuantity=").append(deliveredQuantity);
        sb.append(", status=").append(status);
        sb.append(", deleted=").append(deleted);
        sb.append(", version=").append(version);
        sb.append(", createBy=").append(createBy);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateBy=").append(updateBy);
        sb.append(", updateTime=").append(updateTime);
        sb.append("]");
        return sb.toString();
    }
}