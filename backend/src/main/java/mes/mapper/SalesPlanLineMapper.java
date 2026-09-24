package mes.mapper;

import mes.dto.sales.SalesPlanFormLineDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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


}
