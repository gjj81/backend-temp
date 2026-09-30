package mes.mapper;

import mes.entity.BaseWorkshop;
import mes.vo.base.WorkshopVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author g2026
* @description 针对表【base_workshop(车间)】的数据库操作Mapper
* @createDate 2026-09-28 15:11:02
* @Entity mes.entity.BaseWorkshop
*/
@Mapper
public interface BaseWorkshopMapper {

    List<WorkshopVO> list();
}
