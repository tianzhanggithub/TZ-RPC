package tzrpc.framework.core.communicate.provider;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tzrpc.framework.common.exception.TzrpcException;
import tzrpc.framework.core.start.TzrpcBootstrapConfig;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class NetResponseExecutor implements DisposableBean {

    @Autowired
    private TzrpcBootstrapConfig tzrpcBootstrapConfig;   // Tzrpc 核心配置文件

    private ServerBootstrap nettyServerBootstrap;

    @PostConstruct
    private void init() {
        startNetty();
    }

    // 启动 Netty 服务
    private void startNetty() {
        log.info("NetResponseExecutor.startNetty --> 启动 NetResponseExecutor 服务");
        // 1. 创建 eventLoop，boss 只负责处理请求，worker 执行具体任务
        EventLoopGroup boss = new NioEventLoopGroup(tzrpcBootstrapConfig.getEventLoopWorkerThread());
        EventLoopGroup worker = new NioEventLoopGroup(tzrpcBootstrapConfig.getEventLoopWorkerThread());
        try {
            // 创建一个引导程序
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            // 配置 Netty 服务器
            this.nettyServerBootstrap = serverBootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ProviderChannelInitializer());
            // 启动 Netty 监听
            serverBootstrap.bind(tzrpcBootstrapConfig.getPort()).sync();
        } catch(Exception e) {
            log.info("NetResponseExecutor.startNetty --> 启动 Netty 服务异常; ", e);
            throw new TzrpcException("netty fail", e.getMessage());
        }
        log.info("NetResponseExecutor.startNetty --> 启动 NetResponseExecutor 服务完毕");
    }

    @Override
    public void destroy() throws Exception {
        try {
            this.nettyServerBootstrap.config().group().shutdownGracefully().sync();
        } catch(Exception e) {
            log.info("NetResponseExecutor.startNetty --> 关闭 Netty 服务资源异常; ", e);
            throw new TzrpcException("netty shutdown fail", e.getMessage());
        }
    }
}
