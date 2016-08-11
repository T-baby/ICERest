package com.cybermkd.route;

import com.cybermkd.route.config.Config;
import com.cybermkd.route.core.RouteBuilder;
import com.cybermkd.route.core.RouteHandler;
import com.cybermkd.route.handler.Handler;
import com.cybermkd.route.handler.HandlerFactory;

import javax.servlet.ServletContext;

/**
 * ICEREST
 */
public final class IceIniter {

    private ConfigIniter configIniter;
    private Handler handler;
    private ServletContext servletContext;
    private Config config;

    public IceIniter(Config config, ServletContext servletContext) {
        this.servletContext = servletContext;
        this.config = config;
        configIniter = new ConfigIniter(config);
        //build route
        RouteBuilder routeBuilder = new RouteBuilder(configIniter.getResourceLoader(), configIniter.getInterceptorLoader());
        routeBuilder.build();
        //add handler
        //must after route build
        Handler routeHandler = new RouteHandler(routeBuilder);
        handler = HandlerFactory.getHandler(configIniter.getHandlerLoader().getHandlerList(), routeHandler);
        //start job when config over
        config.afterStart();
    }

    public void stop() {
        config.beforeStop();
        configIniter.stopPlugins();
    }


    public ServletContext getServletContext() {
        return servletContext;
    }

    public Handler getHandler() {
        return handler;
    }
}










