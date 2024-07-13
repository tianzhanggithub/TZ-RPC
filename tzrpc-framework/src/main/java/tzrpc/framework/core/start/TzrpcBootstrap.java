package tzrpc.framework.core.start;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tzrpc.framework.core.protocol.ProtocolEnum;
import tzrpc.framework.core.registry.core.Registry;

import java.util.List;

@Slf4j
@Component
public class TzrpcBootstrap {

    @Autowired
    private Registry registry;

    private List<ServiceConfig<?, ?>> services;
    private ProtocolEnum protocol;

    public TzrpcBootstrap setServices(List<ServiceConfig<?, ?>> services) {
        this.services = services;
        return this;
    }

    public TzrpcBootstrap protocol(ProtocolEnum protocol) {
        this.protocol = protocol;
        return this;
    }

    // 启动
    public void start() throws Exception{
        log.info("TzrpcBootstrap.start --> 原神，启动！");
        this.registry.maintainMeta();
        this.registry.registerProvider(this.services);
    }
}
