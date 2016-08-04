package com.cybermkd.waf.attack;

/**
 * SQL注入攻击
 */
public class SqlInjection implements Istrip {

	/**
	 * @Description SQL注入内容剥离
	 * @param value
	 * 				待处理内容
	 * @return
	 */
	public String strip(String value) {

		//剥离SQL注入部分代码
		return value.replaceAll("('.+--)|(--)|(\\|)|(%7C)", "");
	}
}
