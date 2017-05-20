package com.jinr.core.utils;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 自动填写短信验证码 正则表达式
 *
 * @author CQJ
 */
public class AutoSmsUtil {
    private String patternCoder = "(?<!\\d)\\d{6}(?!\\d)";
    private static AutoSmsUtil aUtil;

    public static AutoSmsUtil getInstance() {
        if (aUtil == null) {
            aUtil = new AutoSmsUtil();
        }
        return aUtil;
    }

    /**
     * 匹配短信中间的6个数字（验证码等）
     *
     * @param patternContent
     * @return
     */
    public String patternCode(String patternContent) {
        if (TextUtils.isEmpty(patternContent)) {
            return null;
        }
        Pattern p = Pattern.compile(patternCoder);
        Matcher matcher = p.matcher(patternContent);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }
}
