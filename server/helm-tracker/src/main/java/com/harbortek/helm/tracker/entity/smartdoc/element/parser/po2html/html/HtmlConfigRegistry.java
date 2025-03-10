package com.wangeditor.html;

import com.wangeditor.editor.IDomEditor;
import com.wangeditor.slate.Descendant;
import com.wangeditor.slate.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description HTML配置注册中心
 * @author wangfupeng (Java version translator)
 */
public class HtmlConfigRegistry {

  // ------------------------------------ 样式转HTML
  // ------------------------------------

  /**
   * 样式转HTML函数列表
   */
  public static final List<StyleToHtmlFunction> STYLE_TO_HTML_FN_LIST = new ArrayList<>();

  /**
   * 注册样式转HTML处理函数
   * 
   * @param fn 处理样式转HTML的函数
   */
  public static void registerStyleToHtmlHandler(StyleToHtmlFunction fn) {
    STYLE_TO_HTML_FN_LIST.add(fn);
  }

  // ------------------------------------ 元素节点转HTML
  // ------------------------------------

  /**
   * 元素转HTML配置映射
   * key要和node.type对应
   */
  public static final Map<String, ElemToHtmlFunction> ELEM_TO_HTML_CONF = new HashMap<>();

  /**
   * 元素转HTML配置接口
   */
  public interface ElemToHtmlConf {
    /**
     * 获取元素类型
     * 
     * @return 元素类型
     */
    String getType();

    /**
     * 获取元素转HTML函数
     * 
     * @return 转换函数
     */
    ElemToHtmlFunction getElemToHtml();
  }

  /**
   * 注册元素转HTML配置
   * 
   * @param conf 配置对象
   */
  public static void registerElemToHtmlConf(ElemToHtmlConf conf) {
    String type = conf.getType() != null ? conf.getType() : "";
    ELEM_TO_HTML_CONF.put(type, conf.getElemToHtml());
  }

  // ------------------------------------ 预解析HTML
  // ------------------------------------

  /**
   * 预解析HTML函数接口
   */
  @FunctionalInterface
  public interface PreParseHtmlFunction {
    /**
     * 预处理HTML元素
     * 
     * @param element DOM元素
     * @return 处理后的DOM元素
     */
    Object apply(Object element);
  }

  /**
   * 预解析HTML配置接口
   */
  public interface PreParseHtmlConf {
    /**
     * 获取CSS选择器
     * 
     * @return 选择器字符串
     */
    String getSelector();

    /**
     * 获取预解析函数
     * 
     * @return 预解析函数
     */
    PreParseHtmlFunction getPreParseHtml();
  }

  /**
   * 预解析HTML配置列表
   */
  public static final List<PreParseHtmlConf> PRE_PARSE_HTML_CONF_LIST = new ArrayList<>();

  /**
   * 注册预解析HTML配置
   * 
   * @param conf 配置对象
   */
  public static void registerPreParseHtmlConf(PreParseHtmlConf conf) {
    PRE_PARSE_HTML_CONF_LIST.add(conf);
  }

  // ------------------------------------ 解析样式HTML
  // ------------------------------------

  /**
   * 解析样式HTML函数接口
   */
  @FunctionalInterface
  public interface ParseStyleHtmlFunction {
    /**
     * 解析元素样式
     * 
     * @param element DOM元素
     * @param node    节点
     * @param editor  编辑器
     * @return 处理后的节点
     */
    Descendant apply(Object element, Descendant node, IDomEditor editor);
  }

  /**
   * 解析样式HTML函数列表
   */
  public static final List<ParseStyleHtmlFunction> PARSE_STYLE_HTML_FN_LIST = new ArrayList<>();

  /**
   * 注册解析样式HTML处理函数
   * 
   * @param fn 处理函数
   */
  public static void registerParseStyleHtmlHandler(ParseStyleHtmlFunction fn) {
    PARSE_STYLE_HTML_FN_LIST.add(fn);
  }

  // ------------------------------------ 解析元素HTML
  // ------------------------------------

  /**
   * 解析元素HTML函数接口
   */
  @FunctionalInterface
  public interface ParseElemHtmlFunction {
    /**
     * 解析元素HTML
     * 
     * @param element  DOM元素
     * @param children 子节点列表
     * @param editor   编辑器
     * @return 解析后的元素节点或节点列表
     */
    Object apply(Object element, List<Descendant> children, IDomEditor editor);
  }

  /**
   * 解析元素HTML配置映射
   * key是CSS选择器
   */
  public static final Map<String, ParseElemHtmlFunction> PARSE_ELEM_HTML_CONF = new HashMap<>();

  /**
   * 解析元素HTML配置接口
   */
  public interface ParseElemHtmlConf {
    /**
     * 获取CSS选择器
     * 
     * @return 选择器字符串
     */
    String getSelector();

    /**
     * 获取解析元素HTML函数
     * 
     * @return 解析函数
     */
    ParseElemHtmlFunction getParseElemHtml();
  }

  /**
   * 注册解析元素HTML配置
   * 
   * @param conf 配置对象
   */
  public static void registerParseElemHtmlConf(ParseElemHtmlConf conf) {
    PARSE_ELEM_HTML_CONF.put(conf.getSelector(), conf.getParseElemHtml());
  }

  /**
   * 常见的文本标签列表
   */
  public static final List<String> TEXT_TAGS = new ArrayList<>();

  static {
    // 初始化文本标签列表
    TEXT_TAGS.add("span");
    TEXT_TAGS.add("b");
    TEXT_TAGS.add("strong");
    TEXT_TAGS.add("i");
    TEXT_TAGS.add("em");
    TEXT_TAGS.add("s");
    TEXT_TAGS.add("strike");
    TEXT_TAGS.add("u");
    TEXT_TAGS.add("font");
    TEXT_TAGS.add("sub");
    TEXT_TAGS.add("sup");
  }
}