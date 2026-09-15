package mes.vo.sales;

import lombok.Data;
import java.util.Date;

/**
 * 交货节点 VO（第三层）
 */
@Data
public class SalesPlanDeliveryVO {
    private String deliveryId;
    private String nodeName;
    private Date deliveryDate;
    private Integer planQuantity;
    private Integer actualQuantity;
    private Integer status;
    private Integer version;
}