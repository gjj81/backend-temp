package mes.dto.sales;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SalesPlanLineStatusDTO {
    @NotBlank(message = "计划行ID不能为空")
    private String lineId;
    @NotNull(message = "状态不能为空",groups = ValidationGroups.PlanUpdate.class)
    private Integer status;
    @NotNull(message = "版本不能为空", groups = ValidationGroups.PlanUpdate.class)
    private Integer version;
}
