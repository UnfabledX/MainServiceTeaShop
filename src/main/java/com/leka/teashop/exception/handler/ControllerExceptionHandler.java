package com.leka.teashop.exception.handler;

import com.leka.teashop.exception.UserAlreadyExistsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static com.leka.teashop.service.impl.UserServiceImpl.EMAIL_ALREADY_EXISTS;
import static com.leka.teashop.service.impl.UserServiceImpl.NAME_ALREADY_EXISTS;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public String handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        String whereToRedirect = "";
        if (ex.getMessage().equals(EMAIL_ALREADY_EXISTS)) {
            whereToRedirect = "redirect:/register?errorEmailExists";
        }
        if (ex.getMessage().equals(NAME_ALREADY_EXISTS)){
            whereToRedirect = "redirect:/register?errorUserExists";
        }
        return whereToRedirect;
    }

}
