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

package com.harbortek.helm.smartdoc.exporter.word.html2docx.handler.block;

import com.harbortek.helm.smartdoc.exporter.word.html2docx.util.ConverterUtils;
import com.harbortek.helm.smartdoc.exporter.word.html2docx.util.RunUtils;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.*;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import java.math.BigInteger;


public class BTitleHandler  extends   BlockHandler{
    public void handleTag(Node node, WordprocessingMLPackage wordMLPackage) {
        P p  = factory.createP();
//        P p = RunUtils.getCurrentParagraph(wordMLPackage);
//        R r = RunUtils.getCurrentRun(wordMLPackage);
        R r = factory.createR();
        p.getContent().add(r);
        RPr rPr = factory.createRPr();
        //设置加粗
//        rPr.setB(RunUtils.createBooleanDefaultTrue());
//        rPr.setBCs(RunUtils.createBooleanDefaultTrue());
        //设置黑体
        RFonts rfonts = factory.createRFonts();
        rfonts.setAscii("SimHei");
        rfonts.setHAnsi("SimHei");
        rfonts.setEastAsia("SimHei");
        rPr.setRFonts(rfonts);
        r.setRPr(rPr);

        //设置字号
        int fontSize = Integer.parseInt("32");
        HpsMeasure hpsMeasure = factory.createHpsMeasure();
        hpsMeasure.setVal(ConverterUtils.pxToHalfPoints(fontSize));
        rPr.setSz(hpsMeasure);
        rPr.setSzCs(hpsMeasure);


        wordMLPackage.getMainDocumentPart().getContent().add(0,p);
        //设置行间距
        PPr pPr = p.getPPr() != null ? p.getPPr() : factory.createPPr();
// 创建或获取Spacing对象
        PPrBase.Spacing spacing = pPr.getSpacing() != null ? pPr.getSpacing() : factory.createPPrBaseSpacing();
// 设置行间距为2倍行距，这里假设使用的是线倍数方式（具体方法可能根据docx4j版本有所不同）
        spacing.setLineRule(STLineSpacingRule.AUTO);
        spacing.setLine(BigInteger.valueOf(360)); // 2表示2倍行距
        spacing.setBefore(BigInteger.valueOf(240));
        spacing.setAfter(BigInteger.valueOf(240));
// 将Spacing设置回PPr，再将PPr设置回段落
        pPr.setSpacing(spacing);
        p.setPPr(pPr);


        Text text = RunUtils.getObjectFactory().createText();
        String nodeText = ((Element) node).text();
        text.setValue(nodeText);
        text.setSpace("preserve");
        r.getContent().add(text);
        Jc jc = factory.createJc();
        jc.setVal(JcEnumeration.CENTER);
        p.getPPr().setJc(jc);

        RunUtils.getCurrentParagraph(wordMLPackage).setPPr(pPr);
        RunUtils.getCurrentRun(wordMLPackage).setRPr(rPr);
        RunUtils.getCurrentRun(wordMLPackage).getContent().add(text);
        //PPr pPr = RunUtils.getObjectFactory().createPPr();
        //RunUtils.createParagraph(wordMLPackage, pPr);
        //RunUtils.getCurrentParagraph(wordMLPackage).getContent().add(r);
    }
}
