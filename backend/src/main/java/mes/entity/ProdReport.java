package mes.entity;

import java.util.Date;
import lombok.Data;

/**
 * 生产填报（无状态，纯数据记录）
 * @TableName prod_report
 */
@Data
public class ProdReport {
    /**
     * 
     */
    private String reportId;

    /**
     * 关联生产排程
     */
    private String scheduleId;

    /**
     * 1-成品 2-在制品(毛坯)，冗余销售行，铸造/精加工报表区分用
     */
    private Integer orderKind;

    /**
     * 填报归属日期（=排程日期或实际生产日）
     */
    private Date reportDate;

    /**
     * 本批完成数
     */
    private Integer quantity;

    /**
     * 合格数
     */
    private Integer qualifiedQuantity;

    /**
     * 不良数
     */
    private Integer defectQuantity;

    /**
     * 填报人
     */
    private String reporter;

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
        ProdReport other = (ProdReport) that;
        return (this.getReportId() == null ? other.getReportId() == null : this.getReportId().equals(other.getReportId()))
            && (this.getScheduleId() == null ? other.getScheduleId() == null : this.getScheduleId().equals(other.getScheduleId()))
            && (this.getOrderKind() == null ? other.getOrderKind() == null : this.getOrderKind().equals(other.getOrderKind()))
            && (this.getReportDate() == null ? other.getReportDate() == null : this.getReportDate().equals(other.getReportDate()))
            && (this.getQuantity() == null ? other.getQuantity() == null : this.getQuantity().equals(other.getQuantity()))
            && (this.getQualifiedQuantity() == null ? other.getQualifiedQuantity() == null : this.getQualifiedQuantity().equals(other.getQualifiedQuantity()))
            && (this.getDefectQuantity() == null ? other.getDefectQuantity() == null : this.getDefectQuantity().equals(other.getDefectQuantity()))
            && (this.getReporter() == null ? other.getReporter() == null : this.getReporter().equals(other.getReporter()))
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
        result = prime * result + ((getReportId() == null) ? 0 : getReportId().hashCode());
        result = prime * result + ((getScheduleId() == null) ? 0 : getScheduleId().hashCode());
        result = prime * result + ((getOrderKind() == null) ? 0 : getOrderKind().hashCode());
        result = prime * result + ((getReportDate() == null) ? 0 : getReportDate().hashCode());
        result = prime * result + ((getQuantity() == null) ? 0 : getQuantity().hashCode());
        result = prime * result + ((getQualifiedQuantity() == null) ? 0 : getQualifiedQuantity().hashCode());
        result = prime * result + ((getDefectQuantity() == null) ? 0 : getDefectQuantity().hashCode());
        result = prime * result + ((getReporter() == null) ? 0 : getReporter().hashCode());
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
        sb.append(", reportId=").append(reportId);
        sb.append(", scheduleId=").append(scheduleId);
        sb.append(", orderKind=").append(orderKind);
        sb.append(", reportDate=").append(reportDate);
        sb.append(", quantity=").append(quantity);
        sb.append(", qualifiedQuantity=").append(qualifiedQuantity);
        sb.append(", defectQuantity=").append(defectQuantity);
        sb.append(", reporter=").append(reporter);
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