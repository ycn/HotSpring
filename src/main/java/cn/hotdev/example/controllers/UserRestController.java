package cn.hotdev.example.controllers;

import cn.hotdev.example.models.address.Address;
import cn.hotdev.example.models.address.AddressRepository;
import cn.hotdev.example.models.address.AddressValidator;
import cn.hotdev.example.models.customer.Customer;
import cn.hotdev.example.models.customer.CustomerRepository;
import cn.hotdev.example.models.customer.CustomerValidator;
import cn.hotdev.example.models.exceptions.NotFoundException;
import cn.hotdev.example.models.rest.RestPage;
import cn.hotdev.example.models.rest.RestPagingResponse;
import cn.hotdev.example.models.rest.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserRestController extends BaseRestController {

    private final CustomerRepository customerRepository;
    private final CustomerValidator customerValidator;
    private final AddressRepository addressRepository;
    private final AddressValidator addressValidator;

    @Autowired
    public UserRestController(CustomerRepository customerRepository,
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

    @RequestMapping(method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public RestPagingResponse<List<Customer>> findAll(@RequestParam(value = "page", defaultValue = "0", required = false) int page,
                                                      @RequestParam(value = "count", defaultValue = "10", required = false) int count,
                                                      @RequestParam(value = "order", defaultValue = "ASC", required = false) Sort.Direction direction,
                                                      @RequestParam(value = "sort", defaultValue = "lastName", required = false) String sortProperty) {
        Page result = customerRepository.findAll(new PageRequest(page, count, new Sort(direction, sortProperty)));
        return new RestPagingResponse<List<Customer>>().data(result.getContent()).page(new RestPage(page, result));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public RestResponse<Customer> findOne(@PathVariable String id) throws NumberFormatException, NotFoundException {
        Customer customer = customerRepository.findOne(Long.parseLong(id));

        if (customer == null) {
            throw new NotFoundException("user {} not found", id);
        } else {
            return new RestResponse<Customer>().data(customer);
        }
    }

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    @ResponseStatus(value = HttpStatus.CREATED)
    public RestResponse<Customer> create(@RequestBody @Valid Customer newCustomer) {
        Customer customer = customerRepository.save(newCustomer);
        return new RestResponse<Customer>().data(customer);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    public RestResponse<Customer> replace(@PathVariable String id, @RequestBody @Valid Customer newCustomer) throws NumberFormatException, NotFoundException {
        Customer customer = customerRepository.findOne(Long.parseLong(id));

        if (customer != null) {
            newCustomer.setId(customer.getId());
            customerRepository.save(newCustomer);
            return new RestResponse<Customer>();
        } else {
            throw new NotFoundException("user {} not found", id);
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH, consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    public RestResponse<Customer> update(@PathVariable String id, @RequestBody Customer newCustomer) throws NumberFormatException, NotFoundException {
        Customer customer = customerRepository.findOne(Long.parseLong(id));

        if (customer != null) {
            customer.update(newCustomer);
            customerRepository.save(customer);
            return new RestResponse<Customer>();
        } else {
            throw new NotFoundException("user {} not found", id);
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "application/json;charset=UTF-8")
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    public RestResponse<Customer> delete(@PathVariable String id) throws NumberFormatException {
        customerRepository.delete(Long.parseLong(id));
        return new RestResponse<Customer>();
    }

    @RequestMapping(value = "/{id}/address", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public RestResponse<List<Address>> findAllAddress(@PathVariable String id) {
        Customer customer = customerRepository.findOne(Long.parseLong(id));

        if (customer != null) {
            List<Address> addressList = addressRepository.findByCustomer(customer);
            return new RestResponse<List<Address>>().data(addressList);

        } else {
            throw new NotFoundException("user {} not found", id);
        }

    }

    @RequestMapping(value = "/{id}/address", method = RequestMethod.POST, consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    public RestResponse<Address> addAddress(@PathVariable String id, @RequestBody @Valid Address newAddress) {
        Customer customer = customerRepository.findOne(Long.parseLong(id));

        if (customer != null) {

            newAddress.setCustomer(customer);
            Address address = addressRepository.save(newAddress);
            return new RestResponse<Address>().data(address);

        } else {
            throw new NotFoundException("user {} not found", id);
        }

    }
}
