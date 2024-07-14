package tzrpc.framework.core.clientproxy;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.annotation.PostConstruct;

@Configuration
@Import({TzrpcServiceCallProxyHandler.class})
public class TzrpcClientProxyAutoConfiguration {
}
