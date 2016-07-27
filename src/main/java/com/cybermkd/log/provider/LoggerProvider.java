package com.cybermkd.log.provider;

import com.cybermkd.log.Logger;

/**
 * Created by ice on 14-12-19.
 */
public interface LoggerProvider {
  public Logger getLogger(Class clazz);

  public Logger getLogger(String clazzName);

}
