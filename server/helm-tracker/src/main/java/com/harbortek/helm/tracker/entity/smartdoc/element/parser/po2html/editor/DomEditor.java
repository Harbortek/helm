package com.harbortek.helm.tracker.entity.smartdoc.element.parser.po2html.editor;

import com.harbortek.helm.tracker.entity.smartdoc.element.parser.po2html.html.Node2Html;
import com.harbortek.helm.tracker.entity.smartdoc.element.parser.po2html.slate.Descendant;
import com.harbortek.helm.tracker.entity.smartdoc.element.parser.po2html.slate.Element;
import com.harbortek.helm.tracker.entity.smartdoc.element.parser.po2html.slate.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * @description 编辑器实现类
 * @author wangfupeng (Java version translator)
 */
public class DomEditor implements IDomEditor {
    private Element root;

    /**
     * 构造函数
     * @param root 根节点
     */
    public DomEditor(Element root) {
        this.root = root;
    }

    /**
     * 判断节点是否为行内元素
     * @param elemNode 元素节点
     * @return 是否为行内元素
     */
    @Override
    public boolean isInline(Element elemNode) {
        String type = elemNode.getType();
        // 默认的行内元素类型
        return "link".equals(type) || "image".equals(type) || "list-item".equals(type);
    }
    /**
     * 判断节点是否为void元素（没有子节点的元素）
     *
     * @param elemNode 元素节点
     * @return 是否为void元素
     */
    @Override
    public boolean isVoid(Element elemNode) {
        String type = elemNode.getType();
        // 默认的void元素类型
        return "image".equals(type) || "divider".equals(type) || "video".equals(type) || "table".equals(type);
    }
    /**
     * 获取编辑器的根节点
     * @return 根节点
     */
    @Override
    public Element getRoot() {
        return root;
    }

    /**
     * 获取编辑器的所有子节点
     * @return 子节点列表
     */
    @Override
    public List<Descendant> getChildren() {
        return root.getChildren();
    }

    /**
     * 获取编辑器的HTML内容
     * @return HTML字符串
     */
    @Override
    public String getHtml() {
        return Node2Html.node2Html(root, this);
    }

    /**
     * 获取节点类型
     * @param node 节点
     * @return 节点类型
     */
    public static String getNodeType(Descendant node) {
        if (Element.isElement(node)) {
            return ((Element) node).getType();
        }
        return "";
    }

    /**
     * 获取父节点
     * @param editor 编辑器
     * @param node 当前节点
     * @return 父节点
     */
    public static Descendant getParentNode(IDomEditor editor, Descendant node) {
        // 简化实现，实际应该遍历编辑器的节点树查找父节点
        // 在实际应用中，可能需要更复杂的实现
        return null;
    }

    /**
     * 获取所有父节点
     * @param editor 编辑器
     * @param node 当前节点
     * @return 父节点列表
     */
    public static List<Descendant> getParentsNodes(IDomEditor editor, Descendant node) {
        // 简化实现，实际应该遍历编辑器的节点树查找所有父节点
        // 在实际应用中，可能需要更复杂的实现
        return new ArrayList<>();
    }
}