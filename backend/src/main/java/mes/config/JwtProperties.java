package mes.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String secret;
    private Long expiration;
    private Long refreshExpiration;
    private String header;
    // 默认前缀为Bearer
    private String prefix;
    private String refreshKeyPrefix;
    private String metaKeyPrefix;


}
