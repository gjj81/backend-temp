package mes.common.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页响应数据封装
 * @param <T> 数据类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {

    /** 数据列表 */
    private List<T> list;

    /** 总记录数 */
    private Long total;

    /** 当前页码（从1开始） */
    private Long pageNum;

    /** 每页大小 */
    private Long pageSize;

    /** 总页数 */
    private Long pages;

    /** 是否有下一页 */
    private Boolean hasNextPage;

    /** 是否有上一页 */
    private Boolean hasPreviousPage;

    public PageResponse(List<T> list, Long total, Long pageNum, Long pageSize) {
        this.list = list;
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.pages = total == 0 ? 0 : (total + pageSize - 1) / pageSize;
        this.hasNextPage = pageNum < this.pages;
        this.hasPreviousPage = pageNum > 1;
    }
}