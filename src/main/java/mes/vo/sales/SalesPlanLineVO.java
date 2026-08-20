package mes.vo.sales;

import lombok.Data;
import java.util.List;

/**
 * 计划产品行 VO（第二层）
 */
@Data
public class SalesPlanLineVO {
    private String lineId;
    private String productId;
    private String productName;
    private Integer totalQuantity;
    private Integer forecastQuantity;
    private Integer openingInventory;
    private Integer scheduledQuantity;
    private Integer finishedQuantity;
    private Integer deliveredQuantity;
    private Integer status;
    private Integer version;

    /** 第三层：交货节点列表 */
    private List<SalesPlanDeliveryVO> deliveries;
}