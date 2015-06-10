package cn.hotdev.example.models.mss;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Created by andy on 6/10/15.
 */
@Component
public class MssCreateValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return MssCreate.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

    }
}
