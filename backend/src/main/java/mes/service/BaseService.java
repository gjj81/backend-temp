package mes.service;

import mes.common.result.Result;
import mes.vo.base.ProdLineVO;
import mes.vo.base.WorkshopVO;

import java.util.List;

public interface BaseService {

    Result<List<WorkshopVO>> listWorkshops();

    Result<List<ProdLineVO>> listProdLines();
}
