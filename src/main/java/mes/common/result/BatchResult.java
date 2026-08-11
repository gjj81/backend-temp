package mes.common.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 批量操作结果（部分成功、部分失败场景）
 * @param <T> 成功数据类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchResult<T> {

    /** 是否全部成功 */
    private Boolean allSuccess;

    /** 成功数量 */
    private Integer successCount;

    /** 失败数量 */
    private Integer failCount;

    /** 成功数据列表 */
    private List<T> successList;

    /** 失败明细 */
    private List<FailItem> failList;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FailItem {
        /** 标识（如行号、ID、订单号） */
        private String id;
        /** 失败原因 */
        private String reason;
    }

    public static <T> BatchResult<T> empty() {
        return new BatchResult<>(true, 0, 0, null, null);
    }
}