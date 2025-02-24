/*
 * Copyright [2025] [Harbortek Corp.]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.harbortek.helm.smartdoc.utils;

import com.harbortek.helm.smartdoc.importer.word.rules.ExtractionRule;
import com.harbortek.helm.smartdoc.importer.word.rules.ParagraphExtractionRule;
import com.harbortek.helm.smartdoc.importer.word.rules.TableExtractionRule;
import com.harbortek.helm.smartdoc.vo.WordImportJobVo;
import com.harbortek.helm.tracker.constants.BlockTypes;
import com.harbortek.helm.tracker.entity.block.DocBlock;
import com.harbortek.helm.tracker.entity.block.HeaderBlockData;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordUtils {
    private Map<String, Map<String, Object>> orderMap = new HashMap<String, Map<String, Object>>();

    public String getHeadingNumber(String titleLvl) {
        String order = "";

        if ("0".equals(titleLvl) || Integer.parseInt(titleLvl) == 8) {//文档标题||正文
            return "";
        } else if (Integer.parseInt(titleLvl) > 0 && Integer.parseInt(titleLvl) < 8) {//段落标题

            //设置最高级别标题
            Map<String, Object> maxTitleMap = orderMap.get("maxTitleLvlMap");
            if (null == maxTitleMap) {//没有，表示第一次进来
                //最高级别标题赋值
                maxTitleMap = new HashMap<String, Object>();
                maxTitleMap.put("lvl", titleLvl);
                orderMap.put("maxTitleLvlMap", maxTitleMap);
            } else {
                String maxTitleLvl = String.valueOf(maxTitleMap.get("lvl"));//最上层标题级别(0,1,2,3)
                if (Integer.parseInt(titleLvl) < Integer.parseInt(maxTitleLvl)) {//当前标题级别更高
                    maxTitleMap.put("lvl", titleLvl);//设置最高级别标题
                    orderMap.put("maxTitleLvlMap", maxTitleMap);
                }
            }

            //查父节点标题
            int parentTitleLvl = Integer.parseInt(titleLvl) - 1;//父节点标题级别
            Map<String, Object> cMap = orderMap.get(titleLvl);//当前节点信息
            Map<String, Object> pMap = orderMap.get(String.valueOf(parentTitleLvl));//父节点信息

            if (0 == parentTitleLvl) {//父节点为文档标题，表明当前节点为1级标题
                int count = 0;
                //最上层标题，没有父节点信息
                if (null == cMap) {//没有当前节点信息
                    cMap = new HashMap<String, Object>();
                } else {
                    count = Integer.parseInt(String.valueOf(cMap.get("cCount")));//当前序个数
                }
                count++;
                order = String.valueOf(count);
                cMap.put("cOrder", order);//当前序
                cMap.put("cCount", count);//当前序个数
                orderMap.put(titleLvl, cMap);

            } else {//父节点为非文档标题
                int count = 0;
                //如果没有相邻的父节点信息，当前标题级别自动升级
                if (null == pMap) {
                    return getHeadingNumber(String.valueOf(parentTitleLvl));
                } else {
                    String pOrder = String.valueOf(pMap.get("cOrder"));//父节点序
                    if (null == cMap) {//没有当前节点信息
                        cMap = new HashMap<String, Object>();
                    } else {
                        count = Integer.parseInt(String.valueOf(cMap.get("cCount")));//当前序个数
                    }
                    count++;
                    order = pOrder + "." + count;//当前序编号
                    cMap.put("cOrder", order);//当前序
                    cMap.put("cCount", count);//当前序个数
                    orderMap.put(titleLvl, cMap);
                }
            }

            //字节点标题计数清零
            int childTitleLvl = Integer.parseInt(titleLvl) + 1;//子节点标题级别
            Map<String, Object> cdMap = orderMap.get(String.valueOf(childTitleLvl));//
            if (null != cdMap) {
                cdMap.put("cCount", 0);//子节点序个数
                orderMap.get(String.valueOf(childTitleLvl)).put("cCount", 0);
            }
        }
        return order;
    }

    /**
     * 找寻匹配目录的规则
     *
     * @param block
     * @param config
     * @return
     */
    public ExtractionRule getMatchRule(DocBlock block,List<DocBlock> parentHeadingBlocks, WordImportJobVo config) {
        String heading = block.getId();

        for (ExtractionRule rule : config.getRules()) {
            if (rule instanceof ParagraphExtractionRule) {
                ParagraphExtractionRule pRule = (ParagraphExtractionRule) rule;
                if ("ALL".equals(pRule.getScope()) || pRule.getSource().contains(heading)) {
                    return pRule;
                }
            } else if (rule instanceof TableExtractionRule) {
                TableExtractionRule tRule = (TableExtractionRule) rule;
                if ("ALL".equals(tRule.getScope()) || tRule.getSource().contains(heading)) {
                    return tRule;
                }
            }
        }
        return null;
    }

    public static Element blockElements(DocBlock block){
        if (BlockTypes.HEADING.equals(block.getType())){
            String tag = "h"+((HeaderBlockData)block.getData()).getLevel();
            String html = "<"+tag+">" +(block.getData()).getText() + "</"+tag+">";
            return Jsoup.parseBodyFragment(html).body().firstElementChild();
        }else if (BlockTypes.PARAGRAPH.equals(block.getType())){
            String html = (block.getData()).getText();
            return Jsoup.parseBodyFragment(html).body().firstElementChild();
        }
        return null;
    }

    public static List<Element> blockElements(List<DocBlock> blocks){
        StringBuilder sb = new StringBuilder();
        for (DocBlock block : blocks) {
            if (BlockTypes.HEADING.equals(block.getType())){
                String tag = "h"+((HeaderBlockData)block.getData()).getLevel();
                String html = "<"+tag+">" +(block.getData()).getText() + "</"+tag+">";
                sb.append(html).append("\n");
            }else if (BlockTypes.PARAGRAPH.equals(block.getType())){
                String html = (block.getData()).getText();
                sb.append(html).append("\n");
            }
        }
        String html =  sb.toString();
        return new ArrayList<>(Jsoup.parseBodyFragment(html).body().children() );
    }

    public static String html(List<Element> paragraphs) {
        StringBuilder sb = new StringBuilder();
        for (Element element : paragraphs) {
            sb.append(element.outerHtml()).append("\n");
        }
        return sb.toString();
    }

    public static String html(Element paragraph) {
        return paragraph!=null ? paragraph.outerHtml() : "";
    }

    public static String plainText(List<Element> paragraphs) {
        StringBuilder sb = new StringBuilder();
        for (Element element : paragraphs) {
            sb.append(element.text()).append("\n");
        }
        return sb.toString();
    }

    public static String plainText(Element paragraph) {
        return paragraph!=null ?paragraph.text() :"";
    }




}
