
package com.cybermkd.server;

import com.cybermkd.common.util.properties.Prop;
import com.cybermkd.log.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.webapp.WebAppContext;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;

/**
 * JettyServer is used to config and start jetty web server.
 * Jetty version 8.1.8
 */
class JettyServer implements IServer {


    private String webAppDir;
    private int port;
    private String context;
    private int scanIntervalSeconds;
    private boolean running = false;
    private Server server;
    private WebAppContext webApp;
    private static Logger logger = Logger.getLogger(JettyServer.class.getName());

    JettyServer(String webAppDir, int port, String context, int scanIntervalSeconds) {
        if (webAppDir == null) {
            throw new IllegalStateException("Invalid webAppDir of web server: " + webAppDir);
        }
        if (port < 0 || port > 65535) {
            throw new IllegalArgumentException("Invalid port of web server: " + port);
        }
        if (context == null && context.isEmpty()) {
            throw new IllegalStateException("Invalid context of web server: " + context);
        }

        this.webAppDir = webAppDir;
        this.port = port;
        this.context = context;
        this.scanIntervalSeconds = scanIntervalSeconds;
    }

    public void start() {
        if (!running) {
            try {
                running = true;
                doStart();
            } catch (Exception e) {
                System.err.println(e.getMessage());
                logger.error(e.getMessage(), e);
            }
        }
    }

    public void stop() {
        if (running) {
            try {
                server.stop();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            running = false;
        }
    }

    private void doStart() throws IOException {
        if (!available(port)) {
            throw new IllegalStateException("port: " + port + " already in use!");
        }

        server = new Server();
        SelectChannelConnector connector = new SelectChannelConnector();
        connector.setPort(port);
        server.addConnector(connector);
        webApp = new WebAppContext();
        webApp.setThrowUnavailableOnStartupException(true);    // 在启动过程中允许抛出异常终止启动并退出 JVM
        webApp.setContextPath(context);
        webApp.setResourceBase(webAppDir);    // webApp.setWar(webAppDir);
        webApp.setDescriptor(webAppDir + "/WEB-INF/web.xml");
        webApp.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "false");
        webApp.setInitParameter("org.eclipse.jetty.servlet.Default.useFileMappedBuffer", "false");

        server.setHandler(webApp);
        changeClassLoader(webApp);

        // configureScanner
        if (scanIntervalSeconds > 0) {
            Scanner scanner = new Scanner(Prop.getRootClassPath(), scanIntervalSeconds) {
                public void onChange() {
                    try {
                        System.err.println("\nLoading changes ......");
                        webApp.stop();
                        IceRestClassLoader loader = new IceRestClassLoader(webApp, getClassPath());
                        webApp.setClassLoader(loader);
                        webApp.start();
                        System.err.println("Loading complete.");
                    } catch (Exception e) {
                        System.err.println("Error reconfiguring/restarting webapp after change in watched files");
                        logger.error(e.getMessage(), e);
                    }
                }
            };
            System.out.println("Starting scanner at interval of " + scanIntervalSeconds + " seconds.");
            scanner.start();
        }

        try {
            System.out.println("Starting web server on port: " + port);
            server.start();
            server.join();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            System.exit(100);
        }
        return;
    }

    private void changeClassLoader(WebAppContext webApp) {
        try {
            String classPath = getClassPath();
            IceRestClassLoader jfcl = new IceRestClassLoader(webApp, classPath);
            jfcl.addClassPath(classPath);
            webApp.setClassLoader(jfcl);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private String getClassPath() {
        return System.getProperty("java.class.path");
    }


    private static boolean available(int port) {
        if (port <= 0) {
            throw new IllegalArgumentException("Invalid start port: " + port);
        }

        ServerSocket ss = null;
        DatagramSocket ds = null;
        try {
            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            ds = new DatagramSocket(port);
            ds.setReuseAddress(true);
            return true;
        } catch (IOException e) {
            logger.info(e.getMessage());
        } finally {
            if (ds != null) {
                ds.close();
            }

            if (ss != null) {
                try {
                    ss.close();
                } catch (IOException e) {
                    // should not be thrown, just detect port available.
                    logger.info(e.getMessage());
                }
            }
        }
        return false;
    }
}






