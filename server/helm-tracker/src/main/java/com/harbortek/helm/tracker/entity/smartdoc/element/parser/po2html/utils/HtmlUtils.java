package com.wangeditor.utils;

/**
 * @description HTML工具类
 * @author wangfupeng (Java version translator)
 */
public class HtmlUtils {
    
    /**
     * 替换HTML特殊字符
     * @param text 原始文本
     * @return 替换后的文本
     */
    public static String replaceHtmlSpecialSymbols(String text) {
        if (text == null) return "";
        
        // 替换HTML特殊字符
        text = text.replace("&", "&amp;");
        text = text.replace("<", "&lt;");
        text = text.replace(">", "&gt;");
        text = text.replace(" ", "&nbsp;");
        text = text.replace("\"", "&quot;");
        
        return text;
    }
}