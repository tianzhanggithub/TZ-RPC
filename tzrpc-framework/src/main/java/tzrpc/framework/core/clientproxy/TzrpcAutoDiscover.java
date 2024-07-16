package tzrpc.framework.core.clientproxy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import tzrpc.framework.annotation.TzrpcAutowired;
import tzrpc.framework.common.exception.TzrpcException;
import tzrpc.framework.core.clientproxy.dynamicproxy.ClientProxyGenerator;

import java.lang.reflect.*;

@Slf4j
@Component
public class TzrpcAutoDiscover implements CommandLineRunner {

    private final ApplicationContext applicationContext;

    @Autowired
    public TzrpcAutoDiscover(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void run(String... args) throws Exception {
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        for(String beanName : beanNames) {
            Object bean = applicationContext.getBean(beanName);
            Field[] fields = bean.getClass().getDeclaredFields();
            for(Field field : fields) {
                // 没有添加 @TzrpcAutowired 的字段，跳过
                if(field.getAnnotation(TzrpcAutowired.class) == null)
                    continue;
                field.setAccessible(true);
                // 该字段有 @TzrpcAutowired，处理. 首先判断必须是一个 接口类型
                Class<?> fieldClass = field.getType();
                if(!fieldClass.isInterface()) {
                    log.error("TzrpcAutoDiscover.run --> @TzrpcAutowired 的字段必须是 Interface 类型; FieldClass = {}", fieldClass);
                    throw new TzrpcException("config fail", "@TzrpcAutowired 的字段必须是 Interface 类型");
                }
                Object proxy = ClientProxyGenerator.generate(fieldClass);
                field.set(bean, fieldClass.cast(proxy));
                log.info("@TzrpcAutowired 成功注入: " + bean.getClass().getName() + "." + field.getName() + " --> " + proxy);
            }
        }
    }

}
