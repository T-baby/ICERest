//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.cybermkd.route.valid;

import com.cybermkd.common.http.result.ErrorResult;
import com.cybermkd.common.http.result.HttpStatus;
import com.cybermkd.common.util.Stringer;
import com.cybermkd.mongo.kit.MongoKit;
import com.cybermkd.mongo.kit.MongoValidate;

import java.util.ArrayList;
import java.util.List;

public class ValidResult {
    private List<ErrorResult> errors = new ArrayList<ErrorResult>();

    private HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;

    public ValidResult() {
    }

    public ValidResult(List<ErrorResult> errors) {
        this.errors = errors;
    }

    public ValidResult(HttpStatus status, List<ErrorResult> errors) {
        this.errors = errors;
        this.status = status;
    }

    public void addError(String name, String error) {
        this.errors.add(new ErrorResult(name, error));
    }

    public List<ErrorResult> getErrors() {
        return errors;
    }

    public void setErrors(List<ErrorResult> errors) {
        this.errors = errors;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public ValidResult mongoValid(MongoValidate validate) {
        return this.mongoValid(validate, "100", (String[])null);
    }

    public ValidResult mongoValid(MongoValidate validate, String errorValue) {
        return this.mongoValid(validate, errorValue, null);
    }

    public ValidResult mongoValid(MongoValidate validate, String... keys) {
        return this.mongoValid(validate, "100", keys);
    }

    public ValidResult mongoValid(MongoValidate validate, String errorValue, String[] keys) {
        String validateErrorMessage = "";
        if(keys != null && keys.length > 0) {
            validateErrorMessage = MongoKit.INSTANCE.validation(validate, keys);
        } else {
            validateErrorMessage = MongoKit.INSTANCE.validation(validate);
        }

        if(!Stringer.isBlank(validateErrorMessage)) {
            this.status = HttpStatus.BAD_REQUEST;
            this.addError("error", errorValue);
            this.addError("errorMessage", validateErrorMessage);
        }

        return this;
    }

    public boolean isError() {
        return this.errors.size() > 0;
    }
}
