package mes.dto.sales;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class SalesPlanLineUpdateDTO {

    @NotBlank(message = "行ID不能为空", groups = ValidationGroups.StandaloneUpdate.class)
    private String lineId;
    @NotBlank(message = "计划ID不能为空", groups = ValidationGroups.StandaloneUpdate.class)
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

    @NotNull(message = "版本号不能为空", groups = ValidationGroups.StandaloneUpdate.class)
    private Integer version;

    private List<SalesPlanDeliveryUpdateDTO> deliveries;
}