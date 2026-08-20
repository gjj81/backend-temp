package mes.dto.sales;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class SalesPlanLineDTO {

    @NotBlank(message = "产品不能为空")
    private String productId;

    @NotNull(message = "订单总量不能为空")
    @Min(value = 1, message = "数量必须大于0")
    private Integer totalQuantity;

    private Integer forecastQuantity;

    private Integer openingInventory;

    private List<SalesPlanDeliveryDTO> deliveryList;
}