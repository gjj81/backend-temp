package mes.mapper;

import mes.dto.prod.ProdScheduleSaveDTO;
import mes.entity.ProdSchedule;
import mes.vo.prod.ProdScheduleVO;
import mes.vo.prod.ScheduleProductVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
* @author g2026
* @description 针对表【prod_schedule(生产排程（产品行×产线 多对多，按天排产）)】的数据库操作Mapper
* @createDate 2026-09-28 15:11:12
* @Entity mes.entity.ProdSchedule
*/
@Mapper
public interface ProdScheduleMapper {


    List<ProdScheduleVO> selectProdProducts(Date startDate, Date endDate);

    int insertList(@Param("list") List<ProdSchedule> dtoList);

    int updateBatch(@Param("list") List<ProdSchedule> dtoList);

    int deleteByIdAndVersion(String id, Integer version);
}
