package com.cybermkd.route.valid;

import com.cybermkd.route.core.Params;
import com.cybermkd.route.core.RouteMatch;

/**
 * Created by ice on 15-1-26.
 */
public abstract class Validator {

  public abstract ValidResult validate(Params params,RouteMatch routeMatch);

}
