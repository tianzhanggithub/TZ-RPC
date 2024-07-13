package tzrpc.framework.common.util;

import java.util.Collection;

public class CollectionUtil {

    public static boolean isEmpty(Collection<?> c) {
        return c == null || c.size() == 0;
    }

}
