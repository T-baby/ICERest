package com.cybermkd.filter;

import com.cybermkd.waf.request.WafRequestWrapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Waf防火墙过滤器
 */
public class WafFilter implements Filter {

    private static final Logger logger = Logger.getLogger("WafFilter");

    private static String OVER_URL = null;//非过滤地址

    private static boolean FILTER_XSS = true;//开启XSS脚本过滤

    private static boolean FILTER_SQL = true;//开启SQL注入过滤


    public void init(FilterConfig config) throws ServletException {
        //读取Web.xml配置地址
        OVER_URL = config.getInitParameter("whitelists");

        FILTER_XSS = getParamConfig(config.getInitParameter("filter_xss"));
        FILTER_SQL = getParamConfig(config.getInitParameter("filter_sql_injection"));
    }


    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        // HttpServletResponse res = (HttpServletResponse) response;

        boolean isOver = inContainURL(req, OVER_URL);

        /** 非拦截URL、直接通过. */
        if (!isOver) {
            try {
                //Request请求XSS过滤
                chain.doFilter(new WafRequestWrapper(req, FILTER_XSS, FILTER_SQL), response);
            } catch (Exception e) {
                logger.severe(" ICEREST WafFilter exception , requestURL: " + req.getRequestURL());
            }
            return;
        }

        chain.doFilter(request, response);
    }


    public void destroy() {
        logger.warning(" WafFilter destroy .");
    }


    /**
     * @param value 配置参数
     * @return 未配置返回 True
     * @Description 获取参数配置
     */
    private boolean getParamConfig(String value) {
        if (value == null || "".equals(value.trim())) {
            //未配置默认 True
            return true;
        }
        return new Boolean(value);
    }


    /**
     * <p>
     * getRequestURL是否包含在URL之内
     * </p>
     *
     * @param request
     * @param url     参数为以';'分割的URL字符串
     * @return
     */
    public static boolean inContainURL(HttpServletRequest request, String url) {
        boolean result = false;
        if (url != null && !"".equals(url.trim())) {
            String[] urlArr = url.split(";");
            StringBuffer reqUrl = new StringBuffer(request.getRequestURL());
            for (int i = 0; i < urlArr.length; i++) {
                if (reqUrl.indexOf(urlArr[i]) > 1) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }
}
