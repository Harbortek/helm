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

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import com.harbortek.helm.smartdoc.constants.ReqIFDataTypes;
import com.harbortek.helm.smartdoc.importer.reqif.analysis.attributes.AttributeValueXHTML;
import com.harbortek.helm.smartdoc.importer.reqif.analysis.datatypes.Datatype;
import com.harbortek.helm.smartdoc.importer.reqif.analysis.reqif.ReqIFConst;
import com.harbortek.helm.smartdoc.importer.reqif.analysis.reqif.ReqIFDocument;
import com.harbortek.helm.smartdoc.importer.reqif.analysis.reqif.ReqIFFile;
import com.harbortek.helm.smartdoc.importer.reqif.analysis.specification.SpecType;
import com.harbortek.helm.smartdoc.importer.reqif.rules.EnumerationValueMapping;
import com.harbortek.helm.smartdoc.importer.reqif.rules.ReqIFFieldMapping;
import com.harbortek.helm.tracker.constants.Associations;
import com.harbortek.helm.tracker.constants.SystemFields;
import com.harbortek.helm.tracker.vo.items.TrackerItemVo;
import com.harbortek.helm.tracker.vo.tracker.fields.TrackerField;
import com.harbortek.helm.util.DateUtils;
import lombok.SneakyThrows;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.parser.Parser;
import org.w3c.dom.Node;

import javax.imageio.ImageIO;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

public class ReqIFUtils {
    public static String getId(Node node){
        Node valueNode = node.getAttributes().getNamedItem(ReqIFConst.IDENTIFIER);
        if (valueNode!=null) {
           return StringUtils.trim( node.getAttributes().getNamedItem(ReqIFConst.IDENTIFIER).getTextContent());
        }
        return null;
    }

    public static String getName(Node node){
        Node valueNode = node.getAttributes().getNamedItem(ReqIFConst.LONG_NAME);
        if (valueNode!=null) {
            return StringUtils.trim( node.getAttributes().getNamedItem(ReqIFConst.LONG_NAME).getTextContent());
        }
        return null;
    }

    public static Date getLastChange(Node node){
        Node valueNode = node.getAttributes().getNamedItem(ReqIFConst.LAST_CHANGE);
        if (valueNode!=null) {
            return  DateUtils.parseISO8601Date(node.getAttributes().getNamedItem(ReqIFConst.LAST_CHANGE).getTextContent());
        }
        return DateUtils.now();
    }


//    public static String html(ReqIFFile reqIFFile, ReqIFDocument reqIFDocument, AttributeValueXHTML value) {
//        return buildHTML(reqIFFile,reqIFDocument, value.getDivValue());
//    }

    public static String plainText(AttributeValueXHTML value) {
        if (value==null){
            return "";
        }
        String xhtml = StringUtils.trimToEmpty((String)value.getValue());
        xhtml = xhtml.replace("xhtml:", "");
        return WordUtils.plainText(new ArrayList<>(Jsoup.parseBodyFragment(xhtml).body().children()));
    }

//    private static String buildHTML(ReqIFFile reqIFFile, ReqIFDocument reqIFDocument, XHTMLNode parent) {
//        StringBuilder sb = new StringBuilder();
//        String tagName = parent.getTagName();
//        if (parent instanceof XHTMLLeaf){
//            if (parent instanceof XHTMLElementBr){
//                sb.append("<br/>");
//            }else if (parent instanceof XHTMLElementText){
//                sb.append(((XHTMLElementText) parent).getTextContent());
//            }
//        }else if (parent instanceof XHTMLElement) {
//            if (parent instanceof XHTMLElementObject){
//                String pictureFileName = ((XHTMLElementObject) parent).getData();
//                String type = ((XHTMLElementObject) parent).getType();
//                String base64 = reqIFFile.getPicture(reqIFDocument.getFileName(),pictureFileName);
//                sb.append("<img src=\"data:").append(type).append(";base64,").append(base64).append("\"/>");
//            }else{
//                sb.append(tagName);
//                for (XHTMLNode child : ((XHTMLElement) parent).getChildren()) {
//                    sb.append(buildHTML(reqIFFile, reqIFDocument,child));
//                }
//                sb.append("</").append(tagName).append(">");
//            }
//        }
//        return sb.toString();
//    }

    public static String mapRelationNameToCode(ReqIFFile reqIFFile, String name) {
        if (isPolarion(reqIFFile)){
            if (name.equals("affects")){
                return Associations.AFFECTS.getId();
            }else if (name.equals("assesses")){
                return Associations.ASSESSES.getId();
            }else if (name.equals("depends on")){
                return Associations.DEPENDS_ON.getId();
            }else if (name.equals("duplicates")){
                return Associations.DUPLICATES.getId();
            }else if (name.equals("has follow-up")){
                return Associations.FOLLOW_UP.getId();
            }else if (name.equals("implements")){
                return Associations.IMPLEMENTS.getId();
            }else if (name.equals("is branched from")){
                return Associations.BRANCHED_FROM.getId();
            }else if (name.equals("is derived from")){
                return Associations.DERIVED_FROM.getId();
            }else if (name.equals("is triggered by")){
                return Associations.TRIGGERED_BY.getId();
            }else if (name.equals("mitigates")){
                return Associations.MITIGATES.getId();
            }else if (name.equals("refines")){
                return Associations.REFINES.getId();
            }else if (name.equals("tracks")){
                return Associations.TRACKS.getId();
            }else if (name.equals("uses")){
                return Associations.USES.getId();
            }else if (name.equals("verifies")){
                return Associations.VERIFIES.getId();
            }else{
                return Associations.RELATES_TO.getId();
            }
        }
        return Associations.RELATES_TO.getId();
    }

    public static String getToolName(ReqIFFile reqIFFile){
        for (ReqIFDocument document : reqIFFile.getReqIFDocuments().values()) {
            String toolName = document.getHeader().getSourceToolID();
            if (StringUtils.isNotEmpty(toolName)){
                return toolName;
            }
            toolName = document.getHeader().getToolID();
            if (StringUtils.isNotEmpty(toolName)){
                return toolName;
            }
        }
        return "";
    }

    public static boolean isPolarion (ReqIFFile reqIFFile){
        return getToolName(reqIFFile).contains("Polarion");
    }

    public static boolean isDoors(ReqIFFile reqIFFile){
        return getToolName(reqIFFile).contains("IBM");
    }

    public static Datatype findDataType(Collection<Datatype> dataTypes, ReqIFFieldMapping fieldMapping) {
        for (Datatype datatype: dataTypes){
            if (datatype.getType().equals(fieldMapping.getType())){
                if (ReqIFDataTypes.ENUMERATION.equals(datatype.getType())){
                    String typeName = "Enumeration for "+fieldMapping.getReqIFAttributeName();
                    if (datatype.getName().equals(typeName)){
                        return datatype;
                    }
                }else{
                    return datatype;
                }
            }
        }
        return null;
    }

    @SneakyThrows
    public static String stringToUUID(String text){
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(text.getBytes(StandardCharsets.UTF_8));
        byte[] bytes = Arrays.toString(md5.digest()).getBytes(StandardCharsets.UTF_8);
        return "rmf-"+UUID.nameUUIDFromBytes(bytes).toString(false);
    }

    public static String systemIdToUUID(Long id){
        byte[] bytes = String.valueOf(id).getBytes(StandardCharsets.UTF_8);
        return "rmf-"+UUID.nameUUIDFromBytes(bytes).toString(false);
    }

    public static SpecType findSpecType(Collection<SpecType> specTypes, String specTypeName) {
        for (SpecType specType: specTypes){
            if (specType.getName().equals(specTypeName)){
                return specType;
            }
        }
        return null;
    }

    public static String getValue(TrackerItemVo trackerItem, ReqIFFieldMapping fieldMapping) {
        TrackerField trackerField =
                trackerItem.getTracker().getReferTo().getTrackerFields().stream().filter(field -> field.getId().equals(fieldMapping.getTrackerFieldId())).findFirst().orElse(null);
        if (trackerField==null){
            return "";
        }
        String fieldName = trackerField.getSystemProperty();
        if (SystemFields.NAME.equals(fieldName)) {
            return trackerItem.getName();
        } else if (SystemFields.ITEM_NO.equals(fieldName)) {
            return  trackerItem.getItemNo();
        } else if (SystemFields.DESCRIPTION.equals(fieldName)) {
            return trackerItem.getDescription();
        } else if (SystemFields.OWNER.equals(fieldName)) {
            return trackerItem.getOwner().getName();
        } else if (SystemFields.SPRINT.equals(fieldName)) {
            return trackerItem.getSprint().getName();
        } else if (SystemFields.CREATE_BY.equals(fieldName)) {
            return trackerItem.getCreateBy().getName();
        } else if (SystemFields.CREATE_DATE.equals(fieldName)) {
            return DateUtil.format(trackerItem.getCreateDate(), DatePattern.UTC_MS_FORMAT);
        } else if (SystemFields.LAST_MODIFIED_BY.equals(fieldName)) {
            return trackerItem.getLastModifiedBy().getName();
        } else if (SystemFields.LAST_MODIFIED_DATE.equals(fieldName)) {
            return DateUtil.format(trackerItem.getLastModifiedDate(), DatePattern.UTC_MS_FORMAT);
        } else if (SystemFields.STATUS.equals(fieldName)) {
            Long enumId = trackerItem.getStatus().getId();
            return getFieldEnumValue(enumId,fieldMapping.getEnumMapping());
        } else if (SystemFields.ASSIGNED_TO.equals(fieldName)) {
            return trackerItem.getAssignedTo().getName();
        } else if (SystemFields.ASSIGNED_DATE.equals(fieldName)) {
            return DateUtil.format(trackerItem.getAssignedDate(), DatePattern.UTC_MS_FORMAT);
        } else if (SystemFields.PRIORITY.equals(fieldName)) {
            Long priorityId = trackerItem.getPriority().getId();
            return getFieldEnumValue(priorityId,fieldMapping.getEnumMapping());
        } else if (SystemFields.SEVERITY.equals(fieldName)) {
            Long severityId = trackerItem.getSeverity().getId();
            return getFieldEnumValue(severityId,fieldMapping.getEnumMapping());
        } else if (SystemFields.PLAN_START_DATE.equals(fieldName)) {
            return DateUtil.format(trackerItem.getPlanStartDate(), DatePattern.UTC_MS_FORMAT);
        } else if (SystemFields.PLAN_END_DATE.equals(fieldName)) {
            return DateUtil.format(trackerItem.getPlanEndDate(), DatePattern.UTC_MS_FORMAT);
        } else if (SystemFields.REAL_START_DATE.equals(fieldName)) {
            return DateUtil.format(trackerItem.getRealStartDate(), DatePattern.UTC_MS_FORMAT);
        } else if (SystemFields.REAL_END_DATE.equals(fieldName)) {
            return DateUtil.format(trackerItem.getRealEndDate(), DatePattern.UTC_MS_FORMAT);
        } else if (SystemFields.PROGRESS.equals(fieldName)) {
            return String.valueOf(trackerItem.getProgress());
        } else if (SystemFields.CLOSE_DATE.equals(fieldName)) {
            return DateUtil.format(trackerItem.getCloseDate(), DatePattern.UTC_MS_FORMAT);
        } else if (SystemFields.ESTIMATE_WORKING_HOURS.equals(fieldName)) {
            return String.valueOf(trackerItem.getEstimateWorkingHours());
        } else if (SystemFields.REGISTERED_WORKING_HOURS.equals(fieldName)) {
            return String.valueOf(trackerItem.getRegisteredWorkingHours());
        } else if (SystemFields.REMAINING_WORKING_HOURS.equals(fieldName)) {
            return String.valueOf(trackerItem.getRemainingWorkingHours());
//        } else if (SystemFields.TEST_CASE_TYPE.equals(fieldName)) {
//            Long enumId = trackerItem.getTestCaseType().getId();
//            return getFieldEnumValue(enumId,fieldMapping.getEnumMapping());
        }
        return "";
    }

    private static String getFieldEnumValue(Long enumValueId, List<EnumerationValueMapping> enumMapping) {
        for (EnumerationValueMapping mapping: enumMapping){
            if (mapping.getTrackerEnumValueId().equals(enumValueId)){
                return mapping.getReqIFValueId();
            }
        }
        return "";
    }

//    public static String xhtml(AttributeValueXHTMLElementList value) {
//        StringBuilder sb = new StringBuilder();
//        for(String element: value.getElementList()){
//            sb.append(element);
//        }
//        return sb.toString();
//    }

    public static String xhtml2HTML(ReqIFFile reqIFFile, ReqIFDocument reqIFDocument, String htmlContent){
        org.jsoup.nodes.Document document =  Jsoup.parse(htmlContent,Parser.xmlParser());
        document.outputSettings().syntax(org.jsoup.nodes.Document.OutputSettings.Syntax.html);
        document.outputSettings().prettyPrint(false);
        document.getAllElements().forEach(node -> {
            if (node != null){
                node.tagName(StringUtils.removeStart(node.tagName(),"xhtml:"));
                if (node.tagName().equals("object")){
                    node.tagName("img");
                    String pictureFileName = ((org.jsoup.nodes.Element)node).attr("data");
                    String type = ((org.jsoup.nodes.Element)node).attr("type");
                    String base64 = reqIFFile.getPicture(reqIFDocument.getFileName(),pictureFileName);
                    ((org.jsoup.nodes.Element) node).attr("src","data:"+type+";base64,"+base64);
                    ((org.jsoup.nodes.Element) node).removeAttr("data");
                    ((org.jsoup.nodes.Element) node).removeAttr("type");
                    ((org.jsoup.nodes.Element) node).removeAttr("name");
                }
            }
        });
        return document.html();
    }

    public static String html2XHTML(String htmlContent,ImageSaver imageSaver){
        org.jsoup.nodes.Document document =  Jsoup.parse(htmlContent,Parser.xmlParser());
        document.outputSettings().syntax(org.jsoup.nodes.Document.OutputSettings.Syntax.xml);
        document.getAllElements().forEach(node -> {
            if (node != null){
                if (node.tagName().equals("img")){
                    node.tagName("xhtml:object");
                    String src = ((org.jsoup.nodes.Element) node).attr("src");
                    if (src.contains("base64")){
                        String fileType = src.split(",")[0];
                        fileType = StringUtils.removeStart(fileType,"data:image/");
                        fileType = StringUtils.removeEnd(fileType,";base64");
                        String base64 = src.split(",")[1];
                        String fileName = stringToUUID(base64)+"."+fileType;
                        ((org.jsoup.nodes.Element) node).attr("data","files/"+fileName);
                        ((org.jsoup.nodes.Element) node).attr("type","image/"+fileType);
                        ((org.jsoup.nodes.Element) node).attr("name",fileName);
                        if (imageSaver!=null){
                            imageSaver.saveImage("files/"+fileName, base64,true);
                        }
                    }else{
                        String fileName = FilenameUtils.getName(src);
                        String fileType = FilenameUtils.getExtension(src);
                        ((org.jsoup.nodes.Element) node).attr("data","files/"+fileName);
                        ((org.jsoup.nodes.Element) node).attr("type","image/"+fileType);
                        ((org.jsoup.nodes.Element) node).attr("name",fileName);
                        if (imageSaver!=null){
                            imageSaver.saveImage("files/"+fileName,src,false);
                        }
                    }

                    ((org.jsoup.nodes.Element) node).removeAttr("src");
                }
                else {
                    node.tagName("xhtml:"+ node.tagName());
                }
            }
        });

        return document.html();
    }

    public static String w3cNode2String(Node node){
        StringWriter buffer = new StringWriter();
        try {
            TransformerFactory transFactory = TransformerFactory.newInstance();
            Transformer transformer = transFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.transform(new DOMSource(node.getNextSibling()),
                                  new StreamResult(buffer));
        } catch (TransformerException ignored) {
        }
        return buffer.toString();
    }

//    public static Node parseXHTML(String xhtml) {
//        xhtml = "<xhtml><xhtml:div>"+xhtml+"</xhtml:div></xhtml>";
//
//        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//        DocumentBuilder builder = null;
//        try {
//            builder = factory.newDocumentBuilder();
//            Document document = builder.parse(new ByteArrayInputStream(xhtml.getBytes(StandardCharsets.UTF_8)));
//            return document.getElementsByTagName("xhtml").item(0);
//        } catch (ParserConfigurationException | IOException | SAXException e) {
//            throw new RuntimeException(e);
//        }
//    }

    /**
     * 图片转Base64字符串
     * @param imageFileName
     * @return
     */
    public static String convertImageToBase64Str(String imageFileName) {
        ByteArrayOutputStream baos = null;
        try {
            //获取图片类型
            String suffix = imageFileName.substring(imageFileName.lastIndexOf(".") + 1);
            //构建文件
            File imageFile = new File(imageFileName);
            //通过ImageIO把文件读取成BufferedImage对象
            BufferedImage bufferedImage = ImageIO.read(imageFile);
            //构建字节数组输出流
            baos = new ByteArrayOutputStream();
            //写入流
            ImageIO.write(bufferedImage, suffix, baos);
            //通过字节数组流获取字节数组
            byte[] bytes = baos.toByteArray();
            //获取JDK8里的编码器Base64.Encoder转为base64字符
            return Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Base64字符串转图片
     * @param base64String
     * @param imageFileName
     */
    public static void convertBase64StrToImage(String base64String, String imageFileName) {
        ByteArrayInputStream bais = null;
        try {
            //获取图片类型
            String suffix = imageFileName.substring(imageFileName.lastIndexOf(".") + 1);
            //获取JDK8里的解码器Base64.Decoder,将base64字符串转为字节数组
            byte[] bytes = Base64.getDecoder().decode(base64String);
            //构建字节数组输入流
            bais = new ByteArrayInputStream(bytes);
            //通过ImageIO把字节数组输入流转为BufferedImage
            BufferedImage bufferedImage = ImageIO.read(bais);
            //构建文件
            File imageFile = new File(imageFileName);
            //写入生成文件
            ImageIO.write(bufferedImage, suffix, imageFile);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bais != null) {
                    bais.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
