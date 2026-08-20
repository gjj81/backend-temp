package mes.dto.sales;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class SalesPlanDTO {

    @NotBlank(message = "客户不能为空")
    private String customer;

    @NotBlank(message = "年月不能为空")
    private String yearMonth;

    @NotNull(message = "计划类型不能为空")
    private Integer planType;

    private String remark;

    @Valid  // 嵌套校验lineList里的每个对象
    private List<SalesPlanLineDTO> lineList;
}