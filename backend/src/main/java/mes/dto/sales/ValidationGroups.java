package mes.dto.sales;

/**
 * 校验组接口
 *
 * 设计原则：
 *   - Add组：新增时的校验规则（ID允许为空，由后端生成）
 *   - Update组：更新时的校验规则（ID不能为空，用于定位记录和乐观锁）
 */
public interface ValidationGroups {

    /** 计划相关校验组 */
    interface PlanAdd {      // 新增计划
    }
    interface PlanUpdate {   // 更新计划
    }

    /** 计划行相关校验组 */
    interface LineAdd {      // 新增计划行
    }
    interface LineUpdate {   // 更新计划行
    }

    /** 交货节点相关校验组 */
    interface DeliveryAdd {       // 新增交货节点（ID由后端生成，允许为空）
    }
    interface DeliveryUpdate {    // 更新交货节点（ID必须提供，用于定位记录）
    }
}
