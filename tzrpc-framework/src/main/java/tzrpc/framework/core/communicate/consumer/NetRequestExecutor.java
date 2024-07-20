package tzrpc.framework.core.communicate.consumer;

import com.alibaba.fastjson.JSON;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tzrpc.framework.common.exception.TzrpcException;
import tzrpc.framework.common.util.IdGenerateUtil;
import tzrpc.framework.core.communicate.RemoteResponseWaiter;
import tzrpc.framework.core.communicate.message.TzrpcMessageBody;
import tzrpc.framework.core.communicate.message.TzrpcRequestMessage;
import tzrpc.framework.core.enumtype.CompressEnum;
import tzrpc.framework.core.enumtype.RequestEnum;
import tzrpc.framework.core.enumtype.SerializeEnum;
import tzrpc.framework.core.start.TzrpcBootstrap;
import tzrpc.framework.core.start.TzrpcBootstrapConfig;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@Slf4j
public class NetRequestExecutor implements DisposableBean {

    @Autowired
    private TzrpcBootstrap tzrpcBootstrap;
    @Autowired
    private TzrpcBootstrapConfig tzrpcBootstrapConfig;

    private Bootstrap nettyBootstrap;

    // InetSocketAddress 重写了 equals and hashCode，可以用作 key
    private final Map<InetSocketAddress, Channel> nettyChannelCache = new HashMap<>();

    @PostConstruct
    private void init() {
        startNetty();
    }

    private void startNetty() {
        log.info("NetRequestExecutor.startNetty --> 启动 NetRequestExecutor 服务");
        // 定义线程池
        NioEventLoopGroup group = new NioEventLoopGroup();
        //  启动一个客户端需要一个辅助类
        Bootstrap bootstrap = new Bootstrap();
        this.nettyBootstrap = bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ConsumerChannelInitializer());
        log.info("NetRequestExecutor.startNetty --> 启动 NetRequestExecutor 服务完毕");
    }

    public Object remoteExecute(Method method, Object[] args) throws Exception {
        Class<?> interf = method.getDeclaringClass();
        InetSocketAddress proxyProviderAddr = tzrpcBootstrap.getProxyProvider(interf.getName());
        log.info("尝试调用远程方法: " + interf.getName() + "." + method.getName() + "; 找到了提供者: " + proxyProviderAddr);
        // 通过 Netty 向远端请求服务执行结果
        Channel channel = getChannel(proxyProviderAddr);
        return execute(channel, method, args);
    }

    // 有缓存的 channel，可以直接用
    private Object execute(Channel channel, Method method, Object[] args) {
        try {
            // 发送消息
            CompletableFuture<Object> waiter = new CompletableFuture<>();
            long requestId = IdGenerateUtil.nextId();
            TzrpcRequestMessage message = encapRequestMessage(requestId, method, args);
            log.info("尝试发送请求信息: " + JSON.toJSONString(message));
            channel.writeAndFlush(message).addListener((ChannelFutureListener) future -> {
                if(!future.isSuccess()) {
                    Throwable cause = future.cause();
                    log.error("NetRequestExecutor.execute --> 请求远程服务提供者执行业务失败; ", cause);
                    throw new TzrpcException("communicate fail", "请求远程服务提供者执行业务失败; " + cause.getMessage());
                }
                // 挂起 CompletableFuture，等待服务端返回
                RemoteResponseWaiter.addWaiter(requestId, waiter);
            });
            return waiter.get(tzrpcBootstrapConfig.getRemoteCallTimeout(), TimeUnit.MILLISECONDS);
        } catch(Exception e) {
            log.error("Request Fail, 请求服务提供方执行业务失败; ", e);
            throw new TzrpcException("request fail", "请求服务提供方执行业务失败");
        }
    }

    private Channel getChannel(InetSocketAddress address) {
        try {
            Channel channel = nettyChannelCache.get(address);
            if(channel == null || !channel.isOpen()) {
                CompletableFuture<Channel> waiter = new CompletableFuture<>();
                this.nettyBootstrap.connect(address).addListener((ChannelFutureListener)future -> {
                    if(future.isDone()) {
                        waiter.complete(future.channel());
                    } else if(!future.isSuccess()) {
                        Throwable cause = future.cause();
                        log.error("NetRequestExecutor.getChannel --> 连接远端服务提供方失败，连接失败; ", cause);
                        throw new TzrpcException("communicate fail", "连接远端服务提供方失败，连接失败; " + cause.getMessage());
                    }
                });
                channel = waiter.get(tzrpcBootstrapConfig.getRemoteCallTimeout(), TimeUnit.MILLISECONDS);
                if(channel == null || !channel.isOpen()) {
                    throw new TzrpcException("communicate fail", "连接远端服务提供方失败，连接超时");
                }
                nettyChannelCache.put(address, channel);
            }
            return channel;
        } catch(Exception e) {
            log.info("NetRequestExecutor.getChannel --> 获取 Channel 时发生异常, address = {}; ", address, e);
            throw new TzrpcException("get Channel fail", "获取 Channel 时发生异常");
        }
    }

    @Override
    public void destroy() throws Exception {
        if(this.nettyBootstrap != null) {
            try {
                log.info("NetRequestExecutor.destroy --> 优雅关闭 Netty EventLoop");
                this.nettyBootstrap.config().group().shutdownGracefully().sync();
            } catch(Exception e) {
                log.info("NetRequestExecutor.destroy --> 优雅关闭 Netty EventLoop 失败; ", e);
            }
        }
    }

    private TzrpcRequestMessage encapRequestMessage(long requestId, Method method, Object[] args) {
        Parameter[] params = method.getParameters();
        List<? extends Class<?>> paramTypes = Arrays.stream(params).map(Parameter::getType).collect(Collectors.toList());
        return TzrpcRequestMessage.builder()
                .requestId(requestId)
                .compress(CompressEnum.parseFromName(tzrpcBootstrapConfig.getCompress()).getCode())
                .serialize(SerializeEnum.parseFromName(tzrpcBootstrapConfig.getSerialize()).getCode())
                .requestType(RequestEnum.COMMON.getCode())
                .body(TzrpcMessageBody.builder()
                        .interf(method.getDeclaringClass().getName())
                        .method(method.getName())
                        .paramType(paramTypes.toArray(new Class[0]))
                        .paramValue(args)
                        .resultType(method.getReturnType())
                        .build())
                .build();
    }
}
