package cn.hotdev.example.controllers.hello;

import cn.hotdev.example.models.hello.Hello;
import cn.hotdev.example.viewmodels.hello.HelloView;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.atomic.LongAdder;

/**
 * Created by andy on 5/22/15.
 */
@RestController
public class HelloController {

    private static final String template = "Hello, %s!";
    private final LongAdder counter = new LongAdder();

    @RequestMapping(method = RequestMethod.GET, value = "/hello/{name}")
    public HelloView hello(@PathVariable("name") String name,
                           @RequestParam(value = "msg", required = false, defaultValue = "Welcome!") String msg
    ) {
        counter.increment();

        Hello hello = new Hello(counter.longValue(), String.format(template, name), msg);

        return new HelloView(hello);
    }
}
