package com.wangeditor.editor;

import com.wangeditor.slate.Element;

/**
 * @description 编辑器工具类
 * @author wangfupeng (Java version translator)
 */
public class Editor {
    
    /**
     * 判断元素是否为void元素（没有子节点的元素）
     * @param editor 编辑器
     * @param elemNode 元素节点
     * @return 是否为void元素
     */
    public static boolean isVoid(IDomEditor editor, Element elemNode) {
        String type = elemNode.getType();
        // 默认的void元素类型
        return "image".equals(type) || "divider".equals(type);
    }
}