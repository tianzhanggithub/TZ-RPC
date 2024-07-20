package tzrpc.framework.common.util;

import java.util.concurrent.atomic.AtomicLong;

public class IdGenerateUtil {

    public static final AtomicLong idGenerator = new AtomicLong(1);

    public static Long nextId() {
        return idGenerator.getAndIncrement();
    }

}
