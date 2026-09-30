package mes.dto.sales;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class SalesPlanStatusDTO {
    @NotBlank(message = "计划ID不能为空")
    private String planId;
    @NotNull(message = "状态不能为空")
    private Integer status;
    @NotNull(message = "版本不能为空")
    private Integer version;
    private List<SalesPlanLineStatusDTO> lineStatusList;

}
