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

package com.harbortek.helm.smartdoc.exporter.word.html2docx.handler;

import com.deepoove.poi.data.PictureRenderData;
import com.deepoove.poi.data.PictureType;
import com.deepoove.poi.data.Pictures;
import com.harbortek.helm.smartdoc.exporter.word.html2docx.core.TagHandler;
import com.harbortek.helm.smartdoc.exporter.word.html2docx.util.Constants;
import com.harbortek.helm.smartdoc.exporter.word.html2docx.util.RunUtils;
import com.harbortek.helm.util.SpringContextUtil;
import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;
import org.docx4j.wml.*;
import org.jsoup.nodes.Node;
import org.springframework.core.env.Environment;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class ImageHandler implements TagHandler {
    private ObjectFactory factory = RunUtils.getObjectFactory();

    /**
     * 根据Image 标签转换为 WordprocessingMLPackage的照片
     *
     * @param node          The HTML node representing the custom tag to be processed.
     * @param wordMLPackage The WordprocessingMLPackage to be manipulated
     *                      based on the tag logic.
     */
    @Override
    public void handleTag(Node node, WordprocessingMLPackage wordMLPackage) {
        try {
            PictureRenderData pictureRenderData = this.parsePicture(node);
            BinaryPartAbstractImage imagePart = null;
            imagePart = BinaryPartAbstractImage.createImagePart(wordMLPackage, pictureRenderData.readPictureData());
            wordMLPackage.getMainDocumentPart().addTargetPart(imagePart).getId();
            Inline inline = imagePart.createImageInline(null, pictureRenderData.getAltMeta(), 0L, 1);
            Drawing picture = factory.createDrawing();
            picture.getAnchorOrInline().add(inline);
            RunUtils.getCurrentRun(wordMLPackage).getContent().add(picture);
            //设置单倍行间距
            P p = RunUtils.getCurrentParagraph(wordMLPackage);
            PPr pPr = p.getPPr() != null ? p.getPPr() : RunUtils.getObjectFactory().createPPr();
            PPrBase.Spacing spacing = pPr.getSpacing() != null ? pPr.getSpacing() : RunUtils.getObjectFactory().createPPrBaseSpacing();
            spacing.setLineRule(STLineSpacingRule.AUTO);
            spacing.setLine(BigInteger.valueOf(240)); // 1倍 行间距
            pPr.setSpacing(spacing);
            p.setPPr(pPr);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private PictureType parsePictureTypeByBase64(String uri) {
        PictureType pictureType = PictureType.JPEG;
        if (uri.startsWith("data:image/gif")) {
            pictureType = PictureType.GIF;
        } else if (uri.startsWith("data:image/png")) {
            pictureType = PictureType.PNG;
        } else if (uri.startsWith("data:image/jpeg")) {
            pictureType = PictureType.JPEG;
        }
        return pictureType;
    }

    private PictureRenderData parsePicture(Node image) {
        String uri = image.attr(Constants.IMAGE_SRC);
        if (uri.startsWith("data:image")) {
            PictureRenderData pictureRenderData = Pictures.ofBase64(uri, this.parsePictureTypeByBase64(uri)).altMeta(image.attr(Constants.IMAGE_TITLE)).fitSize().left().create();
            return pictureRenderData;
        }
        if (!uri.startsWith("http")) {

            try {
                String ip = InetAddress.getLocalHost().getHostAddress();
                Environment env = SpringContextUtil.getApplicationContext().getEnvironment();
                String port = env.getProperty("server.port");
                String path = env.getProperty("server.servlet.context-path");
                uri = "http://" + ip + ":" + port + uri;
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
        }
        image.attr(Constants.IMAGE_SRC, uri);
        PictureRenderData pictureRenderData = Pictures.of(uri).altMeta(image.attr(Constants.IMAGE_TITLE)).fitSize().left().create();
        return pictureRenderData;
    }

    /**
     * Indicates whether the FontHandler can be applied multiple times to the same node.
     *
     * @return true if the FontHandler can be applied multiple times; false otherwise.
     */
    @Override
    public boolean isRepeatable() {
        return false;
    }
}
