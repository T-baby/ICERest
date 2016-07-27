package com.cybermkd.route.render;

import com.cybermkd.common.Render;
import com.cybermkd.common.http.ContentType;
import com.cybermkd.common.http.HttpRequest;
import com.cybermkd.common.http.HttpResponse;

/**
 * Created by ice on 14-12-29.
 */
public class TextRender extends Render {

  public void render(HttpRequest request, HttpResponse response, Object out) {
    if (out != null) {
      response.setContentType(ContentType.TEXT.value());
      write(request, response, out.toString());
    }
  }

}
