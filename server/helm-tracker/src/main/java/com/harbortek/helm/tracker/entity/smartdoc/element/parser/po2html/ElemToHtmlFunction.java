package com.wangeditor.html;

import com.wangeditor.editor.IDomEditor;
import com.wangeditor.slate.Element;

/**
 * @description 元素转HTML函数接口
 * @author wangfupeng (Java version translator)
 */
@FunctionalInterface
public interface ElemToHtmlFunction {
    
    /**
     * 将元素节点转换为HTML
     * @param elemNode 元素节点
     * @param childrenHtml 子节点HTML
     * @param editor 编辑器
     * @return HTML字符串或带前后缀的结果对象
     */
    Object apply(Element elemNode, String childrenHtml, IDomEditor editor);
}