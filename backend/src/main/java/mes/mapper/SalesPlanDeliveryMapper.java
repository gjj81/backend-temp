package mes.mapper;

import mes.dto.sales.SalesPlanDeliveryUpdateDTO;
import mes.entity.SalesPlanDelivery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author g2026
* @description 针对表【sales_plan_delivery(交货节点)】的数据库操作Mapper
* @createDate 2026-08-20 09:43:03
* @Entity mes.entity.SalesPlanDelivery
*/
@Mapper
public interface SalesPlanDeliveryMapper {


    int deleteByPlanLineDeliveryId(String planLineDeliveryId, Integer version);

    int deleteByPlanLineDeliveryList(@Param("planLineIds") List<String> planLineIds);

    int batchInsert(@Param("list") List<SalesPlanDeliveryUpdateDTO> salesPlanDeliveryList);

    int batchUpdate(@Param("list") List<SalesPlanDeliveryUpdateDTO> salesPlanDeliveryList);

    int insert(SalesPlanDeliveryUpdateDTO salesPlanDeliveryUpdateDTO);

    int update(SalesPlanDeliveryUpdateDTO salesPlanDeliveryUpdateDTO);

    int updateBatch(@Param("list") List<SalesPlanDeliveryUpdateDTO> dtoList);
}
