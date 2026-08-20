package mes.dto.sales;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SalesPlanDeliveryDTO {

    @NotBlank(message = "节点名称不能为空")
    private String nodeName;

    @NotBlank(message = "交货日期不能为空")
    private String deliveryDate;  // 前端传字符串，后端转LocalDate

    @NotNull(message = "数量不能为空")
    private Integer planQuantity;
}