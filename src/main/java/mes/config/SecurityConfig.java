package mes.config;

import mes.security.JwtAccessDeniedHandler;
import mes.security.JwtAuthenticationEntryPoint;
import mes.security.JwtAuthenticationFilter;
import mes.security.UrlAuthorizationManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // 开启方法级安全配置
public class SecurityConfig {
    
    private final JwtAuthenticationFilter jwtAuthenticationFilter;// 自定义JWT过滤器
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;// 自定义无权限访问处理类
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;// 自定义认证入口点类
    private final UrlAuthorizationManager urlAuthorizationManager;// URL权限管理器
    
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, JwtAccessDeniedHandler jwtAccessDeniedHandler, JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint, UrlAuthorizationManager urlAuthorizationManager) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.urlAuthorizationManager = urlAuthorizationManager;
    }
    

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }// 密码加密器


    // 配置AuthenticationManager对象，用于身份验证
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager(); // 返回AuthenticationManager对象
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));       // 允许所有来源（生产环境建议限制具体域名）
        config.setAllowedMethods(List.of("*"));               // GET/POST/PUT/DELETE...
        config.setAllowedHeaders(List.of("*"));               // 允许所有请求头
        config.setExposedHeaders(List.of("Authorization"));   // 前端可读取的响应头
        config.setAllowCredentials(true);                     // 允许携带 Cookie
        config.setMaxAge(3600L);                              // 预检请求缓存时间（秒）

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))// 跨域配置
                .csrf(AbstractHttpConfigurer::disable) // 禁用CSRF保护
                .sessionManagement( // 会话管理
                        session ->
                                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 禁用会话管理
                )
                .authorizeHttpRequests(
                        authorize -> authorize
                                .requestMatchers("/api/auth/**").permitAll() // 允许访问登录接口
                                .anyRequest().access(urlAuthorizationManager) // 其他请求都需要认证
                ) // 授权请求
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)   // 没认证 → 401
                        .accessDeniedHandler(jwtAccessDeniedHandler)            // 有认证但没权限 → 403
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // 在UsernamePasswordAuthenticationFilter之前添加JwtAuthenticationFilter过滤器
        return http.build();
    }
}
