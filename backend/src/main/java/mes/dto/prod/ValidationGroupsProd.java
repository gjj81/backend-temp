package mes.dto.prod;

/**
 * 校验组接口-排产模块
 *
 * 设计原则：
 *   - Add组：新增时的校验规则（ID允许为空，由后端生成）
 *   - Update组：更新时的校验规则（ID不能为空，用于定位记录和乐观锁）
 */
public interface ValidationGroupsProd {

    /** 排程-新增校验组 */
    interface ScheduleAdd {
    }

    /** 排程-更新校验组 */
    interface ScheduleUpdate {
    }
}