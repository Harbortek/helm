package com.harbortek.helm.tracker.entity.smartdoc.element.po;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.element.SlateElement;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.style.SlateStyle;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.style.StyleedStyle;

import java.util.List;

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

    public static JSONArray toMap(List<SlateNode> nodes) {
        JSONArray roots = new JSONArray();
        for (SlateNode node : nodes) {
            JSONObject root = new JSONObject();
            BeanUtil.copyProperties(node, root, CopyOptions.create().ignoreNullValue());
            List<StyleedStyle> styles = node.getStyles();
            if ((styles != null && !styles.isEmpty())) {
                for (Object styleObj : styles) {
                    BeanUtil.copyProperties(styleObj, root, CopyOptions.create().ignoreNullValue());
                }
                node.setStyles(null);
            }
            if (node instanceof SlateElement<?>) {
                SlateElement element = (SlateElement) node;
                JSONArray childrenMap = new JSONArray();
                List<SlateNode> children = element.getChildren();
                if (children != null && !children.isEmpty()) {
                    childrenMap = toMap(children);
                    root.put("children", childrenMap);
                }
            }
            roots.add(root);
        }
        return roots;
    }

    public static JSONArray toMap(JSONArray nodes) {
        JSONArray roots = new JSONArray();
        for (Object obj : nodes) {
            JSONObject node = (JSONObject) obj;
            JSONObject root = new JSONObject();
            BeanUtil.copyProperties(node, root, CopyOptions.create().ignoreNullValue());
            JSONArray styles = node.getJSONArray("styles");
            if ((styles != null && !styles.isEmpty())) {
                for (Object styleObj : styles) {
                    BeanUtil.copyProperties(styleObj, root, CopyOptions.create().ignoreNullValue());
                }
                node.put("styles", null);
            }
            JSONArray childrenMap = new JSONArray();
            JSONArray children = node.getJSONArray("children");
            if (children != null && !children.isEmpty()) {
                childrenMap = toMap(children);
                root.put("children", childrenMap);
            }
            roots.add(root);
        }
        return roots;
    }
}
