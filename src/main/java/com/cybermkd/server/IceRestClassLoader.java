package com.cybermkd.server;

import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppClassLoader;
import org.eclipse.jetty.webapp.WebAppContext;

import java.io.File;
import java.io.IOException;

/**
 * ICEREST ClassLoader
 */
class IceRestClassLoader extends WebAppClassLoader {
    private boolean initialized = false;

    public IceRestClassLoader(WebAppContext context, String classPath) throws IOException {
        super(context);
        if (classPath != null) {
            String[] tokens = classPath.split(String.valueOf(File.pathSeparatorChar));
            for (String entry : tokens) {
                String path = entry;
                if (path.startsWith("-y-") || path.startsWith("-n-")) {
                    path = path.substring(3);
                }

                if (entry.startsWith("-n-") == false) {
                    super.addClassPath(path);
                }
            }
        }

        initialized = true;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public Class loadClass(String name) throws ClassNotFoundException {
        try {
            return loadClass(name, false);
        } catch (NoClassDefFoundError e) {
            throw new ClassNotFoundException(name);
        }
    }

    public void addClassPath(String classPath) throws IOException {
        if (initialized) {
            if (!classPath.endsWith("WEB-INF/classes/"))
                return;
        }
        super.addClassPath(classPath);
    }

    public void addJars(Resource jars) {
        if (initialized) {
            return;
        }
        super.addJars(jars);
    }
}






