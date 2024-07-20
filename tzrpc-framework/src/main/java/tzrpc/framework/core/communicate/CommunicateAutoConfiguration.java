package tzrpc.framework.core.communicate;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import tzrpc.framework.core.communicate.consumer.NetRequestExecutor;
import tzrpc.framework.core.communicate.provider.NetResponseExecutor;

@Configuration
@Import({NetRequestExecutor.class, NetResponseExecutor.class})
public class CommunicateAutoConfiguration {
}
