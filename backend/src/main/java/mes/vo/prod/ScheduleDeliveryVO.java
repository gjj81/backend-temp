package mes.vo.prod;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.util.Date;

/**
 * 交货节点（列表 chips / 看板复用）
 */
@Data
public class ScheduleDeliveryVO {
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date deliveryDate;
    private Integer planQuantity;
}