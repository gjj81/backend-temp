package mes.dto.sales;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SalesPlanUpdateDTO {

    @NotBlank(message = "计划ID不能为空")
    private String planId;

    @NotNull(message = "版本号不能为空")
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

    private List<SalesPlanLineUpdateDTO> lines;
}