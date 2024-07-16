package tzrpc.framework.core.start;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import tzrpc.framework.core.clientproxy.TzrpcAutoDiscover;
import tzrpc.framework.core.serviceproxy.TzrpcAutoRegister;

@Configuration
@Import({TzrpcBootstrapConfig.class, TzrpcBootstrap.class})
public class TzrpcBootstrapAutoConfiguration{
}
