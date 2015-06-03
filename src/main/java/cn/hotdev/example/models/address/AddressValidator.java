package cn.hotdev.example.models.address;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class AddressValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return Address.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "addr", "address.addr.empty", "Address Addr is empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "phone", "address.phone.empty", "Address Phone is empty");
    }
}
