package mes.util;

import mes.security.SecurityUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class SecurityUtils {

    public static UserDetails getCurrentUser() {// 获取当前登录用户的认证信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();// 获取当前登录用户的认证信息

        if (authentication != null && authentication.getPrincipal() != null) {

            return (UserDetails) authentication.getPrincipal();// 获取当前登录用户的认证信息
        }
        return null;
    }

    /**
     * 获取当前用户ID
     */
    public static String getCurrentUserId() {
        SecurityUser user = (SecurityUser) getCurrentUser();
        return user != null ? user.getUserId() : null;
    }

    /**
     * 获取当前用户名
     */
    public static String getCurrentUsername() {
        SecurityUser user = (SecurityUser) getCurrentUser();
        return user != null ? user.getUsername() : null;
    }

}
