package com.cybermkd.route.holder;

import com.cybermkd.common.Constant;
import com.cybermkd.common.Render;
import com.cybermkd.common.http.HttpRequest;
import com.cybermkd.common.http.HttpResponse;
import com.cybermkd.common.http.exception.HttpException;
import com.cybermkd.common.http.result.HttpStatus;
import com.cybermkd.common.util.json.Jsoner;
import com.cybermkd.log.Logger;
import com.cybermkd.route.render.RenderFactory;

/**
 * Created by Dreampie on 15/4/27.
 */
public class DefaultExceptionHolder extends ExceptionHolder {
    private final static Logger logger = Logger.getLogger(DefaultExceptionHolder.class);

    public void hold(HttpRequest request, HttpResponse response, Exception exception, boolean[] isHandled) {
        String restPath = request.getRestPath();
        Render render = RenderFactory.getByUrl(restPath);
        String message;
        if (exception instanceof HttpException) {
            HttpException webException = (HttpException) exception;
            //api访问 所有的异常 以httpStatus返回
            if (Constant.apiPrefix == null || restPath.startsWith(Constant.apiPrefix)) {
                message = Jsoner.toJSON(webException.getContent());
                if (logger.isWarnEnabled()) {
                    logger.warn("Request \"" + request.getHttpMethod() + " " + request.getRestPath() + "\" error : " + webException.getStatus().getCode() + " " + message);
                }
                response.setStatus(webException.getStatus());
                render.render(request, response, message);
            } else {
                //其他访问  跳转到 指定页面
                go(response, webException.getStatus(), isHandled);
            }
        } else {
            message = exception.getMessage();
            if (message == null) {
                Throwable throwable = exception.getCause();
                if (throwable != null) {
                    message = throwable.getMessage();
                }
            }
            if (logger.isErrorEnabled()) {
                logger.warn("Request \"" + request.getHttpMethod() + " " + request.getRestPath() + "\" error : " + HttpStatus.BAD_REQUEST.getCode() + " " + message, exception);
            }
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            render.render(request, response, message);
        }
    }
}
