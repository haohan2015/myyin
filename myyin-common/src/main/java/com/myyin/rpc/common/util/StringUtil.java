package com.myyin.rpc.common.util;

import org.apache.commons.lang3.StringUtils;

/**
 * @author admin
 * @Description: TODO
 * @date 2019/4/16 18:52
 */
public class StringUtil {

    /**
     * 判断字符串是否为空
     * @param str
     * @return
     */
    public static boolean isEmpty(String str){
        if(str != null){
            str = str.trim();
        }

        return StringUtils.isEmpty(str);
    }

    /**
     * 判断字符串是否为非空
     * @param str
     * @return
     */
    public static boolean isNotEmpty(String str){
        return !isEmpty(str);
    }

    /**
     * 分隔固定格式的字符串
     * @param str
     * @param separator
     * @return
     */
    public static String[] split(String str,String separator){
        return StringUtils.splitByWholeSeparator(str,separator);
    }

}
