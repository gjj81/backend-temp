package mes.vo.sales;

import lombok.Data;
import java.util.Date;
import java.util.List;

/**
 * 销售计划 VO（第一层）
 */
@Data
public class SalesPlanVO {
    private String planId;
    private String planNo;
    private String customer;
    private Date planMonth;
    private Integer planType;
    private Integer status;
    private String remark;
    private Integer version;

    /** 第二层：计划产品行列表 */
    private List<SalesPlanLineVO> lines;
}