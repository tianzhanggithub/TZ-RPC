package tzrpc.framework.core.serviceproxy;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

@Configuration
@Import({TzrpcAutoRegister.class})
public class TzrpcServerProxyAutoConfiguration {
}
