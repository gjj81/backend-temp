package mes.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import mes.common.result.Result;
import mes.dto.sales.SalesPlanDeliveryDTO;
import mes.dto.sales.SalesPlanDeliveryUpdateDTO;
import mes.dto.sales.SalesPlanLineUpdateDTO;
import mes.mapper.SalesPlanDeliveryMapper;
import mes.mapper.SalesPlanLineMapper;
import mes.service.SalesPlanDeliveryService;
import mes.service.SalesPlanLineService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class SalesPlanLineServiceImpl implements SalesPlanLineService {
    private final SalesPlanLineMapper salesPlanLineMapper;
    private final SalesPlanDeliveryMapper salesPlanDeliveryMapper;

    public SalesPlanLineServiceImpl(SalesPlanLineMapper salesPlanLineMapper,SalesPlanDeliveryMapper salesPlanDeliveryMapper) {
        this.salesPlanLineMapper = salesPlanLineMapper;
        this.salesPlanDeliveryMapper = salesPlanDeliveryMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> delete(String planLineId, Integer version) {//删除销售计划行,先删除关联的产品计划行，再删除销售计划行

        int lineRows = salesPlanLineMapper.deleteByPrimaryKey(planLineId, version);
        if (lineRows == 0) {
            throw new RuntimeException("该产品行已被他人修改或不存在，请刷新后重试");
        }
        salesPlanDeliveryMapper.deleteByPlanLineDeliveryList(List.of(planLineId));
        return Result.success("删除成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> update(SalesPlanLineUpdateDTO dto) {
        int affected = salesPlanLineMapper.updateByPrimaryKey(dto);
        if (affected == 0) {
            throw new RuntimeException("产品行已被他人修改，请刷新后重试");
        }
        List<SalesPlanDeliveryUpdateDTO> deliveryList = dto.getDeliveries();
        if (CollUtil.isNotEmpty(deliveryList)) {
            List<SalesPlanDeliveryUpdateDTO> updateLines = new ArrayList<>();
            List<SalesPlanDeliveryUpdateDTO> insertLines = new ArrayList<>();
            for (SalesPlanDeliveryUpdateDTO delivery : deliveryList) {
                if (delivery.getDeliveryId() == null) {
                    delivery.setDeliveryId(IdUtil.fastSimpleUUID());
                    delivery.setLineId(dto.getLineId());
                    insertLines.add(delivery);
                } else {
                    updateLines.add(delivery);
                }
            }
            /*
             * TODO 批量更新子表（sales_plan_delivery）现在数据量少，并且需要考虑乐观锁更新，后续使用BATCH或者先查后改 方法批量更新,和返回更新失败的节点名称
             */
            for (SalesPlanDeliveryUpdateDTO delivery : updateLines) {
                int i = salesPlanDeliveryMapper.batchUpdate(delivery);
                if (i == 0) {
                    throw new RuntimeException("交货节点" + delivery.getNodeName() + "已被他人修改，请刷新后重试");
                }
            }
            if (!insertLines.isEmpty()) {
                salesPlanDeliveryMapper.batchInsert(insertLines);
            }
        }
        return Result.success("更新成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> insert(SalesPlanLineUpdateDTO dto) {
        dto.setLineId(IdUtil.fastSimpleUUID());
        salesPlanLineMapper.insert(dto);

        List<SalesPlanDeliveryUpdateDTO> deliveryList = dto.getDeliveries();
        if (CollUtil.isNotEmpty(deliveryList)) {
            deliveryList.forEach(delivery -> {
                delivery.setLineId(dto.getLineId());
                delivery.setDeliveryId(IdUtil.fastSimpleUUID());
            });
            salesPlanDeliveryMapper.batchInsert(deliveryList);
        }

        return Result.success("插入成功");
    }


}
