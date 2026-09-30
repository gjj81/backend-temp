package mes.vo.prod;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 排产工作台-月度销售计划产品列表（纯销售数据展示，供排产/看板联动）
 */
@Data
public class ScheduleProductVO {
    /** 销售行ID：排产、看板联动主键 */
    private String lineId;
    /** 计划编号：展示/追溯 */
    private String planNo;
    /** 客户 */
    private String customer;
    private String productId;
    /** 产品名 */
    private String productName;
    /** 订单总量 */
    private Integer totalQuantity;
    /** 计划日期 */
    @JsonFormat(pattern = "yyyy-MM")
    private Date planMonth;
    /** 次月预测 */
    private Integer forecastQuantity;
    /** 期初库存快照 */
    private Integer openingInventory;
    /** 行状态：0待排程 1已排程 2生产中 3已完成 */
    private Integer lineStatus;
    /** 乐观锁：后续状态流转/看板提交必传 */
    private Integer version;
    /** 已排产数量 */
    private Integer scheduledQuantity;
    /** 交货节点 */
    private List<ScheduleDeliveryVO> deliveryNodes;
}