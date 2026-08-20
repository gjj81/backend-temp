package mes.service.impl;

import lombok.val;
import mes.common.result.Result;
import mes.dto.sales.SalesPlanDeliveryUpdateDTO;
import mes.mapper.SalesPlanDeliveryMapper;
import mes.service.SalesPlanDeliveryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SalesPlanDeliveryServiceImpl implements SalesPlanDeliveryService {
    private final SalesPlanDeliveryMapper salesPlanDeliveryMapper;

    public SalesPlanDeliveryServiceImpl(SalesPlanDeliveryMapper salesPlanDeliveryMapper) {
        this.salesPlanDeliveryMapper = salesPlanDeliveryMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)// 声明事务回滚，当抛出异常时回滚
    public Result<String> delete(String planLineId, Integer version) {
        val i = salesPlanDeliveryMapper.deleteByPlanLineDeliveryId(planLineId, version);
        if (i == 0) {
            throw new RuntimeException("删除失败");
        }
        return Result.success("删除成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> update(SalesPlanDeliveryUpdateDTO dto) {
        val i = salesPlanDeliveryMapper.batchUpdate(dto);
        if (i == 0) {
            throw new RuntimeException("更新失败"+dto.getNodeName()+"已被他人修改，请刷新后重试");
        }
        return Result.success("更新成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> insert(SalesPlanDeliveryUpdateDTO dto) {
        salesPlanDeliveryMapper.insert(dto);
        return Result.success("插入成功");
    }
}
