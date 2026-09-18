package mes.service;

import mes.common.result.Result;
import mes.dto.sales.SalesPlanDeliveryUpdateDTO;

import java.util.List;

public interface SalesPlanDeliveryService {
    Result<String> delete(String planLineId, Integer version);
    Result<String> update(SalesPlanDeliveryUpdateDTO dto);
    Result<String> insert(SalesPlanDeliveryUpdateDTO dto);

    Result<String> updateBatch(List<SalesPlanDeliveryUpdateDTO> dtoList);
}
