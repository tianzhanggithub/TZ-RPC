package tzrpc.framework.core.start;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({TzrpcBootstrapConfig.class, TzrpcBootstrap.class,
        TzrpcAutoRegister.class, TzrpcAutoDiscover.class})
public class TzrpcBootstrapAutoConfiguration{
}
