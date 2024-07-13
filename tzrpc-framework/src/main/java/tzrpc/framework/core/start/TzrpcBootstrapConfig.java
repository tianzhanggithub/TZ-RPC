package tzrpc.framework.core.start;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "tzrpc")
public class TzrpcBootstrapConfig{

    private String appName = "tzrpc-application";
    @Value("${server.port:8863}")
    private int port;

}
