package tzrpc.framework.core.communicate.message;

import lombok.Data;
import lombok.experimental.Accessors;

import java.nio.charset.StandardCharsets;

@Data
@Accessors(chain = true)
public class TzrpcResponseMessage {

    // 2. 请求版本号. 请求方发送请求时自己生成，用于在收到消息后，判断属于哪一次请求，服务提供方无需更改此值
    // long, 固定 2 字节
    private Long requestId;
    // 3. 压缩类型, 固定 1 字节
    private byte compress;
    // 4. 序列化方式， 固定 1 字节
    private byte serialize;
    // 5. 执行结果， 不定长
    private Object result;

}
