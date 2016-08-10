package com.cybermkd.route.valid;

import com.cybermkd.common.http.result.HttpStatus;
import com.cybermkd.common.util.Stringer;
import com.cybermkd.kit.MongoKit;
import com.cybermkd.kit.MongoValidate;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ice on 15-1-26.
 */
public class ValidResult {
    private Map<String, Object> errors = new HashMap<String, Object>();

    private HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;

    public ValidResult() {
    }

    public ValidResult(Map<String, Object> errors) {
        this.errors = errors;
    }

    public ValidResult(Map<String, Object> errors, HttpStatus status) {
        this.errors = errors;
        this.status = status;
    }

    public void addError(String name, Object error) {
        this.errors.put(name, error);
    }

    public Map<String, Object> getErrors() {
        return errors;
    }

    public void setErrors(Map<String, Object> errors) {
        this.errors = errors;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }


    public ValidResult mongoValid(MongoValidate validate,String... keys){
        return this.mongoValid(validate, "100",keys);
    }
    public ValidResult mongoValid(MongoValidate validate, Object errorValue,String... keys){
        String validateErrorMessage = MongoKit.validation(validate,keys);
        if(Stringer.isBlank(validateErrorMessage)){
            this.addError("error", errorValue);
            this.addError("errorMessage", validateErrorMessage);
        }
        return this;
    }

    public ValidResult mongoValid(MongoValidate validate){
        return this.mongoValid(validate,"100");
    }

    public ValidResult mongoValid(MongoValidate validate,Object errorValue){
        if (!validate.validation()){
            this.addError("error",errorValue);
            this.addError("errorMessage", validate.getErrorMessage());
        }
        return this;
    }

    public boolean isError(){
        return errors.size() > 0;
    }

}
