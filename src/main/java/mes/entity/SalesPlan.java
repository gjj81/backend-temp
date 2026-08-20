package mes.entity;

import java.util.Date;
import lombok.Data;

/**
 * 月度销售计划
 * @TableName sales_plan
 */
@Data
public class SalesPlan {
    /**
     * 
     */
    private String planId;

    /**
     * 计划编号
     */
    private String planNo;

    /**
     * 客户
     */
    private String customer;

    /**
     * 计划归属月（存当月1日）
     */
    private Date planMonth;

    /**
     * 0正式订单 1滚动预测
     */
    private Integer planType;

    /**
     * 0待确认 1已确认 2已下发
     */
    private Integer status;

    /**
     * 
     */
    private String remark;

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
        SalesPlan other = (SalesPlan) that;
        return (this.getPlanId() == null ? other.getPlanId() == null : this.getPlanId().equals(other.getPlanId()))
            && (this.getPlanNo() == null ? other.getPlanNo() == null : this.getPlanNo().equals(other.getPlanNo()))
            && (this.getCustomer() == null ? other.getCustomer() == null : this.getCustomer().equals(other.getCustomer()))
            && (this.getPlanMonth() == null ? other.getPlanMonth() == null : this.getPlanMonth().equals(other.getPlanMonth()))
            && (this.getPlanType() == null ? other.getPlanType() == null : this.getPlanType().equals(other.getPlanType()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getRemark() == null ? other.getRemark() == null : this.getRemark().equals(other.getRemark()))
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
        result = prime * result + ((getPlanId() == null) ? 0 : getPlanId().hashCode());
        result = prime * result + ((getPlanNo() == null) ? 0 : getPlanNo().hashCode());
        result = prime * result + ((getCustomer() == null) ? 0 : getCustomer().hashCode());
        result = prime * result + ((getPlanMonth() == null) ? 0 : getPlanMonth().hashCode());
        result = prime * result + ((getPlanType() == null) ? 0 : getPlanType().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getRemark() == null) ? 0 : getRemark().hashCode());
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
        sb.append(", planId=").append(planId);
        sb.append(", planNo=").append(planNo);
        sb.append(", customer=").append(customer);
        sb.append(", planMonth=").append(planMonth);
        sb.append(", planType=").append(planType);
        sb.append(", status=").append(status);
        sb.append(", remark=").append(remark);
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