package cn.hotdev.example.models.customer;

import cn.hotdev.example.models.address.Address;
import cn.hotdev.example.models.address.AddressValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.Set;

@Component
public class CustomerValidator implements Validator {

    private final AddressValidator addressValidator;

    @Autowired
    public CustomerValidator(AddressValidator addressValidator) {
        if (addressValidator == null) {
            throw new IllegalArgumentException("The supplied [AddressValidator] is required and must not be null.");
        }
        if (!addressValidator.supports(Address.class)) {
            throw new IllegalArgumentException("The supplied [AddressValidator] must support the validation of [Address] instances.");
        }
        this.addressValidator = addressValidator;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return Customer.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "customer.lastName.empty", "Customer LastName is empty");

        Customer customer = (Customer) o;
        if (customer.getAge() == null || customer.getAge() < 0) {
            errors.rejectValue("age", "customer.age.negative_value", "Customer Age is negative");
        }

        Set<Address> addresses = customer.getAddresses();

        if (addresses != null && !addresses.isEmpty()) {
            try {
                errors.pushNestedPath("addresses");
                for (Address address : customer.getAddresses())
                    ValidationUtils.invokeValidator(this.addressValidator, address, errors);
            } finally {
                errors.popNestedPath();
            }
        }
    }
}
