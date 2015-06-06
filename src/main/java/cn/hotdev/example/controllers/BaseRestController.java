package cn.hotdev.example.controllers;


import cn.hotdev.example.models.exceptions.BadRequestException;
import cn.hotdev.example.models.exceptions.NotFoundException;
import cn.hotdev.example.models.rest.RestResponse;
import cn.hotdev.example.models.rest.RestStatus;
import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public abstract class BaseRestController {

    @ExceptionHandler({NumberFormatException.class, BadRequestException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public RestResponse badRequest(Exception e) {
        return new RestResponse(RestStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public RestResponse notFound(Exception e) {
        return new RestResponse(RestStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public RestResponse<List<String>> notValid(MethodArgumentNotValidException e) {
        List<ObjectError> allErrors = e.getBindingResult().getAllErrors();
        List<String> errorMessages = new ArrayList<String>();
        if (allErrors != null && !allErrors.isEmpty()) {
            for (ObjectError err : allErrors) {
                errorMessages.add(err.getDefaultMessage());
            }
        }
        return new RestResponse<List<String>>(RestStatus.BAD_REQUEST, "request arguments is invalid").
                data(errorMessages);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public RestResponse otherException(Exception e) {
        return new RestResponse(RestStatus.INTERNAL_ERROR, e.getMessage());
    }
}
