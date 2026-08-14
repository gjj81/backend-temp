package mes.entity;

import lombok.Data;

/**
 * 系统参数
 * @TableName sys_config
 */
@Data
public class SysConfig {
    /**
     * 
     */
    private String configId;

    /**
     * 参数键名
     */
    private String configKey;

    /**
     * 
     */
    private String configValue;

    /**
     * 
     */
    private String configLabel;

    /**
     * 类型 1-文本 2-数字 3-布尔 4-json
     */
    private Integer configType;

    /**
     * 状态：0-禁用 1-正常
     */
    private Integer status;

    /**
     * 
     */
    private Integer deleted;

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
        SysConfig other = (SysConfig) that;
        return (this.getConfigId() == null ? other.getConfigId() == null : this.getConfigId().equals(other.getConfigId()))
            && (this.getConfigKey() == null ? other.getConfigKey() == null : this.getConfigKey().equals(other.getConfigKey()))
            && (this.getConfigValue() == null ? other.getConfigValue() == null : this.getConfigValue().equals(other.getConfigValue()))
            && (this.getConfigLabel() == null ? other.getConfigLabel() == null : this.getConfigLabel().equals(other.getConfigLabel()))
            && (this.getConfigType() == null ? other.getConfigType() == null : this.getConfigType().equals(other.getConfigType()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getDeleted() == null ? other.getDeleted() == null : this.getDeleted().equals(other.getDeleted()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getConfigId() == null) ? 0 : getConfigId().hashCode());
        result = prime * result + ((getConfigKey() == null) ? 0 : getConfigKey().hashCode());
        result = prime * result + ((getConfigValue() == null) ? 0 : getConfigValue().hashCode());
        result = prime * result + ((getConfigLabel() == null) ? 0 : getConfigLabel().hashCode());
        result = prime * result + ((getConfigType() == null) ? 0 : getConfigType().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getDeleted() == null) ? 0 : getDeleted().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", configId=").append(configId);
        sb.append(", configKey=").append(configKey);
        sb.append(", configValue=").append(configValue);
        sb.append(", configLabel=").append(configLabel);
        sb.append(", configType=").append(configType);
        sb.append(", status=").append(status);
        sb.append(", deleted=").append(deleted);
        sb.append("]");
        return sb.toString();
    }
}