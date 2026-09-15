package mes.controller;

import mes.common.result.Result;
import mes.mapper.SalesPlanLineMapper;
import mes.service.SalesPlanDeliveryService;
import mes.service.SalesPlanLineService;
import mes.service.SalesPlanService;
import mes.vo.sales.SalesPlanVO;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;


import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/sales-plan")
public class SalesPlanController {


    private final SalesPlanService salesPlanService;
    private final SalesPlanLineService salesPlanLineService ;
    private final SalesPlanDeliveryService salesPlanDeliveryService;

    public SalesPlanController(SalesPlanService salesPlanService, SalesPlanLineService salesPlanLineService, SalesPlanDeliveryService salesPlanDeliveryService) {
        this.salesPlanService = salesPlanService;
        this.salesPlanLineService = salesPlanLineService;
        this.salesPlanDeliveryService = salesPlanDeliveryService;
    }

    @GetMapping("/monthly")
    public Result<List<SalesPlanVO>> listMonthly(@RequestParam @DateTimeFormat(pattern = "yyyy-MM") Date yearMonth) {
        return salesPlanService.listMonthly(yearMonth);
    }

    @GetMapping("/delete/plan/{planId}/{version}")
    public Result<String> deletePlan(@PathVariable String planId, @PathVariable Integer version) {
        return salesPlanService.delete(planId, version);
    }

    @GetMapping("/delete/plan/line/{planLineId}/{version}")
    public Result<String> deletePlanLine(@PathVariable String planLineId, @PathVariable Integer version) {
        return salesPlanLineService.delete(planLineId, version);
    }

    @GetMapping("/delete/plan/delivery/{planLineId}/{version}")
    public Result<String> deletePlanDelivery(@PathVariable String planLineId, @PathVariable Integer version) {
        return salesPlanDeliveryService.delete(planLineId, version);
    }

}