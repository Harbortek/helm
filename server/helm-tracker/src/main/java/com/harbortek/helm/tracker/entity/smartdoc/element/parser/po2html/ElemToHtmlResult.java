package com.wangeditor.html;

/**
 * @description 元素转HTML结果类，对应TypeScript中的IElemToHtmlRes接口
 * @author wangfupeng (Java version translator)
 */
public class ElemToHtmlResult {
    private String html;
    private String prefix;
    private String suffix;
    
    /**
     * 创建一个HTML结果对象
     * @param html HTML内容
     */
    public ElemToHtmlResult(String html) {
        this.html = html;
    }
    
    /**
     * 创建一个带前后缀的HTML结果对象
     * @param html HTML内容
     * @param prefix 前缀
     * @param suffix 后缀
     */
    public ElemToHtmlResult(String html, String prefix, String suffix) {
        this.html = html;
        this.prefix = prefix;
        this.suffix = suffix;
    }
    
    /**
     * 获取HTML内容
     * @return HTML字符串
     */
    public String getHtml() {
        return html;
    }
    
    /**
     * 设置HTML内容
     * @param html HTML字符串
     */
    public void setHtml(String html) {
        this.html = html;
    }
    
    /**
     * 获取前缀
     * @return 前缀字符串
     */
    public String getPrefix() {
        return prefix;
    }
    
    /**
     * 设置前缀
     * @param prefix 前缀字符串
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
    
    /**
     * 获取后缀
     * @return 后缀字符串
     */
    public String getSuffix() {
        return suffix;
    }
    
    /**
     * 设置后缀
     * @param suffix 后缀字符串
     */
    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
}