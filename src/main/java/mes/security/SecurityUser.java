package mes.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
public class SecurityUser implements UserDetails {

    private String userId;
    private String username;
    @JsonIgnore // 忽略密码字段
    private String password;
    private Integer status;
    private List<GrantedAuthority> authorities;// 权限列表

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<? extends GrantedAuthority> authorities) {
        this.authorities = authorities != null ? List.copyOf(authorities) : null;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }// 账号是否未过期

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }// 账号是否未锁定

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }// 凭证是否未过期

    @Override
    public boolean isEnabled() {
        return status != null && status == 1;
    }// 账号是否启用
}
