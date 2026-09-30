package mes.service.impl;

import mes.common.result.Result;
import mes.dto.prod.ProdScheduleSaveDTO;
import mes.entity.ProdSchedule;
import mes.mapper.ProdScheduleMapper;
import mes.mapper.SalesPlanLineMapper;
import mes.service.ProdScheduleService;
import mes.vo.prod.ProdScheduleVO;
import mes.vo.prod.ScheduleDeliveryVO;
import mes.vo.prod.ScheduleProductVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProdScheduleServiceImpl implements ProdScheduleService {

    private final SalesPlanLineMapper salesPlanLineMapper;
    private final ProdScheduleMapper prodScheduleMapper;

    public ProdScheduleServiceImpl(SalesPlanLineMapper salesPlanLineMapper, ProdScheduleMapper prodScheduleMapper) {
        this.salesPlanLineMapper = salesPlanLineMapper;
        this.prodScheduleMapper = prodScheduleMapper;
    }

    @Override
    public Result<List<ScheduleProductVO>> listWorkbenchProducts() {
        /**
         *  TODO 现在是连表查询后续会更新为查询两次，一次查询销售行，一次查询交货节点。然后在服务端合并。
         */
        List<ScheduleProductVO> list = salesPlanLineMapper.selectWorkbenchProducts();
        if (list.isEmpty()) {
            return Result.success("暂无数据");
        }

        return Result.success(list);
    }

    @Override
    public Result<List<ProdScheduleVO>> listProdProducts(Date startDate, Date endDate) {

        return Result.success(prodScheduleMapper.selectProdProducts(startDate, endDate));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> saveSchedule(List<ProdScheduleSaveDTO> dtoList) {
        List<ProdSchedule> entityList = dtoList.stream().map(dto -> {
            ProdSchedule entity = new ProdSchedule();
            BeanUtils.copyProperties(dto, entity);
            entity.setScheduleId(UUID.randomUUID().toString().replace("-", ""));
            entity.setVersion(0);
            return entity;
        }).collect(Collectors.toList());

        int count = prodScheduleMapper.insertList(entityList);
        if (count == 0) {
            return Result.error("保存失败");
        }
        return Result.success("保存成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> updateSchedule(List<ProdScheduleSaveDTO> dtoList) {
        List<ProdSchedule> entityList = dtoList.stream().map(dto -> {
            ProdSchedule entity = new ProdSchedule();
            BeanUtils.copyProperties(dto, entity);
            return entity;
        }).collect(Collectors.toList());

        int count = prodScheduleMapper.updateBatch(entityList);
        if (count == 0) {
            return Result.error("更新失败");
        }
        return Result.success("更新成功");
    }

    @Override
    public Result<Void> deleteSchedule(String id, Integer version) {
        System.out.println(id +"xx"+version);

        int count = prodScheduleMapper.deleteByIdAndVersion(id, version);
        if (count == 0) {
            return Result.error("删除失败");
        }
        return Result.success("删除成功");
    }
}