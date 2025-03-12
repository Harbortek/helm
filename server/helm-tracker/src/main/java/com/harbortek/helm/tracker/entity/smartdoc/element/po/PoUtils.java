package com.harbortek.helm.tracker.entity.smartdoc.element.po;

public class PoUtils {
    public static boolean isPlainText(String text) {
        // 移除所有空白字符
        text = text.trim();

        // 如果文本为空，返回true
        if (text.isEmpty()) {
            return true;
        }

        // 检查是否包含HTML标签
        // 如果文本以 < 开头且以 > 结尾，则可能是HTML标签
        if (text.startsWith("<") && text.endsWith(">")) {
            return false;
        }

        // 使用正则表达式检查是否包含HTML标签
        return !text.matches(".*<[^>]+>.*");
    }
}
