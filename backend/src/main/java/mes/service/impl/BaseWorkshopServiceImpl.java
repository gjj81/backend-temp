package mes.service.impl;

import mes.common.result.Result;
import mes.mapper.BaseProdLineMapper;
import mes.mapper.BaseWorkshopMapper;
import mes.service.BaseService;
import mes.vo.base.ProdLineVO;
import mes.vo.base.WorkshopVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BaseWorkshopServiceImpl implements BaseService {

    private final BaseWorkshopMapper workshopMapper;
    private final BaseProdLineMapper prodLineMapper;

    public BaseWorkshopServiceImpl(BaseWorkshopMapper workshopMapper, BaseProdLineMapper prodLineMapper) {
        this.workshopMapper = workshopMapper;
        this.prodLineMapper = prodLineMapper;
    }

    @Override
    public Result<List<WorkshopVO>> listWorkshops() {
        return Result.success(workshopMapper.list());
    }

    @Override
    public Result<List<ProdLineVO>> listProdLines() {
        return Result.success(prodLineMapper.list());
    }
}
