package tzrpc.framework.core.communicate.message;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class TzrpcMessageBody implements Serializable {

    // 1. 接口全类名， 不定长
    private String interf;
    // 2. 方法名， 不定长
    private String method;
    // 3. 参数类型列表， 不定长
    private Class[] paramType;
    // 4. 参数值列表， 不定长
    private Object[] paramValue;
    // 5. 返回值的类型， 不定长
    private Class<?> resultType;

}
