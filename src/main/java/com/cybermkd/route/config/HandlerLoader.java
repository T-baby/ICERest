package com.cybermkd.route.config;


import com.cybermkd.route.handler.Handler;

import java.util.ArrayList;
import java.util.List;

/**
 * Handlers.
 */
final public class HandlerLoader {

  private final List<Handler> handlerList = new ArrayList<Handler>();

  public HandlerLoader add(Handler handler) {
    if (handler != null)
      handlerList.add(handler);
    return this;
  }

  public List<Handler> getHandlerList() {
    return handlerList;
  }
}
