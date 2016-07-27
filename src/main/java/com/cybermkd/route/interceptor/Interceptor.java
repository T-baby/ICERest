package com.cybermkd.route.interceptor;


import com.cybermkd.route.core.RouteInvocation;

/**
 * Interceptor.
 */
public interface Interceptor {
  public void intercept(RouteInvocation ri);
}
