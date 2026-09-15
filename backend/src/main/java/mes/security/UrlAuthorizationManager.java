package mes.security;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import mes.entity.SysMenu;
import mes.mapper.SysMenuMapper;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@Slf4j
@Component
public class UrlAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private final SysMenuMapper sysMenuMapper;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private volatile Map<String, String> urlPermMapping = new ConcurrentHashMap<>();

    public UrlAuthorizationManager(SysMenuMapper sysMenuMapper) {
        this.sysMenuMapper = sysMenuMapper;
    }

    @PostConstruct// 初始化时加载URL权限映射
    public void init() {
        loadMapping();
    }

    public void reloadMapping() {
        loadMapping();
        log.info("URL权限映射已刷新");
    }

    private void loadMapping() {
        Map<String, String> newMapping = new ConcurrentHashMap<>();
        try {
            List<SysMenu> menus = sysMenuMapper.selectUrlPermissions();
            if (menus != null) {
                for (SysMenu menu : menus) {
                    if (menu.getApiUrl() != null && menu.getPerms() != null) {
                        newMapping.put(menu.getApiUrl(), menu.getPerms());
                    }
                }
            }
        } catch (Exception e) {
            log.error("加载URL权限映射失败", e);
        }
        this.urlPermMapping = newMapping;
        log.info("加载URL权限映射: {} 条", urlPermMapping.size());
    }

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authenticationSupplier,
                                       RequestAuthorizationContext context) { // 判断当前请求的URL是否匹配到某个权限标识
        String requestUri = context.getRequest().getRequestURI();

        for (Map.Entry<String, String> entry : urlPermMapping.entrySet()) {
            if (pathMatcher.match(entry.getKey(), requestUri)) {
                String requiredPerm = entry.getValue();
                Authentication authentication = authenticationSupplier.get();

                if (authentication == null || !authentication.isAuthenticated()) {
                    return new AuthorizationDecision(false);
                }

                for (GrantedAuthority authority : authentication.getAuthorities()) {
                    if (requiredPerm.equals(authority.getAuthority())) {
                        return new AuthorizationDecision(true);
                    }
                }
                return new AuthorizationDecision(false);
            }
        }
        return new AuthorizationDecision(true);
    }
}