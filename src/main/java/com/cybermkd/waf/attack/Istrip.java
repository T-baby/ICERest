package com.cybermkd.waf.attack;

/**
 * 攻击过滤父类
 */
public interface Istrip {

	/**
	 * @Description 脚本内容剥离
	 * @param value
	 * 				待处理内容
	 * @return
	 */
	public String strip(String value);
}
