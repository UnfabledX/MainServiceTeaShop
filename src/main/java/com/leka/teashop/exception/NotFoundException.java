package com.leka.teashop.exception;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class NotFoundException extends RuntimeException{

    private String value;

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, String value) {
        super(message);
        this.value = value;
    }

    public String getWrongValue(){
        return value;
    }
}
