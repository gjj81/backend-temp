package mes.service;

import mes.common.result.Result;
import mes.dto.sales.SalesPlanLineUpdateDTO;

public interface SalesPlanLineService {

    Result<String> delete(String planLineId, Integer version);
    Result<String> update(SalesPlanLineUpdateDTO dto);
    Result<String> insert(SalesPlanLineUpdateDTO dto);
}
