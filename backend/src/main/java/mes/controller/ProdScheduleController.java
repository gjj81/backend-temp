package mes.controller;

import mes.common.result.Result;
import mes.dto.prod.ProdScheduleSaveDTO;
import mes.dto.prod.ValidationGroupsProd;
import mes.service.ProdScheduleService;
import mes.vo.prod.ProdScheduleVO;
import mes.vo.prod.ScheduleProductVO;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 生产排产
 */
@RestController
@RequestMapping("/api/prod-schedule")
public class ProdScheduleController {

    private final ProdScheduleService prodScheduleService;

    public ProdScheduleController(ProdScheduleService prodScheduleService) {
        this.prodScheduleService = prodScheduleService;
    }

    // ==================== 查询接口 ====================

    /**
     * 排产工作台-月度销售计划产品列表（纯销售数据，供排产联动）
     * GET /api/prod-schedule/workbench/list?month=2026-05&customer=奇瑞&planStatus=
     * 说明：
     *   - 只查销售侧数据（sales_plan / sales_plan_line / sales_plan_delivery），不关联 prod_schedule；
     *   - 已排/剩余等由前端计算；
     *   - planStatus 仅给"草稿/未下发"角标精确过滤，不传默认查 状态<=已下发(2) 的全部。
     */
    @GetMapping("/workbench/list")
    public Result<List<ScheduleProductVO>> listWorkbenchProducts() {
        return prodScheduleService.listWorkbenchProducts();
    }

    @GetMapping("/prod/list")
    public Result<List<ProdScheduleVO>> listProdProducts(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate ,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate ) {
        return prodScheduleService.listProdProducts(startDate, endDate);
    }

    /** 新增排产 */
    @PostMapping("/prod/add")
    public Result<Void> addSchedule(@RequestBody @Validated List<ProdScheduleSaveDTO> dtoList) {
        return prodScheduleService.saveSchedule(dtoList);
    }

    /** 修改排产 */
    @PutMapping("/prod/update")
    public Result<Void> updateSchedule(@RequestBody @Validated(ValidationGroupsProd.ScheduleUpdate.class) List<ProdScheduleSaveDTO> dtoList) {
        return prodScheduleService.updateSchedule(dtoList);
    }

    @DeleteMapping("/prod/delete")
    public Result<Void> deleteSchedule(@RequestParam String id, @RequestParam Integer version) {
        return prodScheduleService.deleteSchedule(id,version);
    }
}