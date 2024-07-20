package tzrpc.client.testmodule.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tzrpc.api.testmodule.SayHelloService;
import tzrpc.framework.annotation.TzrpcAutowired;
import tzrpc.framework.annotation.TzrpcProvider;

import javax.annotation.PostConstruct;

@Component
@Slf4j
public class TestServiceImpl implements TestService {

    @TzrpcAutowired
    private SayHelloService sayHelloService;

    @Override
    public void meeting() {
        log.info("我是 client，我来跟你打招呼~~~~~");
        sayHelloService.sayHello();
    }

    @PostConstruct
    private void init() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        Thread.sleep(5000);
                        meeting();
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
