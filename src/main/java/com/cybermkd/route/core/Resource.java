package com.cybermkd.route.core;


import com.cybermkd.common.Constant;
import com.cybermkd.common.http.HttpRequest;
import com.cybermkd.common.http.HttpResponse;

/**
 * Resource
 */
public abstract class Resource {

    private RouteMatch routeMatch;

    final void setRouteMatch(RouteMatch routeMatch) {
        this.routeMatch = routeMatch;
    }

    final public String getPath() {
        return routeMatch.getPath();
    }

    final public Params getParams() {
        return routeMatch.getParams();
    }

    /**
     * Get param of any type.
     */
    final public <T> T getParam(String name) {
        return (T) (getParams().get(name));
    }

    final public HttpRequest getRequest() {
        return routeMatch.getRequest();
    }

    final public HttpResponse getResponse() {
        return routeMatch.getResponse();
    }

    final public String getCookie(String key) {
        return getRequest().getCookieValue(key);
    }

    final public void setCookie(String key, String value) {
        getResponse().addCookie(key, value);
    }

    final public void setCookie(String key, String value, int age) {
        getResponse().addCookie(key, value, age);
    }

    final public void setCookie(String key, String value, int age, String domain) {
        if (Constant.devEnable) {
            getResponse().addCookie(key, value, age);
        } else {
            getResponse().addCookie(key, value, age, false);
        }

    }

    final public void delCookie(String key) {
        getResponse().clearCookie(key);
    }

}


