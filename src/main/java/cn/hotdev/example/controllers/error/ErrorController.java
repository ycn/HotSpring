package cn.hotdev.example.controllers.error;

import cn.hotdev.example.models.error.Error;
import cn.hotdev.example.viewmodels.error.ErrorView;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class ErrorController implements org.springframework.boot.autoconfigure.web.ErrorController {

    private static final String PATH = "/error";

    @RequestMapping(value = PATH, method = RequestMethod.GET)
    public ErrorView home() {
        Error error = new Error(404, "Not Found");
        return new ErrorView(error);
    }

    @Override
    public String getErrorPath() {
        return PATH;
    }
}
