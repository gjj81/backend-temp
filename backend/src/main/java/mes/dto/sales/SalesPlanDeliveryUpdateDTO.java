package mes.dto.sales;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Data
public class SalesPlanDeliveryUpdateDTO {

    @NotBlank(message = "交货节点ID不能为空", groups = ValidationGroups.StandaloneCreate.class)
    private String deliveryId;

    @NotBlank(message = "计划行ID不能为空", groups = ValidationGroups.StandaloneCreate.class)
    private String lineId;

    private String nodeName;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date deliveryDate;

    private Integer planQuantity;
    private Integer actualQuantity;
    private Integer status;

    @NotNull(message = "版本号不能为空", groups = ValidationGroups.StandaloneUpdate.class)
    private Integer version;
}