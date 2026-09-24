package mes.dto.sales;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SalesPlanLineStatusDTO {
    @NotBlank(message = "计划行ID不能为空")
    private String lineId;
    @NotNull(message = "版本号不能为空")
    private Integer version;
    @NotNull(message = "状态不能为空")
    private Integer status;
}
