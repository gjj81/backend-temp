package mes.entity;

import lombok.Data;

/**
 * 字典数据
 * @TableName sys_dict_data
 */
@Data
public class SysDictData {
    /**
     * 
     */
    private String dictDataId;

    /**
     * 编码
     */
    private String dictType;

    /**
     * 显示标签
     */
    private String dictLabel;

    /**
     * 存储值
     */
    private String dictValue;

    /**
     * 样式
     */
    private String cssClass;

    /**
     * 是否默认：0-否 1-是
     */
    private Integer isDefault;

    /**
     * 0-禁用 1-正常
     */
    private Integer status;

    /**
     * 0-未删除 其余删除
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
        SysDictData other = (SysDictData) that;
        return (this.getDictDataId() == null ? other.getDictDataId() == null : this.getDictDataId().equals(other.getDictDataId()))
            && (this.getDictType() == null ? other.getDictType() == null : this.getDictType().equals(other.getDictType()))
            && (this.getDictLabel() == null ? other.getDictLabel() == null : this.getDictLabel().equals(other.getDictLabel()))
            && (this.getDictValue() == null ? other.getDictValue() == null : this.getDictValue().equals(other.getDictValue()))
            && (this.getCssClass() == null ? other.getCssClass() == null : this.getCssClass().equals(other.getCssClass()))
            && (this.getIsDefault() == null ? other.getIsDefault() == null : this.getIsDefault().equals(other.getIsDefault()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getDeleted() == null ? other.getDeleted() == null : this.getDeleted().equals(other.getDeleted()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getDictDataId() == null) ? 0 : getDictDataId().hashCode());
        result = prime * result + ((getDictType() == null) ? 0 : getDictType().hashCode());
        result = prime * result + ((getDictLabel() == null) ? 0 : getDictLabel().hashCode());
        result = prime * result + ((getDictValue() == null) ? 0 : getDictValue().hashCode());
        result = prime * result + ((getCssClass() == null) ? 0 : getCssClass().hashCode());
        result = prime * result + ((getIsDefault() == null) ? 0 : getIsDefault().hashCode());
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
        sb.append(", dictDataId=").append(dictDataId);
        sb.append(", dictType=").append(dictType);
        sb.append(", dictLabel=").append(dictLabel);
        sb.append(", dictValue=").append(dictValue);
        sb.append(", cssClass=").append(cssClass);
        sb.append(", isDefault=").append(isDefault);
        sb.append(", status=").append(status);
        sb.append(", deleted=").append(deleted);
        sb.append("]");
        return sb.toString();
    }
}