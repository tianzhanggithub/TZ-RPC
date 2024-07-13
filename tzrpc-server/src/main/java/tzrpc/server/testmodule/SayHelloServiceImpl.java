package tzrpc.server.testmodule;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tzrpc.api.testmodule.SayHelloService;
import tzrpc.framework.annotation.TzrpcProvider;

import javax.annotation.PostConstruct;

@Slf4j
@Service
@TzrpcProvider(proxy = SayHelloService.class)
public class SayHelloServiceImpl implements SayHelloService {

    @Override
    public void sayHello() {
        System.out.println("Hello, 我是 tzprc-server 模块");
    }
}
