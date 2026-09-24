package mes.controller;

import mes.common.result.Result;
import mes.dto.SalesPlanStatusDTO;
import mes.dto.sales.*;
import mes.service.SalesPlanDeliveryService;
import mes.service.SalesPlanLineService;
import mes.service.SalesPlanService;
import mes.vo.sales.SalesPlanVO;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/sales-plan")
public class SalesPlanController {

    private final SalesPlanService salesPlanService;
    private final SalesPlanLineService salesPlanLineService;
    private final SalesPlanDeliveryService salesPlanDeliveryService;

    public SalesPlanController(
            SalesPlanService salesPlanService,
            SalesPlanLineService salesPlanLineService,
            SalesPlanDeliveryService salesPlanDeliveryService) {
        this.salesPlanService = salesPlanService;
        this.salesPlanLineService = salesPlanLineService;
        this.salesPlanDeliveryService = salesPlanDeliveryService;
    }

    // ==================== 查询接口 ====================

    @GetMapping("/monthly")
    public Result<List<SalesPlanVO>> listMonthly(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") Date yearMonth) {
        return salesPlanService.listMonthly(yearMonth);
    }

    // ==================== 删除接口 ====================

    @GetMapping("/delete/plan/{planId}/{version}")
    public Result<String> deletePlan(@PathVariable String planId, @PathVariable Integer version) {
        return salesPlanService.delete(planId, version);
    }

    @GetMapping("/delete/line/{planLineId}/{version}")
    public Result<String> deletePlanLine(@PathVariable String planLineId, @PathVariable Integer version) {
        return salesPlanLineService.delete(planLineId, version);
    }

    @GetMapping("/delete/delivery/{planDeliveryId}/{version}")
    public Result<String> deletePlanDelivery(@PathVariable String planDeliveryId, @PathVariable Integer version) {
        return salesPlanDeliveryService.delete(planDeliveryId, version);
    }

    // ==================== 新增接口（使用 Add 校验组）====================

    /**
     * 新增交货节点
     * 使用 DeliveryAdd 组：
     *   - lineId: 必填 ✅
     *   - deliveryId: 允许为空 ✅（后端生成）
     *   - version: 允许为空 ✅
     */
    @PostMapping("/add/delivery")
    public Result<String> addPlanDelivery(
            @Validated(ValidationGroups.DeliveryAdd.class)
            @RequestBody SalesPlanFormDeliveryDTO dto) {
        return salesPlanDeliveryService.insert(dto);
    }

    /**
     * 新增计划行
     * 使用 LineAdd 组：
     *   - planId: 必填 ✅
     *   - lineId: 允许为空 ✅（后端生成）
     *   - version: 允许为空 ✅
     */
    @PostMapping("/add/line")
    public Result<String> addPlanLine(
            @Validated(ValidationGroups.LineAdd.class)
            @RequestBody SalesPlanFormLineDTO dto) {
        return salesPlanLineService.insert(dto);
    }

    /**
     * 新增计划
     * 使用 PlanAdd 组：
     *   - planId: 允许为空 ✅（后端生成）
     *   - version: 允许为空 ✅
     *   - 其他字段按各自约束校验
     */
    @PostMapping("/add/plan")
    public Result<String> addPlan(
            @Validated(ValidationGroups.PlanAdd.class)
            @RequestBody SalesPlanFormDTO dto) {
        return salesPlanService.insert(dto);
    }

    // ==================== 更新接口（使用 Update 校验组）====================

    /**
     * 更新计划
     * 使用 PlanUpdate 组：
     *   - planId: 必填 ✅（用于定位记录）
     *   - version: 必填 ✅（乐观锁）
     */
    @PostMapping("/update/plan")
    public Result<String> updatePlan(
            @Validated(ValidationGroups.PlanUpdate.class)
            @RequestBody SalesPlanFormDTO dto) {
        return salesPlanService.update(dto);
    }

    /**
     * 更新计划行
     * 使用 LineUpdate 组：
     *   - lineId: 必填 ✅（用于定位记录）
     *   - planId: 必填 ✅
     *   - version: 必填 ✅（乐观锁）
     */
    @PostMapping("/update/line")
    public Result<String> updatePlanLine(
            @Validated(ValidationGroups.LineUpdate.class)
            @RequestBody SalesPlanFormLineDTO dto) {
        return salesPlanLineService.update(dto);
    }

    /**
     * 更新交货节点
     * 使用 DeliveryUpdate 组：
     *   - deliveryId: 必填 ✅（用于定位记录）
     *   - lineId: 必填 ✅
     *   - version: 必填 ✅（乐观锁）
     */
    @PostMapping("/update/delivery")
    public Result<String> updatePlanDelivery(
            @Validated(ValidationGroups.DeliveryUpdate.class)
            @RequestBody SalesPlanFormDeliveryDTO dto) {
        return salesPlanDeliveryService.update(dto);
    }

    @PostMapping("/update/list/delivery")
    public Result<String> updatePlanDeliveryList(
            @Validated(ValidationGroups.DeliveryUpdate.class)
            @RequestBody List<SalesPlanFormDeliveryDTO> dtoList) {
        return salesPlanDeliveryService.updateBatch(dtoList);
    }

    @PostMapping("/update/plan/status")
    public Result<String> updatePlanStatus(
        @RequestBody SalesPlanStatusDTO dto
    ) {
        return null;
    }

    @PostMapping("/update/line/status")
    public Result<String> updatePlanLineStatus(
        @RequestBody SalesPlanLineStatusDTO dtoList
    ) {
        return null;
    }


}
