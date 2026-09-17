package mes.dto.sales;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class SalesPlanLineUpdateDTO {

    /**
     * 行ID
     * - 新增时：允许为空（后端自动生成）→ 不属于 LineAdd 组
     * - 更新时：不能为空 → 属于 LineUpdate 组
     */
    @NotBlank(message = "更新时行ID不能为空", groups = ValidationGroups.LineUpdate.class)
    private String lineId;

    /**
     * 计划ID
     * - 新增和更新时都不能为空（必须关联到某个计划）
     */
    @NotBlank(message = "计划ID不能为空", groups = {
            ValidationGroups.LineAdd.class,
            ValidationGroups.LineUpdate.class
    })
    private String planId;

    private String productId;
    private String productName;
    private Integer totalQuantity;
    private Integer forecastQuantity;
    private Integer openingInventory;
    private Integer scheduledQuantity;
    private Integer finishedQuantity;
    private Integer deliveredQuantity;
    private Integer status;

    /**
     * 版本号
     * - 新增时可以为0或null
     * - 更新时必须提供（乐观锁）
     */
    @NotNull(message = "更新时版本号不能为空", groups = ValidationGroups.LineUpdate.class)
    private Integer version;

    private List<SalesPlanDeliveryUpdateDTO> deliveries;
}
