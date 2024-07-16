package tzrpc.framework.core.start;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "tzrpc")
public class TzrpcBootstrapConfig{

    private String appName = "tzrpc-application";    // 应用名称
    private int port = 8863;                         // RPC 应用端口
    private int eventLoopBossThread = 2;             // eventLoop boss 部分的线程数
    private int eventLoopWorkerThread = 10;          // eventLoop worker 部分的线程数

}
