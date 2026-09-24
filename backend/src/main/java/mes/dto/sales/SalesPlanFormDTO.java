package mes.dto.sales;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SalesPlanFormDTO {

    /**
     * 计划ID
     * - 新增时：允许为空（后端自动生成）→ 不属于 PlanAdd 组
     * - 更新时：不能为空 → 属于 PlanUpdate 组
     */
    @NotBlank(message = "更新时计划ID不能为空", groups = ValidationGroups.PlanUpdate.class)
    private String planId;

    /**
     * 版本号
     * - 新增时可以为0或null
     * - 更新时必须提供（乐观锁）
     */
    @NotNull(message = "更新时版本号不能为空", groups = ValidationGroups.PlanUpdate.class)
    private Integer version;

    @Size(max = 32, message = "计划编号长度不能超过32")
    private String planNo;

    @Size(max = 128, message = "客户名称长度不能超过128")
    private String customer;

    @JsonFormat(pattern = "yyyy-MM", timezone = "GMT+8")
    private Date planMonth;

    private Integer planType;

    private Integer status;

    @Size(max = 500, message = "备注长度不能超过500")
    private String remark;

    private List<SalesPlanFormLineDTO> lines;
}
