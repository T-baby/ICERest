//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.cybermkd.route.handler.cors;

import com.cybermkd.common.http.HttpRequest;
import com.cybermkd.common.http.HttpResponse;
import com.cybermkd.common.http.exception.HttpException;
import com.cybermkd.common.http.result.HttpStatus;
import com.cybermkd.common.util.Joiner;
import com.cybermkd.common.util.Lister;
import com.cybermkd.log.Logger;
import com.cybermkd.route.handler.Handler;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CORSHandler extends Handler {
    public static final String ACCESS_CONTROL_REQUEST_METHOD_HEADER = "Access-Control-Request-Method";
    public static final String ACCESS_CONTROL_REQUEST_HEADERS_HEADER = "Access-Control-Request-Headers";
    public static final String ACCESS_CONTROL_ALLOW_ORIGIN_HEADER = "Access-Control-Allow-Origin";
    public static final String ACCESS_CONTROL_ALLOW_METHODS_HEADER = "Access-Control-Allow-Methods";
    public static final String ACCESS_CONTROL_ALLOW_HEADERS_HEADER = "Access-Control-Allow-Headers";
    public static final String ACCESS_CONTROL_MAX_AGE_HEADER = "Access-Control-Max-Age";
    public static final String ACCESS_CONTROL_ALLOW_CREDENTIALS_HEADER = "Access-Control-Allow-Credentials";
    public static final String ACCESS_CONTROL_EXPOSE_HEADERS_HEADER = "Access-Control-Expose-Headers";
    private static final Logger logger = Logger.getLogger(CORSHandler.class);
    private static final String ORIGIN_HEADER = "Origin";
    private static final List<String> SIMPLE_HTTP_METHODS = Lister.of(new Object[]{"GET", "POST", "HEAD"});
    private boolean anyOriginAllowed;
    private boolean anyHeadersAllowed;
    private List<String> allowedOrigins;
    private List<String> allowedMethods;
    private List<String> allowedHeaders;
    private List<String> exposedHeaders;
    private int preflightMaxAge;
    private boolean allowCredentials;
    private boolean chainPreflight;

    public CORSHandler() {
        this.anyOriginAllowed = true;
        this.anyHeadersAllowed = false;
        this.allowedOrigins = Lister.of(new Object[]{"*"});
        this.allowedMethods = Lister.of(new Object[]{"GET", "POST", "HEAD"});
        this.allowedHeaders = Lister.of(new Object[]{"X-Requested-With", "Content-Type", "Accept", "Origin"});
        this.exposedHeaders = null;
        this.preflightMaxAge = 1800;
        this.allowCredentials = true;
        this.chainPreflight = true;
    }

    public CORSHandler(String allowedMethods) {
        this((String)null, allowedMethods, (String)null);
    }

    public CORSHandler(String allowedMethods, String allowedHeaders) {
        this((String)null, allowedMethods, allowedHeaders);
    }

    public CORSHandler(String allowedOrigins, String allowedMethods, String allowedHeaders) {
        this(allowedOrigins, allowedMethods, allowedHeaders, (String)null);
    }

    public CORSHandler(String allowedOrigins, String allowedMethods, String allowedHeaders, String exposedHeaders) {
        this.anyOriginAllowed = true;
        this.anyHeadersAllowed = false;
        this.allowedOrigins = Lister.of(new Object[]{"*"});
        this.allowedMethods = Lister.of(new Object[]{"GET", "POST", "HEAD"});
        this.allowedHeaders = Lister.of(new Object[]{"X-Requested-With", "Content-Type", "Accept", "Origin"});
        this.exposedHeaders = null;
        this.preflightMaxAge = 1800;
        this.allowCredentials = true;
        this.chainPreflight = true;
        if(allowedOrigins != null) {
            this.allowedOrigins = Lister.of(allowedOrigins.split(","));
        }

        if(allowedMethods != null) {
            this.allowedMethods = Lister.of(allowedMethods.split(","));
        }

        if(allowedHeaders != null) {
            this.allowedHeaders = Lister.of(allowedHeaders.split(","));
        }

        if(exposedHeaders != null) {
            this.exposedHeaders = Lister.of(exposedHeaders.split(","));
        }

    }

    public final void handle(HttpRequest request, HttpResponse response, boolean[] isHandled) {
        String origin = request.getHeader("Origin");
        if(origin != null && this.isEnabled(request)) {
            if(this.originMatches(origin)) {
                if(this.isSimpleRequest(request)) {
                    logger.debug("Cross-origin request to %s is a simple cross-origin request", new Object[]{request.getRestPath()});
                    this.handleSimpleResponse(request, response, origin);
                } else if(this.isPreflightRequest(request)) {
                    logger.debug("Cross-origin request to %s is a preflight cross-origin request", new Object[]{request.getRestPath()});
                    this.handlePreflightResponse(request, response, origin);
                    if(!this.chainPreflight) {
                        throw new HttpException(HttpStatus.FORBIDDEN, "Unauthorized CORS request");
                    }

                    logger.debug("Preflight cross-origin request to %s forwarded to application", new Object[]{request.getRestPath()});
                } else {
                    logger.debug("Cross-origin request to %s is a non-simple cross-origin request", new Object[]{request.getRestPath()});
                    this.handleSimpleResponse(request, response, origin);
                }
            } else {
                logger.debug("Cross-origin request to " + request.getRestPath() + " with origin " + origin + " does not match allowed origins " + this.allowedOrigins);
            }
        }

        this.nextHandler.handle(request, response, isHandled);
    }

    protected boolean isEnabled(HttpRequest request) {
        Enumeration connections = request.getHeaders("Connection");

        while(true) {
            String connection;
            do {
                if(!connections.hasMoreElements()) {
                    return true;
                }

                connection = (String)connections.nextElement();
            } while(!"Upgrade".equalsIgnoreCase(connection));

            Enumeration upgrades = request.getHeaders("Upgrade");

            while(upgrades.hasMoreElements()) {
                String upgrade = (String)upgrades.nextElement();
                if("WebSocket".equalsIgnoreCase(upgrade)) {
                    return false;
                }
            }
        }
    }

    private boolean originMatches(String originList) {
        if(this.anyOriginAllowed) {
            return true;
        } else if(originList.trim().length() == 0) {
            return false;
        } else {
            String[] origins = originList.split(" ");
            String[] var3 = origins;
            int var4 = origins.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                String origin = var3[var5];
                if(origin.trim().length() != 0) {
                    Iterator var7 = this.allowedOrigins.iterator();

                    while(var7.hasNext()) {
                        String allowedOrigin = (String)var7.next();
                        if(allowedOrigin.contains("*")) {
                            Matcher matcher = this.createMatcher(origin, allowedOrigin);
                            if(matcher.matches()) {
                                return true;
                            }
                        } else if(allowedOrigin.equals(origin)) {
                            return true;
                        }
                    }
                }
            }

            return false;
        }
    }

    private Matcher createMatcher(String origin, String allowedOrigin) {
        String regex = this.parseAllowedWildcardOriginToRegex(allowedOrigin);
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(origin);
    }

    private String parseAllowedWildcardOriginToRegex(String allowedOrigin) {
        String regex = allowedOrigin.replace(".", "\\.");
        return regex.replace("*", ".*");
    }

    private boolean isSimpleRequest(HttpRequest request) {
        return SIMPLE_HTTP_METHODS.contains(request.getHttpMethod())?request.getHeader("Access-Control-Request-Method") == null:false;
    }

    private boolean isPreflightRequest(HttpRequest request) {
        return "OPTIONS".equalsIgnoreCase(request.getHttpMethod())?true:request.getHeader("Access-Control-Request-Method") != null;
    }

    private void handleSimpleResponse(HttpRequest request, HttpResponse response, String origin) {
        response.setHeader("Access-Control-Allow-Origin", origin);
        /*允许附带cookie*/
        response.setHeader("Access-Control-Allow-Credentials","true");
        if(!this.anyOriginAllowed) {
            response.addHeader("Vary", "Origin");
        }

        if(this.allowCredentials) {
            response.setHeader("Access-Control-Allow-Credentials", "true");
        }

        if(this.exposedHeaders != null && !this.exposedHeaders.isEmpty()) {
            response.setHeader("Access-Control-Expose-Headers", Joiner.on(",").join(this.exposedHeaders));
        }

    }

    private void handlePreflightResponse(HttpRequest request, HttpResponse response, String origin) {
        boolean methodAllowed = this.isMethodAllowed(request);
        if(methodAllowed) {
            List headersRequested = this.getAccessControlRequestHeaders(request);
            boolean headersAllowed = this.areHeadersAllowed(headersRequested);
            if(headersAllowed) {
                response.setHeader("Access-Control-Allow-Origin", origin);
                  /*允许附带cookie*/
                response.setHeader("Access-Control-Allow-Credentials","true");
                if(!this.anyOriginAllowed) {
                    response.addHeader("Vary", "Origin");
                }

                if(this.allowCredentials) {
                    response.setHeader("Access-Control-Allow-Credentials", "true");
                }

                if(this.preflightMaxAge > 0) {
                    response.setHeader("Access-Control-Max-Age", String.valueOf(this.preflightMaxAge));
                }

                response.setHeader("Access-Control-Allow-Methods", Joiner.on(",").join(this.allowedMethods));
                if(this.anyHeadersAllowed) {
                    response.setHeader("Access-Control-Allow-Headers", Joiner.on(",").join(headersRequested));
                } else {
                    response.setHeader("Access-Control-Allow-Headers", Joiner.on(",").join(this.allowedHeaders));
                }

            }
        }
    }

    private boolean isMethodAllowed(HttpRequest request) {
        String accessControlRequestMethod = request.getHeader("Access-Control-Request-Method");
        logger.debug("%s is %s", new Object[]{"Access-Control-Request-Method", accessControlRequestMethod});
        boolean result = false;
        if(accessControlRequestMethod != null) {
            result = this.allowedMethods.contains(accessControlRequestMethod);
        }

        logger.debug("Method %s is" + (result?"":" not") + " among allowed methods %s", new Object[]{accessControlRequestMethod, this.allowedMethods});
        return result;
    }

    List<String> getAccessControlRequestHeaders(HttpRequest request) {
        String accessControlRequestHeaders = request.getHeader("Access-Control-Request-Headers");
        logger.debug("%s is %s", new Object[]{"Access-Control-Request-Headers", accessControlRequestHeaders});
        if(accessControlRequestHeaders == null) {
            return Lister.of(new Object[0]);
        } else {
            List requestedHeaders = Lister.of(new Object[0]);
            String[] headers = accessControlRequestHeaders.split(",");
            String[] var5 = headers;
            int var6 = headers.length;

            for(int var7 = 0; var7 < var6; ++var7) {
                String header = var5[var7];
                String h = header.trim();
                if(h.length() > 0) {
                    requestedHeaders.add(h);
                }
            }

            return requestedHeaders;
        }
    }

    private boolean areHeadersAllowed(List<String> requestedHeaders) {
        if(this.anyHeadersAllowed) {
            logger.debug("Any header is allowed");
            return true;
        } else {
            boolean result = true;
            Iterator var3 = requestedHeaders.iterator();

            while(var3.hasNext()) {
                String requestedHeader = (String)var3.next();
                boolean headerAllowed = false;
                Iterator var6 = this.allowedHeaders.iterator();

                while(var6.hasNext()) {
                    String allowedHeader = (String)var6.next();
                    if(requestedHeader.equalsIgnoreCase(allowedHeader.trim())) {
                        headerAllowed = true;
                        break;
                    }
                }

                if(!headerAllowed) {
                    result = false;
                    break;
                }
            }

            logger.debug("Headers [%s] are" + (result?"":" not") + " among allowed headers %s", new Object[]{requestedHeaders, this.allowedHeaders});
            return result;
        }
    }

    public List<String> getAllowedOrigins() {
        return this.allowedOrigins;
    }

    public void setAllowedOrigins(String... allowedOrigins) {
        if(allowedOrigins.length == 1 && allowedOrigins[0].equals("*")) {
            this.anyOriginAllowed = true;
        }

        this.allowedOrigins = Lister.of(allowedOrigins);
    }

    public List<String> getAllowedMethods() {
        return this.allowedMethods;
    }

    public void setAllowedMethods(String... allowedMethods) {
        this.allowedMethods = Lister.of(allowedMethods);
    }

    public List<String> getAllowedHeaders() {
        return this.allowedHeaders;
    }

    public void setAllowedHeaders(String... allowedHeaders) {
        if(allowedHeaders.length == 1 && allowedHeaders[0].equals("*")) {
            this.anyHeadersAllowed = true;
        }

        this.allowedHeaders = Lister.of(allowedHeaders);
    }

    public List<String> getExposedHeaders() {
        return this.exposedHeaders;
    }

    public void setExposedHeaders(String... exposedHeaders) {
        this.exposedHeaders = Lister.of(exposedHeaders);
    }

    public int getPreflightMaxAge() {
        return this.preflightMaxAge;
    }

    public void setPreflightMaxAge(int preflightMaxAge) {
        this.preflightMaxAge = preflightMaxAge;
    }

    public boolean isAllowCredentials() {
        return this.allowCredentials;
    }

    public void setAllowCredentials(boolean allowCredentials) {
        this.allowCredentials = allowCredentials;
    }

    public boolean isChainPreflight() {
        return this.chainPreflight;
    }

    public void setChainPreflight(boolean chainPreflight) {
        this.chainPreflight = chainPreflight;
    }
}
