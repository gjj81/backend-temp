package mes.service;

import mes.common.result.Result;
import mes.dto.prod.ProdScheduleSaveDTO;
import mes.vo.prod.ProdScheduleVO;
import mes.vo.prod.ScheduleProductVO;

import java.util.Date;
import java.util.List;

public interface ProdScheduleService {

    /**
     * 排产工作台-月度销售计划产品列表
     */
    Result<List<ScheduleProductVO>> listWorkbenchProducts();

    Result<List<ProdScheduleVO>> listProdProducts(Date startDate, Date endDate);

    Result<Void> saveSchedule(List<ProdScheduleSaveDTO> dtoList);

    Result<Void> updateSchedule(List<ProdScheduleSaveDTO> dtoList);

    Result<Void> deleteSchedule(String id, Integer version);
}