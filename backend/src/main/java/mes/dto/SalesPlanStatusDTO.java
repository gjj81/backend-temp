package mes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SalesPlanStatusDTO {

    @NotBlank(message = "计划ID不能为空")
    private String planId;

    @NotNull(message = "版本号不能为空")
    private Integer version;
}