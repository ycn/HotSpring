package cn.hotdev.example.controllers.hello;

import cn.hotdev.example.internals.exceptions.BadRequestException;
import cn.hotdev.example.internals.exceptions.ErrorMessageException;
import cn.hotdev.example.models.customer.Customer;
import cn.hotdev.example.models.customer.CustomerRepository;
import cn.hotdev.example.models.error.Error;
import cn.hotdev.example.models.hello.Hello;
import cn.hotdev.example.viewmodels.error.ErrorView;
import cn.hotdev.example.viewmodels.hello.HelloView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

        if (name.equals("error_msg")) {
            throw new ErrorMessageException(new Error(123, "meet name 'error_msg'"));
        }

        Hello hello = new Hello(counter.longValue(), String.format(template, firstName, name), msg);

        return new HelloView(hello);
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorView helloGotBadRequestException(BadRequestException e) {
        return new ErrorView(new Error(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
    }

    @ExceptionHandler(ErrorMessageException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorView helloGotErrorMessageException(ErrorMessageException e) {
        return new ErrorView(e.getError());
    }
}
