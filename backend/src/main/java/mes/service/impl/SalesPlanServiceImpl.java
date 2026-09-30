package mes.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import mes.common.result.Result;
import mes.dto.sales.SalesPlanStatusDTO;
import mes.dto.sales.SalesPlanFormDeliveryDTO;
import mes.dto.sales.SalesPlanFormLineDTO;
import mes.dto.sales.SalesPlanFormDTO;
import mes.dto.sales.SalesPlanLineStatusDTO;
import mes.mapper.SalesPlanDeliveryMapper;
import mes.mapper.SalesPlanLineMapper;
import mes.mapper.SalesPlanMapper;
import mes.service.SalesPlanService;
import mes.vo.sales.SalesPlanVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SalesPlanServiceImpl implements SalesPlanService {

    private final SalesPlanMapper salesPlanMapper;
    private final SalesPlanLineMapper salesPlanLineMapper;
    private final SalesPlanDeliveryMapper salesPlanDeliveryMapper;


    @Override
    public Result<List<SalesPlanVO>> listMonthly(Date yearMonth) {
        List<SalesPlanVO> salesPlanVOList = salesPlanMapper.selectByPlanMonth(yearMonth);
        return Result.success(salesPlanVOList);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> delete(String planId, Integer version) {
        // 这里可以添加删除逻辑，例如：
        val i1 = salesPlanMapper.deleteByPrimaryKey(planId, version);
        if (i1 == 0) {
            throw new RuntimeException("关联的销售计划删除失败");
        }
        // 先删除关联的发货计划行
        List<String> planLineIdList = salesPlanLineMapper.selectPlanLineIdList(planId);
        if (CollUtil.isNotEmpty(planLineIdList)) {// 当planLineIdList不为空时
            salesPlanDeliveryMapper.deleteByPlanLineDeliveryList(planLineIdList);
        }
        // 先删除关联的销售计划行
        salesPlanLineMapper.deleteByPlanId(planId);
        return Result.success("删除成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> update(SalesPlanFormDTO dto) {
        // 1. 更新主表（乐观锁）
        int affected = salesPlanMapper.updateByPrimaryKey(dto);
        if (affected == 0) {
            throw new RuntimeException("销售计划已被他人修改，请刷新后重试");
        }
        // 2.1 分离：有 lineId = 更新，无 lineId = 新增
        List<SalesPlanFormLineDTO> updateLines = new ArrayList<>();
        List<SalesPlanFormLineDTO> insertLines = new ArrayList<>();
        // 2. 处理子表（sales_plan_line）
        List<SalesPlanFormLineDTO> lines = dto.getLines();
        if (CollUtil.isNotEmpty(lines)) {
            for (SalesPlanFormLineDTO line : lines) {
                if (StrUtil.isNotBlank(line.getLineId())) {// 已存在ID，则为更新
                    updateLines.add(line);
                } else {// 无ID，则为新增
                    line.setLineId(IdUtil.fastSimpleUUID()); // 生成新ID,格式为UUID格式长度32
                    line.setPlanId(dto.getPlanId());
                    insertLines.add(line);
                }
            }
            // 2.2 批量更新子表（乐观锁）
            if (CollUtil.isNotEmpty(updateLines)) {// 批量更新子表,当updateLines不为空时
                /*
                * TODO 批量更新子表（sales_plan_line）现在数据量少，并且需要考虑乐观锁更新，后续使用BATCH或者先查后改 方法批量更新
                * */
                for (SalesPlanFormLineDTO line : updateLines) {
                    int i = salesPlanLineMapper.updateByPrimaryKey(line);
                    if (i == 0) {
                        String message = line.getProductName() + "产品修改失败\n";
                        throw new RuntimeException(message);
                    };
                }
            }
            List<SalesPlanFormDeliveryDTO> updateDels = new ArrayList<>();
            List<SalesPlanFormDeliveryDTO> insertDels = new ArrayList<>();
            // 3. 处理孙表（sales_plan_delivery）
            for (SalesPlanFormLineDTO line : updateLines) {
                List<SalesPlanFormDeliveryDTO> deliveries = line.getDeliveries();
                if (CollUtil.isEmpty(deliveries)) continue;

                for (SalesPlanFormDeliveryDTO del : deliveries) {
                    if (StrUtil.isNotBlank(del.getDeliveryId())) {// 已存在ID，则为更新
                        updateDels.add(del);
                    } else {
                        del.setDeliveryId(IdUtil.fastSimpleUUID());
                        del.setLineId(line.getLineId());
                        insertDels.add(del);
                    }
                }
            }
            /*
             * TODO 批量更新孙表（sales_plan_delivery）现在数据量少，并且需要考虑乐观锁更新，后续使用BATCH或者先查后改 方法批量更新,和返回更新失败的节点名称
             */
            if (CollUtil.isNotEmpty(updateDels)) {
                for (SalesPlanFormDeliveryDTO del : updateDels) {
                    int i = salesPlanDeliveryMapper.update(del);
                    if (i == 0) {
                        String message = del.getNodeName() + "节点更新失败\n请检查节点是否存在，或者已经被修改，已被删除\n";
                        throw new RuntimeException(message);
                    }
                }
            }
            // 2.3 批量插入子表
            if (CollUtil.isNotEmpty(insertLines)) {
                salesPlanLineMapper.batchInsert(insertLines);
            }
            for (SalesPlanFormLineDTO line : insertLines) {
                List<SalesPlanFormDeliveryDTO> deliveries = line.getDeliveries();
                if (CollUtil.isEmpty(deliveries)) continue;
                for (SalesPlanFormDeliveryDTO del : deliveries) {
                        del.setDeliveryId(IdUtil.fastSimpleUUID());
                        del.setLineId(line.getLineId());
                        insertDels.add(del);
                }
            }
            if (CollUtil.isNotEmpty(insertDels)) {
                salesPlanDeliveryMapper.batchInsert(insertDels);
            }
        }
        return Result.success("修改成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> insert(SalesPlanFormDTO dto) {
        dto.setPlanId(IdUtil.fastSimpleUUID());
        int affected = salesPlanMapper.insert(dto);
        if (affected == 0) {
            throw new RuntimeException("新增失败");
        }
        List<SalesPlanFormLineDTO> lines = dto.getLines();
        if (CollUtil.isNotEmpty(lines)) {
            List<SalesPlanFormDeliveryDTO> deliveries = new ArrayList<>();
            for (SalesPlanFormLineDTO line : lines) {
                line.setLineId(IdUtil.fastSimpleUUID());
                line.setPlanId(dto.getPlanId());
                if (CollUtil.isEmpty(line.getDeliveries())) continue;
                for (SalesPlanFormDeliveryDTO del : line.getDeliveries()) {
                    del.setLineId(line.getLineId());
                    del.setDeliveryId(IdUtil.fastSimpleUUID());
                    deliveries.add(del);
                }
            }
            salesPlanLineMapper.batchInsert(lines);
            if (CollUtil.isNotEmpty(deliveries)) {
                salesPlanDeliveryMapper.batchInsert(deliveries);
            }
        }
        return Result.success("新增成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> updateStatus(SalesPlanStatusDTO dto) {

        int affected = salesPlanMapper.updateStatus(dto.getPlanId(), dto.getStatus(), dto.getVersion());
        if (affected == 0) {
            throw new RuntimeException("计划状态更新失败");
        }
        List<SalesPlanLineStatusDTO> lineStatusList = dto.getLineStatusList();
        if (CollUtil.isNotEmpty(lineStatusList)) {
            val i = salesPlanLineMapper.batchStatusUpdate(lineStatusList, dto.getStatus());
            if (i == 0) {
                throw new RuntimeException("计划行状态更新失败");
            }
        }
        return Result.success("计划状态更新成功");
    }
}