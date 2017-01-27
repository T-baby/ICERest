package com.cybermkd.common.http.exception;


import com.cybermkd.common.http.HttpMessage;
import com.cybermkd.common.http.result.ErrorResult;
import com.cybermkd.common.http.result.HttpStatus;
import com.cybermkd.common.util.Lister;
import com.cybermkd.common.util.json.Jsoner;

import java.util.List;

/**
 * Created by ice on 14-12-19.
 * A HttpException can be raised to make resty return immediately an HTTP response with a specific HTTP status.
 */
public class HttpException extends RuntimeException {

  private final HttpStatus status;
  private final Object content;

  public HttpException(String key) {
    this(key, HttpMessage.getMessage(key));
  }

  public HttpException(HttpStatus status, String key) {
    this(status, key, HttpMessage.getMessage(key));
  }

  public HttpException(String key, String message) {
    this(HttpMessage.getStatus(key), key, message);
  }

  public HttpException(HttpStatus status, String key, String message) {
    this(status, Lister.<ErrorResult>of(new ErrorResult(key, message)));
  }


  public HttpException(HttpStatus status, List<ErrorResult> errors) {
    super(Jsoner.toJSON(errors));
    this.status = status;
    this.content = errors;
  }

  public HttpStatus getStatus() {
    return status;
  }

  /**
   * Returns the content to use in the HTTP response generated for this exception.
   * <p/>
   * Developer's note: override to provide a content different from the exception message.
   * Alternatively you can override the writeTo method for full control over the response.
   *
   * @return the content to use in the response.
   */
  public Object getContent() {
    return content != null ? content : getMessage();
  }

}
