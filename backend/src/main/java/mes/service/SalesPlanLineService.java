package mes.service;

import mes.common.result.Result;
import mes.dto.sales.SalesPlanFormLineDTO;

public interface SalesPlanLineService {

    Result<String> delete(String planLineId, Integer version);
    Result<String> update(SalesPlanFormLineDTO dto);
    Result<String> insert(SalesPlanFormLineDTO dto);
}
