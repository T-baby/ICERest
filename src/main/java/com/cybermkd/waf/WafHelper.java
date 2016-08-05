package com.cybermkd.waf;

import com.cybermkd.waf.attack.SqlInjection;
import com.cybermkd.waf.attack.XSS;

/**
 * Web防火墙工具类
 */
public class WafHelper {

    /**
     * @param value 待处理内容
     * @return
     * @Description 过滤XSS脚本内容
     */
    public static String stripXSS(String value) {
        if (value == null) {
            return null;
        }

        return new XSS().strip(value);
    }

    /**
     * @param value 待处理内容
     * @return
     * @Description 过滤SQL注入内容
     */
    public static String stripSqlInjection(String value) {
        if (value == null) {
            return null;
        }

        return new SqlInjection().strip(value);
    }

    /**
     * @param value 待处理内容
     * @return
     * @Description 过滤SQL/XSS注入内容
     */
    public static String stripSqlXSS(String value) {
        if (value == null) {
            return null;
        }

        return stripXSS(stripSqlInjection(value));
    }

}
