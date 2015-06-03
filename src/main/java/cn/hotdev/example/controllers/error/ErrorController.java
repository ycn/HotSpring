package cn.hotdev.example.controllers.error;

import cn.hotdev.example.constants.ErrorStatus;
import cn.hotdev.example.models.error.Error;
import cn.hotdev.example.viewmodels.error.ErrorView;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class ErrorController implements org.springframework.boot.autoconfigure.web.ErrorController {

    private static final String PATH = "/error";

    @RequestMapping(value = PATH, method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorView home() {
        Error error = new Error(ErrorStatus.PAGE_NOT_FOUND, "Page Not Found");
        return new ErrorView(error);
    }

    @Override
    public String getErrorPath() {
        return PATH;
    }
}
