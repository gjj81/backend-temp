package mes.mapper;

import mes.entity.ProdSchedule;

/**
* @author g2026
* @description 针对表【prod_schedule(生产排程)】的数据库操作Mapper
* @createDate 2026-08-20 09:42:53
* @Entity mes.entity.ProdSchedule
*/
public interface ProdScheduleMapper {

    int deleteByPrimaryKey(Long id);

    int insert(ProdSchedule record);

    int insertSelective(ProdSchedule record);

    ProdSchedule selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ProdSchedule record);

    int updateByPrimaryKey(ProdSchedule record);

}
