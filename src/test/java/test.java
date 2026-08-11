import mes.Main;
import mes.dto.AddUserDTO;
import mes.service.SysUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = Main.class)
public class test {
    @Autowired
    private SysUserService sysUserService;


    @Test
    public void testAddUser() {
        // 测试添加用户逻辑
        AddUserDTO addUserDTO = new AddUserDTO();
        addUserDTO.setUsername("admin");
        addUserDTO.setPassword("123456");
        addUserDTO.setRealName("管理员");
        int result = sysUserService.add(addUserDTO);
        // 断言添加成功
        assertTrue(result != 0);// 断言添加成功
    }
}
