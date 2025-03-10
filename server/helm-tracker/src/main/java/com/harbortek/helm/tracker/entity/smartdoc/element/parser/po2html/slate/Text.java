package com.wangeditor.slate;

import java.util.HashMap;
import java.util.Map;

/**
 * @description Slate编辑器中的文本节点
 * @author wangfupeng (Java version translator)
 */
public class Text implements Descendant {
    private String text;
    private Map<String, Object> marks;
    
    /**
     * 构造函数
     */
    public Text() {
        this.marks = new HashMap<>();
    }
    
    /**
     * 构造函数
     * @param text 文本内容
     */
    public Text(String text) {
        this.text = text;
        this.marks = new HashMap<>();
    }
    
    /**
     * 获取文本内容
     * @return 文本内容
     */
    public String getText() {
        return text;
    }
    
    /**
     * 设置文本内容
     * @param text 文本内容
     */
    public void setText(String text) {
        this.text = text;
    }
    
    /**
     * 获取标记映射
     * @return 标记映射
     */
    public Map<String, Object> getMarks() {
        return marks;
    }
    
    /**
     * 设置标记映射
     * @param marks 标记映射
     */
    public void setMarks(Map<String, Object> marks) {
        this.marks = marks;
    }
    
    /**
     * 判断节点是否为文本节点
     * @param node 节点
     * @return 是否为文本节点
     */
    public static boolean isText(Descendant node) {
        return node instanceof Text;
    }
}