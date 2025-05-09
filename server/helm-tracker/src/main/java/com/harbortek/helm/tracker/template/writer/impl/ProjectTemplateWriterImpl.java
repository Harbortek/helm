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

package com.harbortek.helm.tracker.template.writer.impl;

import com.harbortek.helm.common.vo.BaseIdentity;
import com.harbortek.helm.system.constants.IdentityTypes;
import com.harbortek.helm.system.vo.EnumItemVo;
import com.harbortek.helm.tracker.constants.InternalTrackers;
import com.harbortek.helm.tracker.constants.SystemFields;
import com.harbortek.helm.tracker.entity.block.HeaderBlockData;
import com.harbortek.helm.tracker.entity.block.TrackerItemBlockData;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.SlateElements;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.SlateNode;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.element.AttachmentSlateElement;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.element.CodeSlateElement;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.element.LinkSlateElement;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.element.SlateElement;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.element.image.ImageSlateElement;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.element.list.ListSlateElement;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.element.table.TableSlateElement;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.element.trackerItem.TrackerItemSlateElement;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.style.StyleedStyle;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.text.SlateText;
import com.harbortek.helm.tracker.entity.tracker.TrackerItemEntity;
import com.harbortek.helm.tracker.template.builder.TrackerXmlWriter;
import com.harbortek.helm.tracker.template.writer.ProjectTemplateWriter;
import com.harbortek.helm.tracker.util.ResourceUtils;
import com.harbortek.helm.tracker.vo.block.DocVo;
import com.harbortek.helm.tracker.vo.items.TrackerItemVo;
import com.harbortek.helm.tracker.vo.link.TrackerLinkVo;
import com.harbortek.helm.tracker.vo.pages.ProjectPageVo;
import com.harbortek.helm.tracker.vo.smartpage.SmartPageVo;
import com.harbortek.helm.tracker.vo.template.ProjectTemplateVo;
import com.harbortek.helm.tracker.vo.tracker.TrackerVo;
import com.harbortek.helm.tracker.vo.tracker.stateTransition.TrackerStatus;
import com.harbortek.helm.util.DataUtils;
import com.harbortek.helm.util.DateUtils;
import com.harbortek.helm.util.ObjectUtils;
import lombok.Cleanup;
import org.apache.commons.io.FilenameUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProjectTemplateWriterImpl implements ProjectTemplateWriter {
    private final Logger logger = LoggerFactory.getLogger(ProjectTemplateWriterImpl.class);

    @Value("${HELM_HOME}")
    private String helmHome;
    private final static String PROJECT_TEMPLATE_PATH = "templates";

    @Override
    public File writeFully(ProjectTemplateVo templateVo) {

        File templateDir = null;
        try {
//            Resource resource = new ClassPathResource("/project-template");
            String templatePath =
                    FilenameUtils.normalizeNoEndSeparator(helmHome)+File.separator+FilenameUtils.normalize(PROJECT_TEMPLATE_PATH);
            Resource resource =new FileSystemResource(templatePath);

            if(!resource.exists()){
                resource.getFile().mkdirs();
            }
            String path=templateVo.getName();
            templateDir = Files.createTempDirectory(resource.getFile().toPath(),path).toFile();
            writeBasicInfo(templateVo, templateDir);
            writeRoleMembers(templateVo,templateDir);
            writePermissionGrants(templateVo,templateDir);
            writeTrackers(templateVo,templateDir);
            writeWorkItems(templateVo,templateDir);
            writeWorkItemLinks(templateVo,templateDir);
            writePages(templateVo, templateDir);
            writeDocs(templateVo,templateDir);
            writeEnums(templateVo,templateDir);
            writeSmartPages(templateVo,templateDir);

        } catch (IOException ex) {
            logger.error("保存出错", ex);
        }
        return templateDir;
    }



    void writeBasicInfo(ProjectTemplateVo templateVo, File rootDir) {
        File templateProperties = new File(rootDir, "template.properties");
        Properties properties = new Properties();
        properties.put("name", templateVo.getName());
        if(ObjectUtils.isNotEmpty(templateVo.getMaxItemNo())){
            properties.put("maxItemNo", templateVo.getMaxItemNo().toString());
        }
        if(ObjectUtils.isNotEmpty(templateVo.getDescription())){
            properties.put("description", templateVo.getDescription());
        }
        if(ObjectUtils.isNotEmpty(templateVo.getIcon())){
            properties.put("icon",templateVo.getIcon());
        }
        if(ObjectUtils.isNotEmpty(templateVo.getTrackerPrefixToReplace())){
            properties.put("trackerPrefixToReplace", templateVo.getTrackerPrefixToReplace());
        }

        try {
            @Cleanup FileOutputStream os = new FileOutputStream(templateProperties);
            properties.store(os, "");
            os.close();
        } catch (IOException ex) {
            logger.error("保存出错", ex);
        }
    }

    void writeRoleMembers(ProjectTemplateVo templateVo, File rootDir) {
        File outputFile = new File(rootDir, "roles/roles.xml");

        Document document = DocumentHelper.createDocument();
        Element rolesElement = document.addElement("roles");

        templateVo.getRoles().forEach(role -> {
            Element roleElement = rolesElement.addElement("role");
            roleElement.addAttribute("name", role.getName());
            roleElement.addAttribute("specialRole", role.getSpecialRole().toString());
            roleElement.addAttribute("description", role.getDescription());
            //TODO 要写入成员信息
            if(ObjectUtils.isNotEmpty(role.getMembers())){
                Element membersElement = roleElement.addElement("members");
                role.getMembers().forEach(member ->{
                    Element memberElement = membersElement.addElement("member");
                    memberElement.addAttribute("name", member.getName());
                });
            }
            if(ObjectUtils.isNotEmpty(role.getPermissions())){
                Element membersElement = roleElement.addElement("permissions");
                role.getPermissions().forEach(perm ->{
                    Element memberElement = membersElement.addElement("permission");
                    memberElement.addAttribute("name", perm.getName());
                    perm.getPermission().forEach(p->{
                        memberElement.addAttribute(p, "true");
                    });
                });
            }

        });
        ResourceUtils.writeXml(outputFile, document);
    }

    void writePermissionGrants(ProjectTemplateVo templateVo, File rootDir) {
        File outputFile = new File(rootDir, "roles/permissions.xml");

        Document document = DocumentHelper.createDocument();
        Element permisssionsElement = document.addElement("permissions");
        templateVo.getPermissionGrants().forEach(grantVo -> {
            Element permissionElement = permisssionsElement.addElement("permission");
            permissionElement.addAttribute("name", grantVo.getPermissionName());
            writePermissionGrant(permissionElement,grantVo.getGranted());
        });
        ResourceUtils.writeXml(outputFile, document);
    }

    void writeTrackers(ProjectTemplateVo templateVo, File rootDir) {
        templateVo.getTrackers().forEach(tracker -> {
            File outputFile = new File(rootDir, "trackers/"+tracker.getName()+".xml");
            new TrackerXmlWriter(tracker).write(outputFile);
        });
    }

    void writeWorkItems(ProjectTemplateVo templateVo, File rootDir) throws IOException {
        Map<Long,String> filedMap = new HashMap<>();
        templateVo.getTrackers().forEach(tracker -> {
            tracker.getTrackerFields().forEach(field -> {
                filedMap.put(field.getId(),field.getName());
            });
        });

        List<TrackerItemVo> trackerItems = templateVo.getTrackerItems();
        for (TrackerItemVo trackerItem : trackerItems) {
            File outputFile = new File(rootDir, "work-items/"+trackerItem.getItemNo()+"/work-item.xml");
            Document document = DocumentHelper.createDocument();

            Element workItemElement = document.addElement("work-item");
            writeWorkItemFields(trackerItem,workItemElement,filedMap);
            ResourceUtils.writeXml(outputFile,document);
        }
    }

    void writeWorkItemLinks(ProjectTemplateVo templateVo, File rootDir) throws IOException {
        List<TrackerLinkVo> trackerLinks = templateVo.getTrackerLinks();
        File outputFile = new File(rootDir, "item-links/item-links.xml");
        Document document = DocumentHelper.createDocument();
        Element itemLinkElement = document.addElement("item-links");
        for (TrackerLinkVo link : trackerLinks) {
            if(ObjectUtils.isNotEmpty(link.getSourceItem())&&ObjectUtils.isNotEmpty(link.getTargetItem())){
                Element linkElement = itemLinkElement.addElement("link");
                linkElement.addAttribute("sourceItem", link.getSourceItem().getItemNo());
                linkElement.addAttribute("targetItem", link.getTargetItem().getItemNo());
                linkElement.addAttribute("linkType", link.getLinkType().getCode());
            }
        }
        ResourceUtils.writeXml(outputFile,document);
    }

    void writePages(ProjectTemplateVo templateVo, File rootDir) {
        Map<Long, String> trackerMap = templateVo.getTrackers().stream().collect(Collectors.toMap(TrackerVo::getId, TrackerVo::getName));

        Map<Long,String> filedMap = new HashMap<>();
        templateVo.getTrackers().forEach(tracker -> {
            tracker.getTrackerFields().forEach(field -> {
                filedMap.put(field.getId(),field.getName());
            });
        });

        File outputFile = new File(rootDir, "pages/pages.xml");
        Document document = DocumentHelper.createDocument();
        Element pagesElement = document.addElement("pages");

        List<ProjectPageVo> pages = templateVo.getPages();
        pages.forEach(page->{
            if(page.getFolder()){
                Element folderElement = pagesElement.addElement("folder");
                folderElement.addAttribute("name",page.getName());
                folderElement.addAttribute("icon",page.getIcon());
                page.getChildren().forEach(childrenPage->{
                    writePage(folderElement,childrenPage,rootDir,trackerMap,filedMap);
                });
            }else{
                writePage(pagesElement,page,rootDir,trackerMap,filedMap);
            }
        });
        ResourceUtils.writeXml(outputFile,document);
    }
    void writeDocs(ProjectTemplateVo templateVo, File rootDir) {

        Map<Long, String> trackerItemMap = templateVo.getTrackerItems().stream().filter(itemVo -> ObjectUtils.isNotEmpty(itemVo.getItemNo())).collect(Collectors.toMap(TrackerItemVo::getId, TrackerItemVo::getItemNo));

        List<DocVo> docVos = templateVo.getDocs();
        docVos.forEach(docVo->{
            File outputFile = new File(rootDir, "doc/"+docVo.getName()+".xml");
            Document document = DocumentHelper.createDocument();
            Element pagesElement = document.addElement("docs");

//            if(ObjectUtils.isNotEmpty(pageMap.get(docVo.getPageId()))){
                Element docElement = pagesElement.addElement("doc");
                docElement.addAttribute("page",docVo.getName());
                docElement.addAttribute("reversion",String.valueOf(docVo.getRevision()));
                Element blocksElement = docElement.addElement("elements");
                docVo.getElements().forEach(element->{
                    Element blockElement = blocksElement.addElement("element");
                    setBlockElement(element,blockElement,trackerItemMap);
                });
//            }
            ResourceUtils.writeXml(outputFile,document);
        });
    }
    void setBlockElement(SlateNode element, Element blockElement, Map<Long, String> trackerItemMap){
        blockElement.addAttribute("id",element.getId());
        blockElement.addAttribute("type",element.getType());
        String ref=null;
        if(SlateElements.TRACKER_ITEM.equals(element.getType())) {
            ref = ((TrackerItemSlateElement<?>) element).getRef();
        }else if(SlateElements.TRACKER_ITEM_TITLE.equals(element.getType())){
            ref = ((TrackerItemSlateElement.TrackerItemTitleSlateElement) element).getRef();
        }else if(SlateElements.TRACKER_ITEM_DESCRIPTION.equals(element.getType())){
            ref = ((TrackerItemSlateElement.TrackerItemDescriptionSlateElement) element).getRef();
        }else if(SlateElements.TRACKER_ITEM_EXTRA.equals(element.getType())){
            ref = ((TrackerItemSlateElement.TrackerItemExtraSlateElement) element).getRef();
        }else if(SlateElements.TEXT.equals(element.getType())){
            blockElement.addAttribute("text",((SlateText)element).getText());
        }else if(SlateElements.LIST.equals(element.getType())){
            blockElement.addAttribute("ordered",((ListSlateElement<?>)element).getOrdered().toString());
            blockElement.addAttribute("level",((ListSlateElement<?>)element).getLevel().toString());
        }else if(SlateElements.LINK.equals(element.getType())){
            blockElement.addAttribute("url",((LinkSlateElement<?>)element).getUrl());
            blockElement.addAttribute("target",((LinkSlateElement<?>)element).getTarget());
        }else if(SlateElements.IMAGE.equals(element.getType())){
            blockElement.addAttribute("src",((ImageSlateElement<?>)element).getSrc());
            blockElement.addAttribute("alt",((ImageSlateElement<?>)element).getAlt());
            blockElement.addAttribute("href",((ImageSlateElement<?>)element).getHref());
            ImageSlateElement.ImageStyle imageStyle = ((ImageSlateElement<?>) element).getStyle();
            if(ObjectUtils.isNotEmpty(imageStyle)){
                Element style = blockElement.addElement("style");
                style.addAttribute("width",imageStyle.getWidth());
                style.addAttribute("height",imageStyle.getHeight());
            }
        }else if(SlateElements.CODE.equals(element.getType())){
            blockElement.addAttribute("language",((CodeSlateElement<?>)element).getLanguage());
        }else if(SlateElements.ATTACHMENT.equals(element.getType())){
            blockElement.addAttribute("fileName",((AttachmentSlateElement<?>)element).getFileName());
            blockElement.addAttribute("link",((AttachmentSlateElement<?>)element).getLink());
        }else if(SlateElements.TABLE.equals(element.getType())){
            blockElement.addAttribute("width",((TableSlateElement<?>)element).getWidth());
        }else if(SlateElements.TABLE_CELL.equals(element.getType())){
            blockElement.addAttribute("isHeader", String.valueOf(((TableSlateElement.TableCellSlateElement<?>)element).getIsHeader()));
            blockElement.addAttribute("colSpan", String.valueOf(((TableSlateElement.TableCellSlateElement<?>)element).getColSpan()));
            blockElement.addAttribute("rowSpan", String.valueOf(((TableSlateElement.TableCellSlateElement<?>)element).getRowSpan()));
            blockElement.addAttribute("width", String.valueOf(((TableSlateElement.TableCellSlateElement<?>)element).getWidth()));
        }
        if(ObjectUtils.isNotEmpty(ref)){
            blockElement.addAttribute("ref",trackerItemMap.get(Long.parseLong(ref)));
        }
        if(ObjectUtils.isNotEmpty(element.getStyles())){
            Element style2 = blockElement.addElement("styles");
            element.getStyles().forEach(style->{
                Element style3 = style2.addElement("style");
                style3.addAttribute("bold",String.valueOf(style.getBold()));
                style3.addAttribute("code",String.valueOf(style.getCode()));
                style3.addAttribute("italic",String.valueOf(style.getItalic()));
                style3.addAttribute("underline",String.valueOf(style.getUnderline()));
                style3.addAttribute("through",String.valueOf(style.getThrough()));
                style3.addAttribute("sub",String.valueOf(style.getSub()));
                style3.addAttribute("sup",String.valueOf(style.getSup()));
            });
        }
        if(element instanceof SlateElement<?> element1){
            if(element1.getChildren()!=null){
                element1.getChildren().forEach(childElement->{
                    Element childElement1 = blockElement.addElement("children");
                    setBlockElement((SlateNode) childElement,childElement1,trackerItemMap);
                });
            }
        }
    }

    void writeSmartPages(ProjectTemplateVo templateVo, File rootDir) {

        List<SmartPageVo> smartPages = templateVo.getSmartPages();
        smartPages.forEach(smartPageVo->{
            File outputFile = new File(rootDir, "smart-page/"+smartPageVo.getName()+".xml");
            Document document = DocumentHelper.createDocument();
            Element smartElement = document.addElement("smart-page");
            smartElement.addAttribute("page",smartPageVo.getName());
            smartElement.addAttribute("scope",smartPageVo.getScope());
            Element definitionElement = smartElement.addElement("definition");
            definitionElement.setText("<![CDATA["+smartPageVo.getDefinition()+"]]>");
            ResourceUtils.writeXml(outputFile,document);
        });
    }

    void writeEnums(ProjectTemplateVo templateVo, File rootDir) {
        File outputFile = new File(rootDir, "enum/enums.xml");
        Document document = DocumentHelper.createDocument();
        Element pagesElement = document.addElement("enums");

        List<EnumItemVo> enumItems = templateVo.getEnumItems();
        enumItems.forEach(enumVo->{
            Element enumElement = pagesElement.addElement("enum");
            enumElement.addAttribute("category",enumVo.getCategoryName());
            enumElement.addAttribute("name",enumVo.getName());
            enumElement.addAttribute("color",enumVo.getColor());
            enumElement.addAttribute("backgroundColor",enumVo.getBackgroundColor());
            enumElement.addAttribute("description",enumVo.getDescription());
            enumElement.addAttribute("icon",enumVo.getIcon());
            enumElement.addAttribute("ordinary",enumVo.getOrdinary().toString());
            enumElement.addAttribute("system",enumVo.getSystem().toString());
            enumElement.addAttribute("code",enumVo.getCode());

        });
        ResourceUtils.writeXml(outputFile,document);
    }
    void writePage(Element element,ProjectPageVo page, File rootDir,Map<Long,String> trackerMap,Map<Long,String> filedMap){
        Element pageElement = element.addElement("page");
        pageElement.addAttribute("name",page.getName());
        pageElement.addAttribute("type",page.getType());
        if(ObjectUtils.isNotEmpty(page.getTracker())){
            pageElement.addAttribute("tracker",page.getTracker().getName());
        }
        if(ObjectUtils.isNotEmpty(page.getComponentType())){
            pageElement.addAttribute("componentType",page.getComponentType());
        }
//        if(ObjectUtils.isNotEmpty(page.getDefinition())){
//            String definitionPath="pages/"+page.getName()+"/"+page.getName()+"Spec.xml";
//            pageElement.addAttribute("definition",definitionPath);
//            File definitionFile = new File(rootDir, definitionPath);
//            Document definitiondocument = DocumentHelper.createDocument();
//            Element definitionElement = definitiondocument.addElement("definition");
//            definitionElement.setText("<![CDATA["+page.getDefinition()+"]]>");
//            ResourceUtils.writeXml(definitionFile,definitiondocument);
//        }
        if(ObjectUtils.isNotEmpty(page.getPageSettingTrackers())){
            Element trackersElement = pageElement.addElement("page-setting-trackers");
            page.getPageSettingTrackers().forEach(pageSettingTrackerVo->{
                Element element1 = trackersElement.addElement("page-setting-tracker");
                element1.addAttribute("tracker",trackerMap.get(pageSettingTrackerVo.getId()));
                element1.addAttribute("content",pageSettingTrackerVo.getContent());
                Element fieldIds = element1.addElement("fieldIds");
                pageSettingTrackerVo.getFieldIds().forEach(fieldId->{
                    Element fieldIdElement = fieldIds.addElement("fieldId");
                    fieldIdElement.setText(filedMap.get(fieldId));
                });
            });
        }
        if (ObjectUtils.isValid(page.getSmartPageId())){
            pageElement.addAttribute("smartPage",page.getName());
        }
        if (ObjectUtils.isValid(page.getSmartDocId())){
            pageElement.addAttribute("smartDoc",page.getName());
        }
    }


    private void writePermissionGrant(Element permissionElement, List<BaseIdentity> identityList) {
        identityList.forEach(identity -> {
            if (IdentityTypes.ROLE.equals(identity.getType())){
                Element grantElement =  permissionElement.addElement("grant-to-role");
                grantElement.setText(identity.getName());
            }else if (IdentityTypes.SPECIAL_ROLE.equals(identity.getType())){
                Element grantElement =  permissionElement.addElement("grant-to-special-role");
                grantElement.setText(identity.getName());
            }else if (IdentityTypes.USER.equals(identity.getType())){
                Element grantElement =  permissionElement.addElement("grant-to-user");
                grantElement.setText(identity.getName());
            }
        });
    }

    private void writeWorkItemFields(TrackerItemVo item, Element element, Map<Long, String> filedMap) {
        Element fieldElement;
        Object propertyValue;
        List<String> allSystemFields = SystemFields.ALL_SYSTEM_FIELDS;
        List<Long> internalIds = Arrays.asList(InternalTrackers.HEADING.getId(), InternalTrackers.PARAGRAPH.getId(),
                InternalTrackers.TITLE.getId());
        if(ObjectUtils.isNotEmpty(item.getTracker())){
            fieldElement = element.addElement("field");
            fieldElement.addAttribute("name", SystemFields.TRACKER);
            if(internalIds.contains(item.getTracker().getId())){
                fieldElement.setText(item.getTracker().getId().toString());
                item.setStatus(null);
                item.setMeaning(null);
            }else{
                fieldElement.setText(item.getTracker().getName());
            }
        }else{
            logger.info(item.getTracker().toString());
        }
        if(ObjectUtils.isNotEmpty(item.getOwner())){
            fieldElement = element.addElement("field");
            fieldElement.addAttribute("name", SystemFields.OWNER);
            fieldElement.setText(item.getOwner().getName());
        }
        for (String systemField : allSystemFields) {
            if(systemField.endsWith("Id")){
                continue;
            }
            if(internalIds.contains(item.getTracker().getId())
                    &&(Arrays.asList(SystemFields.STATUS,SystemFields.STATUS_TYPE).contains(systemField))){
                continue;
            }
            propertyValue = DataUtils.getProperty(item,systemField);
            if (ObjectUtils.isNotEmpty(propertyValue)) {
                if (propertyValue instanceof String || propertyValue instanceof Integer
                        || propertyValue instanceof Double) {
                    fieldElement = element.addElement("field");
                    fieldElement.addAttribute("name", systemField);
                    if(SystemFields.DESCRIPTION.equals(systemField)) {
                        fieldElement.setText("<![CDATA["+propertyValue+"]]>");
                    }else{
                        fieldElement.setText(propertyValue.toString());
                    }
                } else if (propertyValue instanceof Date
                        &&!SystemFields.CREATE_DATE.equals(systemField)) {
                    fieldElement = element.addElement("field");
                    fieldElement.addAttribute("name", systemField);
                    fieldElement.setText(DateUtils.toDefDatetimeString((Date)propertyValue));
                } else if (SystemFields.STATUS.equals(systemField)) {
                    fieldElement = element.addElement("field");
                    fieldElement.addAttribute("name", systemField);
                    fieldElement.setText(((TrackerStatus)propertyValue).getName());
                } else if (SystemFields.PRIORITY.equals(systemField)) {
                    fieldElement = element.addElement("field");
                    fieldElement.addAttribute("name", systemField);
                    fieldElement.setText(((EnumItemVo)propertyValue).getName());
                } else if (SystemFields.SEVERITY .equals(systemField)) {
                    fieldElement = element.addElement("field");
                    fieldElement.addAttribute("name", systemField);
                    fieldElement.setText(((EnumItemVo)propertyValue).getName());
                } else if(SystemFields.STATUS_TYPE.equals(systemField)){
                    fieldElement = element.addElement("field");
                    fieldElement.addAttribute("name", systemField);
                    fieldElement.setText(((EnumItemVo)propertyValue).getName());
                }
            }
        }
        if(ObjectUtils.isNotEmpty(item.getValues())){ //自定义属性
            Element fieldElementValues = element.addElement("field");
            fieldElementValues.addAttribute("name", TrackerItemEntity.Fields.values);
            item.getValues().forEach((key, value) -> {
                fieldElementValues.addElement("value").addAttribute("key", filedMap.get(Long.parseLong(key.toString())))
                        .addAttribute("value",value.toString());
            });
        }
    }
}
