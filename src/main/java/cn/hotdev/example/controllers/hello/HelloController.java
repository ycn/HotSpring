package cn.hotdev.example.controllers.hello;

import cn.hotdev.example.models.hello.Hello;
import cn.hotdev.example.viewmodels.hello.HelloView;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.atomic.LongAdder;


@RestController
@RequestMapping("/hello")
public class HelloController {

    private static final String template = "Hello, %s! I'm Newbie! ";
    private final LongAdder counter = new LongAdder();

    @RequestMapping(value = "/{name}", method = RequestMethod.GET)
    public HelloView helloWithName(@PathVariable("name") String name,
                                   @RequestParam(value = "msg", required = false, defaultValue = "Welcome!") String msg
    ) {
        counter.increment();

        Hello hello = new Hello(counter.longValue(), String.format(template, name), msg);

        return new HelloView(hello);
    }
}
