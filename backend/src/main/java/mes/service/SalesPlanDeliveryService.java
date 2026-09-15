package mes.service;

import mes.common.result.Result;
import mes.dto.sales.SalesPlanDeliveryUpdateDTO;

public interface SalesPlanDeliveryService {
    Result<String> delete(String planLineId, Integer version);
    Result<String> update(SalesPlanDeliveryUpdateDTO dto);
    Result<String> insert(SalesPlanDeliveryUpdateDTO dto);
}
