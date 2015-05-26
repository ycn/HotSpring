package cn.hotdev.example.controllers.hello;

import cn.hotdev.example.models.customer.Customer;
import cn.hotdev.example.models.customer.CustomerRepository;
import cn.hotdev.example.models.hello.Hello;
import cn.hotdev.example.viewmodels.hello.HelloView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.atomic.LongAdder;


@RestController
@RequestMapping("/hello")
public class HelloController {

    @Autowired
    private CustomerRepository customerRepository;

    private static final String template = "Hello, %s %s!";
    private final LongAdder counter = new LongAdder();

    @RequestMapping(value = "/{name}", method = RequestMethod.GET)
    public HelloView helloWithName(@PathVariable("name") String name,
                                   @RequestParam(value = "msg", required = false, defaultValue = "Welcome!") String msg
    ) {
        counter.increment();

        List<Customer> customers = customerRepository.findByLastName(name);

        String firstName = "Unknown";

        if (customers != null && !customers.isEmpty()) {
            Customer customer = customers.get(0);
            firstName = customer.getFirstName();
        }

        Hello hello = new Hello(counter.longValue(), String.format(template, firstName, name), msg);

        return new HelloView(hello);
    }
}
