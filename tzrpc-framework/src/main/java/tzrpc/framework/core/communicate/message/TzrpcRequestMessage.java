package tzrpc.framework.core.communicate.message;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.nio.charset.StandardCharsets;

@Builder
@Getter
public class TzrpcRequestMessage {


    // 2. 请求版本号. 请求方发送请求时自己生成，用于在收到消息后，判断属于哪一次请求，服务提供方无需更改此值
    // long, 固定 8 字节
    private Long requestId;
    // 3. 压缩类型, 固定 1 字节
    private byte compress;
    // 4. 序列化方式， 固定 1 字节
    private byte serialize;
    // 5. 请求类型， 固定 1 字节
    private byte requestType;
    // 6. Body
    private TzrpcMessageBody body;

}
