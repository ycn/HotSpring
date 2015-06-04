package cn.hotdev.example.controllers;

import cn.hotdev.example.models.customer.Customer;
import cn.hotdev.example.models.customer.CustomerRepository;
import cn.hotdev.example.models.exceptions.BadRequestException;
import cn.hotdev.example.models.hello.Hello;
import cn.hotdev.example.models.rest.RestResponse;
import cn.hotdev.example.models.rest.RestStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.atomic.LongAdder;


@RestController
@RequestMapping("/hello")
public class HelloRestController extends BaseRestController {

    @Autowired
    private CustomerRepository customerRepository;

    private static final String template = "Hello, %s %s!";
    private final LongAdder counter = new LongAdder();

    /**
     * @param name 姓名
     * @param msg  消息
     * @return { "data" : { "hello" : {@linkplain cn.hotdev.example.models.hello.Hello Hello} } }
     * @throws Exception
     * @title Hello测试接口
     * @summary 会输出name参数和msg参数
     */
    @RequestMapping(value = "/{name}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public RestResponse<Hello> helloWithName(@PathVariable("name") String name,
                                             @RequestParam(value = "msg", required = false, defaultValue = "Welcome!") String msg
    ) throws Exception {
        counter.increment();

        List<Customer> customers = customerRepository.findByLastName(name);

        String firstName = "Unknown";

        if (customers != null && !customers.isEmpty()) {
            Customer customer = customers.get(0);
            firstName = customer.getFirstName();
            if (msg != null && !msg.isEmpty())
                msg = "I found U!";
        } else {
            if (msg != null && !msg.isEmpty())
                msg = "Who are U22222?";
        }

        if (name.equals("error")) {
            throw new BadRequestException("name is bad");
        }

        Hello hello = new Hello(counter.longValue(), String.format(template, firstName, name), msg);

        return new RestResponse<Hello>().data(hello);
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @Override
    public RestResponse badRequest(Exception e) {
        return new RestResponse(RestStatus.BAD_REQUEST, e.getMessage());
    }

}
