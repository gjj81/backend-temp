package mes.dto.sales;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Data
public class SalesPlanDeliveryUpdateDTO {

    /**
     * 交货节点ID
     * - 新增时：允许为空（后端自动生成）→ 不属于 DeliveryAdd 组
     * - 更新时：不能为空 → 属于 DeliveryUpdate 组
     */
    @NotBlank(message = "更新时交货节点ID不能为空", groups = ValidationGroups.DeliveryUpdate.class)
    private String deliveryId;

    /**
     * 计划行ID
     * - 新增和更新时都不能为空
     */
    @NotBlank(message = "计划行ID不能为空", groups = {
            ValidationGroups.DeliveryAdd.class,
            ValidationGroups.DeliveryUpdate.class
    })
    private String lineId;

    private String nodeName;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date deliveryDate;

    private Integer planQuantity;
    private Integer actualQuantity;
    private Integer status;

    /**
     * 版本号
     * - 新增时可以为0或null（新记录）
     * - 更新时必须提供（乐观锁）
     */
    @NotNull(message = "更新时版本号不能为空", groups = ValidationGroups.DeliveryUpdate.class)
    private Integer version;
}
