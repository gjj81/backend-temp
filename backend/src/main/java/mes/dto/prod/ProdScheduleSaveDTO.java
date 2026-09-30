package mes.dto.prod;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import java.util.Date;

import static mes.dto.prod.ValidationGroupsProd.ScheduleUpdate;

/**
 * 生产排程-新增/修改请求体
 */
@Data
public class ProdScheduleSaveDTO {

    /** 新增时为空（后端生成），修改时必传 */
    @NotBlank(message = "排产单号不能为空", groups = ScheduleUpdate.class)
    private String scheduleId;

    /** 销售行ID */
    @NotBlank(message = "产品行不能为空")
    private String lineId;

    /** 产线ID */
    @NotBlank(message = "产线不能为空")
    private String prodLineId;

    /** 排产日期 */
    @NotNull(message = "排产日期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date scheduleDate;

    /** 当天排产数量 */
    @NotNull(message = "排产数量不能为空")
    @Min(value = 1, message = "排产数量必须大于0")
    private Integer quantity;

    /** 车间ID（冗余） */
    private String workshopId;

    /** 车间 名称（冗余） */
    private String workshopName;

    /** 产品ID（冗余） */
    private String productId;

    /** 备注 */
    private String remark;

    /** 排产状态 */
    @NotNull(message = "排产状态不能为空")
    private Integer status;

    /** 乐观锁版本号，新增时不传，修改时必传 */
    @NotNull(message = "版本号不能为空", groups = ScheduleUpdate.class)
    private Integer version;
}