package mes;

import lombok.val;
import mes.service.SalesPlanService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;


/**
 * SalesPlanService 单元测试
 */
@SpringBootTest
public class test {

    @Autowired
    private SalesPlanService salesPlanService;

    @Test
    public void testListMonthly() {
        Date yearMonth = new Date();
        System.out.println(salesPlanService.listMonthly(yearMonth));
    }
}