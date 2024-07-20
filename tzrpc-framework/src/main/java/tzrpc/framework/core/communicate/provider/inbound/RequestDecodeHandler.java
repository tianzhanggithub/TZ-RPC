package tzrpc.framework.core.communicate.provider.inbound;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import tzrpc.framework.core.clientproxy.dynamicproxy.TzrpcClassLoader;
import tzrpc.framework.core.communicate.message.TzrpcMessageConstant;
import tzrpc.framework.core.communicate.message.TzrpcRequestMessage;

import java.util.List;

// 报文入站解码器
public class RequestDecodeHandler extends LengthFieldBasedFrameDecoder {

    public RequestDecodeHandler() {
        super(TzrpcMessageConstant.maxFrameLength,    // 最大帧(报文)长度，超过这个值则会直接丢弃
                TzrpcMessageConstant.fullLengthFieldOffset,   // 长度字段的偏移量，也就是报文中的 "报文总长度" 数据所处位置的偏移量
                TzrpcMessageConstant.fullLengthFieldLength,   // 报文总长 字段的 长度，我们的报文里定义的是 4Byte
                0,                     // 负载的适配长度
                0);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        return super.decode(ctx, in);
    }
}
