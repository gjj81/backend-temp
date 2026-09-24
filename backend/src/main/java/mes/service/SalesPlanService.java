package mes.service;

import mes.common.result.Result;
import mes.dto.sales.SalesPlanFormDTO;
import mes.vo.sales.SalesPlanVO;

import java.util.Date;
import java.util.List;

public interface SalesPlanService {


    Result<List<SalesPlanVO>> listMonthly(Date yearMonth);

    Result<String> delete(String planId, Integer version);

    Result<String> update(SalesPlanFormDTO salesPlanUpdateDTO);

    Result<String> insert(SalesPlanFormDTO salesPlanUpdateDTO);
}