package tzrpc.framework.core.clientproxy;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import tzrpc.framework.core.clientproxy.dynamicproxy.TzrpcServiceCallProxyHandler;

@Configuration
@Import({TzrpcAutoDiscover.class, TzrpcServiceCallProxyHandler.class})
public class TzrpcClientProxyAutoConfiguration {
}
