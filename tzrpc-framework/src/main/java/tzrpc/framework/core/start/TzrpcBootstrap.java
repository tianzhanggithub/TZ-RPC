package tzrpc.framework.core.start;

import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tzrpc.framework.common.util.NetUtil;
import tzrpc.framework.core.protocol.ProtocolEnum;
import tzrpc.framework.core.registry.core.Registry;

import java.util.List;

@Slf4j
@Data
@Accessors(chain = true)
@Component
public class TzrpcBootstrap {

    @Autowired
    private Registry registry;
    @Autowired
    private TzrpcBootstrapConfig config;

    private List<TzrpcService<?, ?>> services;
    private ProtocolEnum protocol;

    // 启动
    public void start() throws Exception{
        log.info("TzrpcBootstrap.start --> 原神，启动！");
        this.registry.maintainMeta();
        this.registry.registerProvider(this.services, NetUtil.getIp(), config.getPort());
    }
}
