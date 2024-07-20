package tzrpc.framework.core.communicate.provider;


import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import lombok.extern.slf4j.Slf4j;
import tzrpc.framework.core.communicate.consumer.outboundhandler.RequestEncodeHandler;

@Slf4j
public class ProviderChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline();
    }
}
