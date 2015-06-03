package cn.hotdev.example.controllers.user;

import cn.hotdev.example.constants.ErrorStatus;
import cn.hotdev.example.internals.exceptions.BadRequestException;
import cn.hotdev.example.internals.exceptions.NotFoundException;
import cn.hotdev.example.internals.tools.Tools;
import cn.hotdev.example.models.address.Address;
import cn.hotdev.example.models.address.AddressRepository;
import cn.hotdev.example.models.address.AddressValidator;
import cn.hotdev.example.models.customer.Customer;
import cn.hotdev.example.models.customer.CustomerRepository;
import cn.hotdev.example.models.customer.CustomerValidator;
import cn.hotdev.example.viewmodels.error.ErrorView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {

    private final CustomerRepository customerRepository;
    private final CustomerValidator customerValidator;
    private final AddressRepository addressRepository;
    private final AddressValidator addressValidator;

    @Autowired
    public UserController(CustomerRepository customerRepository,
                          CustomerValidator customerValidator,
                          AddressRepository addressRepository,
                          AddressValidator addressValidator) {
        this.customerRepository = customerRepository;
        this.customerValidator = customerValidator;
        this.addressRepository = addressRepository;
        this.addressValidator = addressValidator;
    }

    @InitBinder("customer")
    protected void initCustomerBinder(WebDataBinder binder) {
        binder.setValidator(customerValidator);
    }

    @InitBinder("address")
    protected void initAddressBinder(WebDataBinder binder) {
        binder.setValidator(addressValidator);
    }

    @RequestMapping(method = RequestMethod.GET)
    public Iterable findAll(@RequestParam(value = "page", defaultValue = "0", required = false) int page,
                            @RequestParam(value = "count", defaultValue = "10", required = false) int count,
                            @RequestParam(value = "order", defaultValue = "ASC", required = false) Sort.Direction direction,
                            @RequestParam(value = "sort", defaultValue = "lastName", required = false) String sortProperty) {
        Page result = customerRepository.findAll(new PageRequest(page, count, new Sort(direction, sortProperty)));
        return result.getContent();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Customer findOne(@PathVariable String id) throws NumberFormatException, NotFoundException {
        Customer customer = customerRepository.findOne(Long.parseLong(id));

        if (customer == null) {
            throw new NotFoundException("user {} not found", id);
        }

        return customer;
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    public Customer create(@RequestBody @Valid Customer newCustomer) {
        return customerRepository.save(newCustomer);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public Customer replace(@PathVariable String id, @RequestBody @Valid Customer newCustomer) throws NumberFormatException, NotFoundException {
        Customer customer = customerRepository.findOne(Long.parseLong(id));

        if (customer != null) {
            newCustomer.setId(customer.getId());
            customer = customerRepository.save(newCustomer);
        } else {
            throw new NotFoundException("user {} not found", id);
        }

        return customer;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    public Customer update(@PathVariable String id, @RequestBody Customer newCustomer) throws NumberFormatException, NotFoundException {
        Customer customer = customerRepository.findOne(Long.parseLong(id));

        if (customer != null) {
            customer.update(newCustomer);
            customer = customerRepository.save(customer);
        } else {
            throw new NotFoundException("user {} not found", id);
        }

        return customer;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    public void delete(@PathVariable String id) throws NumberFormatException {
        customerRepository.delete(Long.parseLong(id));
    }

    @RequestMapping(value = "/{id}/address", method = RequestMethod.GET)
    public Iterable findAllAddress(@PathVariable String id) {
        Customer customer = customerRepository.findOne(Long.parseLong(id));

        if (customer != null) {

            return addressRepository.findByCustomer(customer);

        } else {
            throw new NotFoundException("user {} not found", id);
        }
    }

    @RequestMapping(value = "/{id}/address", method = RequestMethod.POST)
    public Address addAddress(@PathVariable String id, @RequestBody @Valid Address newAddress) {
        Customer customer = customerRepository.findOne(Long.parseLong(id));
        Address address;

        if (customer != null) {

            newAddress.setCustomer(customer);
            address = addressRepository.save(newAddress);

        } else {
            throw new NotFoundException("user {} not found", id);
        }

        return address;
    }


    @ExceptionHandler({NumberFormatException.class, BadRequestException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorView badRequest(Exception e) {
        return new ErrorView(new cn.hotdev.example.models.error.Error(ErrorStatus.BAD_REQUEST, e.getMessage()));
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorView notFound(NotFoundException e) {
        return new ErrorView(new cn.hotdev.example.models.error.Error(ErrorStatus.USER_NOT_FOUND, e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorView notValid(MethodArgumentNotValidException e) {
        String message = Tools.formatErrorList(e.getBindingResult().getAllErrors());
        return new ErrorView(new cn.hotdev.example.models.error.Error(ErrorStatus.BAD_REQUEST, message));
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorView otherException(Exception e) {
        return new ErrorView(new cn.hotdev.example.models.error.Error(ErrorStatus.INTERNAL_ERROR, e.getMessage()));
    }
}
