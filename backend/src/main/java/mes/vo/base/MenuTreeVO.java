package mes.vo.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)// 忽略值为null的字段
public class MenuTreeVO {
    private String menuId;

    private String parentId;

    private String menuName;

    private Integer menuType;

    private String icon;

    private String path;

    private String component;

    private String query;

    private String perms;

    private Integer isCache;

    private Integer visible;

    private Integer sort;

    private List<MenuTreeVO> children = new ArrayList<>();
}
