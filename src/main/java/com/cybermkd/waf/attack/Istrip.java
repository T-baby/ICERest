package com.cybermkd.waf.attack;

/**
 * 攻击过滤父类
 */
public interface Istrip {

    /**
     * @param value 待处理内容
     * @return
     * @Description 脚本内容剥离
     */
    public String strip(String value);
}
