package cn.hotdev.example.controllers;

import cn.hotdev.example.constants.DefaultRestStatus;
import cn.hotdev.example.models.rest.RestResponse;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class ErrorRestController implements ErrorController {

    private static final String PATH = "/error";

    @RequestMapping(value = PATH, method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public RestResponse home() {
        return new RestResponse(DefaultRestStatus.NOT_FOUND, "Page Not Found");
    }

    @Override
    public String getErrorPath() {
        return PATH;
    }
}
