package mes.entity;

import java.util.Date;
import lombok.Data;

/**
 * 操作日志
 * @TableName sys_oper_log
 */
@Data
public class SysOperLog {
    /**
     * 
     */
    private String logId;

    /**
     * 模块标题
     */
    private String title;

    /**
     * 操作类型：1-新增 2-删除 3-查询 4-修改 5-其它
     */
    private Integer operType;

    /**
     * 请求方法全路径
     */
    private String method;

    /**
     * 请求url
     */
    private String requestUrl;

    /**
     * 操作人员账号
     */
    private String operUser;

    /**
     * 请求json
     */
    private String jsonParam;

    /**
     * 返回json
     */
    private String jsonResult;

    /**
     * 0-失败 1-成功
     */
    private Integer status;

    /**
     * 操作时间
     */
    private Date operTime;

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
        SysOperLog other = (SysOperLog) that;
        return (this.getLogId() == null ? other.getLogId() == null : this.getLogId().equals(other.getLogId()))
            && (this.getTitle() == null ? other.getTitle() == null : this.getTitle().equals(other.getTitle()))
            && (this.getOperType() == null ? other.getOperType() == null : this.getOperType().equals(other.getOperType()))
            && (this.getMethod() == null ? other.getMethod() == null : this.getMethod().equals(other.getMethod()))
            && (this.getRequestUrl() == null ? other.getRequestUrl() == null : this.getRequestUrl().equals(other.getRequestUrl()))
            && (this.getOperUser() == null ? other.getOperUser() == null : this.getOperUser().equals(other.getOperUser()))
            && (this.getJsonParam() == null ? other.getJsonParam() == null : this.getJsonParam().equals(other.getJsonParam()))
            && (this.getJsonResult() == null ? other.getJsonResult() == null : this.getJsonResult().equals(other.getJsonResult()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getOperTime() == null ? other.getOperTime() == null : this.getOperTime().equals(other.getOperTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getLogId() == null) ? 0 : getLogId().hashCode());
        result = prime * result + ((getTitle() == null) ? 0 : getTitle().hashCode());
        result = prime * result + ((getOperType() == null) ? 0 : getOperType().hashCode());
        result = prime * result + ((getMethod() == null) ? 0 : getMethod().hashCode());
        result = prime * result + ((getRequestUrl() == null) ? 0 : getRequestUrl().hashCode());
        result = prime * result + ((getOperUser() == null) ? 0 : getOperUser().hashCode());
        result = prime * result + ((getJsonParam() == null) ? 0 : getJsonParam().hashCode());
        result = prime * result + ((getJsonResult() == null) ? 0 : getJsonResult().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getOperTime() == null) ? 0 : getOperTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", logId=").append(logId);
        sb.append(", title=").append(title);
        sb.append(", operType=").append(operType);
        sb.append(", method=").append(method);
        sb.append(", requestUrl=").append(requestUrl);
        sb.append(", operUser=").append(operUser);
        sb.append(", jsonParam=").append(jsonParam);
        sb.append(", jsonResult=").append(jsonResult);
        sb.append(", status=").append(status);
        sb.append(", operTime=").append(operTime);
        sb.append("]");
        return sb.toString();
    }
}