package com.wangeditor.editor;

import com.wangeditor.slate.Descendant;
import com.wangeditor.slate.Element;

/**
 * @description 编辑器接口
 * @author wangfupeng (Java version translator)
 */
public interface IDomEditor {
    
    /**
     * 判断节点是否为行内元素
     * @param elemNode 元素节点
     * @return 是否为行内元素
     */
    boolean isInline(Element elemNode);
    
    /**
     * 获取编辑器的根节点
     * @return 根节点
     */
    Element getRoot();
    
    /**
     * 获取编辑器的所有子节点
     * @return 子节点列表
     */
    java.util.List<Descendant> getChildren();
    
    /**
     * 获取编辑器的HTML内容
     * @return HTML字符串
     */
    String getHtml();
}