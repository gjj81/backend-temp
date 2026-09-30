package mes.controller;

import mes.common.result.Result;
import mes.service.BaseService;
import mes.vo.base.ProdLineVO;
import mes.vo.base.WorkshopVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/base")
public class BaseController {
    private final BaseService baseService;

    public BaseController(BaseService baseService) {
        this.baseService = baseService;
    }
    @GetMapping("/workshop/list")
    public Result<List<WorkshopVO>> listWorkshops() {
        // 实现获取车间列表的逻辑
        return baseService.listWorkshops();
    }
    @GetMapping("/prodLine/list")
    public Result<List<ProdLineVO>> listProdLines() {
        // 实现获取产线列表的逻辑
        return baseService.listProdLines();
    }

}
