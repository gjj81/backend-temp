package mes.mapper;

import mes.entity.BaseProdLine;
import mes.vo.base.ProdLineVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author g2026
* @description 针对表【base_prod_line(产线)】的数据库操作Mapper
* @createDate 2026-09-28 15:10:39
* @Entity mes.entity.BaseProdLine
*/
@Mapper
public interface BaseProdLineMapper {

    List<ProdLineVO> list();
}
