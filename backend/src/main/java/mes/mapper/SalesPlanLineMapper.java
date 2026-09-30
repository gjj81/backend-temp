package mes.mapper;

import mes.dto.sales.SalesPlanFormLineDTO;
import mes.dto.sales.SalesPlanLineStatusDTO;
import mes.vo.prod.ScheduleDeliveryVO;
import mes.vo.prod.ScheduleProductVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
* @author g2026
* @description 针对表【sales_plan_line(计划产品行)】的数据库操作Mapper
* @createDate 2026-08-20 09:43:06
* @Entity mes.entity.SalesPlanLine
*/
@Mapper
public interface SalesPlanLineMapper {

    int deleteByPrimaryKey(String planLineId, Integer version);

    List<String> selectPlanLineIdList(String planId);

    int deleteByPlanId(String planId);

    int updateByPrimaryKey(SalesPlanFormLineDTO salesPlanLineUpdateDTO);

    int batchInsert(@Param("list") List<SalesPlanFormLineDTO> salesPlanLineList);
    int insert(SalesPlanFormLineDTO salesPlanLine);

    // ==================== 排产工作台：月度销售计划产品列表 ====================

    /**
     * 工作台产品列表：销售计划行+计划主表，按最早交货日排序
     */
    List<ScheduleProductVO> selectWorkbenchProducts();


    int batchStatusUpdate(@Param("list") List<SalesPlanLineStatusDTO> lineStatusList,int status);
    int updateStatus(@Param("lineId") String lineId, @Param("status") int status, @Param("version") int version);
}
