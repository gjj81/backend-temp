package mes.service;

import mes.common.result.Result;
import mes.dto.sales.SalesPlanFormDeliveryDTO;

import java.util.List;

public interface SalesPlanDeliveryService {
    Result<String> delete(String planLineId, Integer version);
    Result<String> update(SalesPlanFormDeliveryDTO dto);
    Result<String> insert(SalesPlanFormDeliveryDTO dto);

    Result<String> updateBatch(List<SalesPlanFormDeliveryDTO> dtoList);
}
