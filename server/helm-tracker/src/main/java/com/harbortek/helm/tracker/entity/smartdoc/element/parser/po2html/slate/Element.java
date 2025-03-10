package com.wangeditor.slate;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * @description Slate编辑器中的元素节点
 * @author wangfupeng (Java version translator)
 */
public class Element implements Descendant {
    private String type;
    private List<Descendant> children;
    private Map<String, Object> properties;
    
    /**
     * 构造函数
     */
    public Element() {
        this.properties = new HashMap<>();
    }
    
    /**
     * 构造函数
     * @param type 元素类型
     */
    public Element(String type) {
        this.type = type;
        this.properties = new HashMap<>();
    }
    
    /**
     * 获取元素类型
     * @return 元素类型
     */
    public String getType() {
        return type;
    }
    
    /**
     * 设置元素类型
     * @param type 元素类型
     */
    public void setType(String type) {
        this.type = type;
    }
    
    /**
     * 获取子节点列表
     * @return 子节点列表
     */
    public List<Descendant> getChildren() {
        return children;
    }
    
    /**
     * 设置子节点列表
     * @param children 子节点列表
     */
    public void setChildren(List<Descendant> children) {
        this.children = children;
    }
    
    /**
     * 获取属性映射
     * @return 属性映射
     */
    public Map<String, Object> getProperties() {
        return properties;
    }
    
    /**
     * 设置属性映射
     * @param properties 属性映射
     */
    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
    
    /**
     * 判断节点是否为元素节点
     * @param node 节点
     * @return 是否为元素节点
     */
    public static boolean isElement(Descendant node) {
        return node instanceof Element;
    }
}