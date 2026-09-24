package mes.service.impl;

import lombok.val;
import mes.common.result.Result;
import mes.dto.sales.SalesPlanFormDeliveryDTO;
import mes.mapper.SalesPlanDeliveryMapper;
import mes.service.SalesPlanDeliveryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class SalesPlanDeliveryServiceImpl implements SalesPlanDeliveryService {
    private final SalesPlanDeliveryMapper salesPlanDeliveryMapper;

    public SalesPlanDeliveryServiceImpl(SalesPlanDeliveryMapper salesPlanDeliveryMapper) {
        this.salesPlanDeliveryMapper = salesPlanDeliveryMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)// 声明事务回滚，当抛出异常时回滚
    public Result<String> delete(String deliveryId, Integer version) {
        val i = salesPlanDeliveryMapper.deleteByPlanLineDeliveryId(deliveryId, version);
        if (i == 0) {
            throw new RuntimeException("删除失败");
        }
        return Result.success("删除成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> update(SalesPlanFormDeliveryDTO dto) {
        val i = salesPlanDeliveryMapper.update(dto);
        if (i == 0) {
            throw new RuntimeException("更新失败"+dto.getNodeName()+"已被他人修改，请刷新后重试");
        }
        return Result.success("更新成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> insert(SalesPlanFormDeliveryDTO dto) {
        String nodeName = UUID.randomUUID().toString();
        dto.setDeliveryId(nodeName);// 新增时自动生成ID
        val i = salesPlanDeliveryMapper.insert(dto);
        if (i == 0) {
            throw new RuntimeException("插入失败");
        }
        return Result.success(nodeName);
    }

    @Override
    public Result<String> updateBatch(List<SalesPlanFormDeliveryDTO> dtoList) {
        val i = salesPlanDeliveryMapper.updateBatch(dtoList);
        if (i == 0) {
                throw new RuntimeException("更新失败");
            };
        return Result.success("更新成功");
    }
}
