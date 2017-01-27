package com.cybermkd.route.render;

import com.cybermkd.common.Render;
import com.cybermkd.common.http.ContentType;
import com.cybermkd.common.http.HttpRequest;
import com.cybermkd.common.http.HttpResponse;
import com.cybermkd.common.http.exception.HttpException;
import com.cybermkd.common.http.result.HttpStatus;
import com.cybermkd.common.http.result.ImageResult;
import com.cybermkd.log.Logger;

import java.awt.image.RenderedImage;

/**
 * Created by ice on 14-12-29.
 */
public class ImageRender extends Render {
    private static final Logger logger = Logger.getLogger(ImageRender.class);

    public void render(HttpRequest request, HttpResponse response, Object out) {
        if (out != null) {
            ImageResult<RenderedImage> result = null;
            if (out instanceof RenderedImage) {
                result = new ImageResult<RenderedImage>((RenderedImage) out);
            }

            if (result == null) {
                throw new HttpException(HttpStatus.NOT_FOUND, "Image not support '" + out + "'.");
            } else {
                response.setContentType(ContentType.typeOf(result.getType()).value());
                write(request, response, result.getType(), result.getResult());
            }
        }
    }
}
