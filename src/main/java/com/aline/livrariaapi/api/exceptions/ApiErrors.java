package com.aline.livrariaapi.api.exceptions;

import com.aline.livrariaapi.exception.BusinessException;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ApiErrors {

    List<String> errors;

    public List<String> getErrors() {
        return errors;
    }

    public ApiErrors(BusinessException e) {
        this.errors = Arrays.asList(e.getMessage());
    }

    public ApiErrors(BindingResult bindingResult) {
        this.errors = new ArrayList<>();
        bindingResult.getAllErrors().forEach(error -> this.errors.add(error.getDefaultMessage()));
    }
}
