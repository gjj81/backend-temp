package mes.mapper;


import mes.dto.sales.SalesPlanFormDTO;
import mes.vo.sales.SalesPlanVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;
import java.util.List;

/**
* @author g2026
* @description 针对表【sales_plan(月度销售计划)】的数据库操作Mapper
* @createDate 2026-08-25 09:28:10
* @Entity mes.entity.SalesPlan
*/
@Mapper
public interface SalesPlanMapper {


    List<SalesPlanVO> selectByPlanMonth(Date planMonth);

    int deleteByPrimaryKey(String planId , Integer version);

    int updateByPrimaryKey(SalesPlanFormDTO salesPlanUpdateDTO);

    int insert(SalesPlanFormDTO salesPlanUpdateDTO);
}
