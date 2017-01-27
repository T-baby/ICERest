package com.cybermkd.route.core;

import com.cybermkd.common.http.HttpRequest;
import com.cybermkd.common.http.HttpResponse;
import com.cybermkd.common.http.exception.HttpException;
import com.cybermkd.common.http.result.ErrorResult;
import com.cybermkd.common.http.result.HttpResult;
import com.cybermkd.common.http.result.HttpStatus;
import com.cybermkd.common.spring.SpringBuilder;
import com.cybermkd.common.spring.SpringHolder;
import com.cybermkd.log.Logger;
import com.cybermkd.route.interceptor.Interceptor;
import com.cybermkd.route.render.RenderFactory;
import com.cybermkd.route.valid.ValidResult;
import com.cybermkd.route.valid.Validator;

import javax.servlet.http.Cookie;
import java.awt.image.RenderedImage;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.cybermkd.common.util.Checker.checkNotNull;

/**
 * ActionInvocation invoke the action
 */
public class RouteInvocation {

    private final static Logger logger = Logger.getLogger(RouteInvocation.class);
    private Route route;
    private RouteMatch routeMatch;
    private Interceptor[] interceptors;
    private int index = 0;
    private boolean wasInvoke = false;
    private Object invokeResult = null;

    // ActionInvocationWrapper need this constructor
    private RouteInvocation() {

    }

    public RouteInvocation(Route route, RouteMatch routeMatch) {
        this.route = route;
        this.routeMatch = routeMatch;
        this.interceptors = route.getInterceptors();
    }

    /**
     * Invoke the route.
     */
    private void methodInvoke() {
        if (index < interceptors.length) {
            interceptors[index++].intercept(this);
        } else if (index++ == interceptors.length) {
            Resource resource;
            try {
                //初始化resource
                if (SpringHolder.alive) {
                    resource = SpringBuilder.getBean(route.getResourceClass());
                } else {
                    resource = route.getResourceClass().newInstance();
                }

                checkNotNull(resource, "Could init '" + route.getResourceClass() + "' before invoke method.");
                resource.setRouteMatch(routeMatch);
                //获取所有参数
                Params params = routeMatch.getParams();

                //数据验证
                validate(params);
                Method method = route.getMethod();
                method.setAccessible(true);
                //执行方法
                if (route.getAllParamNames().size() > 0) {
                    List<Class<?>> allParamTypes = route.getAllParamTypes();
                    List<String> allParamNames = route.getAllParamNames();
                    //执行方法的参数
                    Object[] args = new Object[allParamNames.size()];
                    int i = 0;
                    for (String name : allParamNames) {
                        if (HttpRequest.class.isAssignableFrom(allParamTypes.get(i))) {
                            args[i++] = routeMatch.getRequest();
                        } else if (HttpResponse.class.isAssignableFrom(allParamTypes.get(i))) {
                            args[i++] = routeMatch.getResponse();
                        } else if (Headers.class.isAssignableFrom(allParamTypes.get(i))) {
                            args[i++] = routeMatch.getHeaders();
                        } else if (Cookies.class.isAssignableFrom(allParamTypes.get(i))) {
                            args[i++] = routeMatch.getCookies();
                        } else if (Params.class.isAssignableFrom(allParamTypes.get(i))) {
                            args[i++] = routeMatch.getParams();
                        } else {
                            args[i++] = params.get(name);
                        }
                    }
                    invokeResult = method.invoke(resource, args);
                } else {
                    invokeResult = method.invoke(resource);
                }
                wasInvoke = true;
                //输出结果
                render(invokeResult);
            } catch (Exception e) {
                if (e instanceof InvocationTargetException) {
                    route.throwException(e.getCause());
                } else {
                    route.throwException(e);
                }
            }
        }
    }


    /**
     * 输出内容
     *
     * @param invokeResult invokeResult
     */
    public void render(Object invokeResult) {
        Object result;
        HttpRequest request = routeMatch.getRequest();
        HttpResponse response = routeMatch.getResponse();
        //通过特定的httpResult返回并携带状态码
        if (invokeResult instanceof HttpResult) {
            HttpResult httpResult = (HttpResult) invokeResult;
            response.setStatus(httpResult.getStatus());
            Map<String, String> headers = httpResult.getHeaders();
            if (headers != null && headers.size() > 0) {
                for (Map.Entry<String, String> headersEntry : headers.entrySet()) {
                    response.setHeader(headersEntry.getKey(), headersEntry.getValue());
                }
            }
            List<Cookie> cookies = httpResult.getCookies();
            if (cookies != null && cookies.size() > 0) {
                for (Cookie cookie : cookies) {
                    response.addCookie(cookie);
                }
            }

            result = httpResult.getResult();
        } else {
            result = invokeResult;
        }
        String extension = routeMatch.getExtension();
        //file render
        if ((result instanceof File && extension.equals("")) || extension.equals(RenderFactory.FILE)) {
            RenderFactory.getFileRender().render(request, response, result);
        } else if ((result instanceof RenderedImage && extension.equals("")) || extension.equals(RenderFactory.IMAGE)) {
            //如果是string  表示为文件类型
            if (result instanceof String) {
                RenderFactory.getFileRender().render(request, response, result);
            } else {
                RenderFactory.getImageRender().render(request, response, result);
            }
        } else {
            RenderFactory.get(extension).render(request, response, result);
        }
    }

    /**
     * 请求参数验证
     *
     * @param params 参数
     */
    private void validate(Params params) {
        Validator[] validators = route.getValidators();

        if (validators.length > 0) {
            List<ErrorResult> errors = new ArrayList<ErrorResult>();
            HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
            ValidResult vr;

            for (Validator validator : validators) {
                //数据验证
                vr = validator.validate(params, routeMatch);
                errors.addAll(vr.getErrors());
                if (!status.equals(vr.getStatus()))
                    status = vr.getStatus();
            }

            if (errors.size() > 0) {
                throw new HttpException(status, errors);
            }
        }
    }

    public Method getMethod() {
        return route.getMethod();
    }

    public Class getResourceClass() {
        return route.getResourceClass();
    }

    public RouteMatch getRouteMatch() {
        return routeMatch;
    }

    public Object invoke() {
        if (!wasInvoke) {
            methodInvoke();
        }
        return invokeResult;
    }
}
