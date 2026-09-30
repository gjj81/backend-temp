package mes.vo.prod;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.util.Date;

/**
 * 生产排程视图（单表，按天查询）
 */
@Data
public class ProdScheduleVO {
    private String scheduleId;
    private String lineId;
    private String prodLineId;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date scheduleDate;
    private Integer quantity;
    private String workshopId;
    private String workshopName;
    private String productId;
    private Integer actualQuantity;
    private Integer qualifiedQuantity;
    private Integer defectQuantity;
    /** 0待确认 1已确认 2生产中 3已完工 */
    private Integer status;
    private String remark;
    private Integer version;
}