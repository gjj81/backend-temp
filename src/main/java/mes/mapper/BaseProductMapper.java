package mes.mapper;

import mes.entity.BaseProduct;

/**
* @author g2026
* @description 针对表【base_product】的数据库操作Mapper
* @createDate 2026-08-20 09:42:48
* @Entity mes.entity.BaseProduct
*/
public interface BaseProductMapper {

    int deleteByPrimaryKey(Long id);

    int insert(BaseProduct record);

    int insertSelective(BaseProduct record);

    BaseProduct selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(BaseProduct record);

    int updateByPrimaryKey(BaseProduct record);

}
