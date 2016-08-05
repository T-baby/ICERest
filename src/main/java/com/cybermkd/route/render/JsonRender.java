package com.cybermkd.route.render;

import com.cybermkd.common.Render;
import com.cybermkd.common.http.ContentType;
import com.cybermkd.common.http.HttpRequest;
import com.cybermkd.common.http.HttpResponse;
import com.cybermkd.common.util.json.Jsoner;

/**
 * Created by ice on 14-12-29.
 *
 * @JsonerFiled(serialize=false)
 */
public class JsonRender extends Render {

    public void render(HttpRequest request, HttpResponse response, Object out) {
        if (out != null) {
            response.setContentType(ContentType.JSON.value());
            if (out instanceof String) {
                if (Jsoner.isJson((String) out)) {
                    write(request, response, (String) out);
                } else {
                    write(request, response, "\"" + out + "\"");
                }
            } else {
                String json = Jsoner.toJSON(out);
                write(request, response, json);
            }
        }
    }
}
