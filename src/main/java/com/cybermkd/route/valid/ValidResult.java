//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.cybermkd.route.valid;

import com.cybermkd.common.http.result.HttpStatus;
import com.cybermkd.common.util.Stringer;
import com.cybermkd.mongo.kit.MongoKit;
import com.cybermkd.mongo.kit.MongoValidate;

import java.util.HashMap;
import java.util.Map;

public class ValidResult {
    private Map<String, Object> errors = new HashMap();
    private HttpStatus status;

    public ValidResult() {
        this.status = HttpStatus.UNPROCESSABLE_ENTITY;
    }

    public ValidResult(Map<String, Object> errors) {
        this.status = HttpStatus.UNPROCESSABLE_ENTITY;
        this.errors = errors;
    }

    public ValidResult(Map<String, Object> errors, HttpStatus status) {
        this.status = HttpStatus.UNPROCESSABLE_ENTITY;
        this.errors = errors;
        this.status = status;
    }

    public void addError(String name, Object error) {
        this.errors.put(name, error);
    }

    public Map<String, Object> getErrors() {
        return this.errors;
    }

    public void setErrors(Map<String, Object> errors) {
        this.errors = errors;
    }

    public HttpStatus getStatus() {
        return this.status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public ValidResult mongoValid(MongoValidate validate) {
        return this.mongoValid(validate, "100", (String[])null);
    }

    public ValidResult mongoValid(MongoValidate validate, Object errorValue) {
        return this.mongoValid(validate, errorValue, (String[])null);
    }

    public ValidResult mongoValid(MongoValidate validate, String... keys) {
        return this.mongoValid(validate, "100", keys);
    }

    public ValidResult mongoValid(MongoValidate validate, Object errorValue, String[] keys) {
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
