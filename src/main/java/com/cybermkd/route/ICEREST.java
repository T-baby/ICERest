/**
 * Copyright (c) 2011-2016, James Zhan 詹波 (jfinal@126.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cybermkd.route;

import com.cybermkd.route.config.Config;
import com.cybermkd.server.IServer;
import com.cybermkd.server.ServerFactory;

import javax.servlet.ServletContext;

/**
 * JFinal
 */
public final class ICEREST {


    private ServletContext servletContext;
    private String contextPath = "";
    private static IServer server;
    private static IceIniter iceIniter;

    private static final ICEREST me = new ICEREST();

    private ICEREST() {
    }

    public static ICEREST me() {
        return me;
    }

    public boolean init(Config config, ServletContext servletContext) {
        this.servletContext = servletContext;
        this.contextPath = servletContext.getContextPath();
        iceIniter = new IceIniter(config, servletContext);
        return true;
    }

    public IceIniter getRestIniter() {
        return iceIniter;
    }

    public static void start() {

        server = ServerFactory.getServer();
        server.start();
    }

    public static void start(String webAppDir, int port, String context, int scanIntervalSeconds) {
        server = ServerFactory.getServer(webAppDir, port, context, scanIntervalSeconds);
        server.start();
    }

    public static void stop() {
        server.stop();
    }

    public static void main(String[] args) {
        if (args == null || args.length == 0) {
            server = ServerFactory.getServer();
            server.start();
        } else {
            String webAppDir = args[0];
            int port = Integer.parseInt(args[1]);
            String context = args[2];
            int scanIntervalSeconds = Integer.parseInt(args[3]);
            server = ServerFactory.getServer(webAppDir, port, context, scanIntervalSeconds);
            server.start();
        }
    }
}










