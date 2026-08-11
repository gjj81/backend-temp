package mes.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddUserDTO {
    @NotNull(message = "用户名不能为空")
    private String username;
    @NotNull(message = "密码不能为空")
    private String password;
    @NotNull(message = "真实姓名不能为空")
    private String realName;
    private String phone;
    private String email;
    private String deptId;
    private Integer status;
}
