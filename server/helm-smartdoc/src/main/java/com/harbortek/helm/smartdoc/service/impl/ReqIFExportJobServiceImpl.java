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

package com.harbortek.helm.smartdoc.service.impl;

import cn.hutool.core.io.file.FileNameUtil;
import com.harbortek.helm.smartdoc.config.SmartDocMessages;
import com.harbortek.helm.smartdoc.constants.ReqIFDataTypes;
import com.harbortek.helm.smartdoc.dao.ReqIFExportJobDao;
import com.harbortek.helm.smartdoc.entity.ReqIFExportJobEntity;
import com.harbortek.helm.smartdoc.exporter.reqif.hanlder.ReqIFDocumentHandler;
import com.harbortek.helm.smartdoc.exporter.reqif.mapping.ReqIFTrackerMapping;
import com.harbortek.helm.smartdoc.importer.reqif.rules.ReqIFFieldMapping;
import com.harbortek.helm.smartdoc.service.ReqIFExportJobService;
import com.harbortek.helm.smartdoc.vo.ReqIFExportJobVo;
import com.harbortek.helm.system.service.FileService;
import com.harbortek.helm.tracker.constants.InternalTrackers;
import com.harbortek.helm.tracker.constants.SystemFields;
import com.harbortek.helm.tracker.entity.block.DocBlock;
import com.harbortek.helm.tracker.entity.project.PageSettingTracker;
import com.harbortek.helm.tracker.service.*;
import com.harbortek.helm.tracker.vo.ProjectVo;
import com.harbortek.helm.tracker.vo.block.DocVo;
import com.harbortek.helm.tracker.vo.items.TrackerItemVo;
import com.harbortek.helm.tracker.vo.link.TrackerLinkTypeVo;
import com.harbortek.helm.tracker.vo.link.TrackerLinkVo;
import com.harbortek.helm.tracker.vo.pages.ProjectPageVo;
import com.harbortek.helm.tracker.vo.tracker.TrackerVo;
import com.harbortek.helm.tracker.vo.tracker.fields.TrackerField;
import com.harbortek.helm.util.DataUtils;
import com.harbortek.helm.util.ZipUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Service
@Slf4j
public class ReqIFExportJobServiceImpl implements ReqIFExportJobService {

    @Autowired
    ReqIFExportJobDao reqIFExportJobDao;

    @Autowired
    FileService fileService;

    @Autowired
    private DocService docService;
    @Autowired
    private TrackerItemService trackerItemService;
    @Autowired
    private TrackerService trackerService;

    @Autowired
    private TrackerLinkTypeService trackerLinkTypeService;

    @Autowired
    private ProjectPageService projectPageService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    SmartDocMessages smartDocMessages;

    @Override
    public List<String> findReqIFFiles(String filePath) {
        List<String> list = new ArrayList<String>();
        try {
            File tempFile = File.createTempFile("req-", FilenameUtils.getExtension(filePath));
            InputStream inputStream = fileService.download(filePath);
            IOUtils.copy(inputStream, new FileOutputStream(tempFile, false));
            IOUtils.closeQuietly(inputStream);

            if (filePath.endsWith(".reqifz")) {
                try (ZipFile zipFile = new ZipFile(tempFile)) {
                    Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
                    while (enumeration.hasMoreElements()) {
                        ZipEntry entry = enumeration.nextElement();
                        if (entry.getName().endsWith(".reqif")) {
                            list.add(FileNameUtil.mainName(entry.getName()));
                        }
                    }
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return list;
    }

    @Override
    public void export(Long pageId, ReqIFExportJobVo job, HttpServletResponse response) {
        ProjectPageVo projectPage = projectPageService.findOneProjectPage(pageId);
        ProjectVo project = projectService.findOneProject(projectPage.getProjectId());
        DocVo smartDoc = docService.findDocByPageId(pageId);
        List<DocBlock> blocks = smartDoc.getBlocks();
        List<Long> ids = new ArrayList<Long>();
        for (DocBlock block : blocks) {
            ids.add(block.getData().getRefId());
        }

        List<TrackerVo> trackers = trackerService.findByProject(projectPage.getProjectId());
        Map<Long, TrackerVo> trackerMap = trackers.stream().collect(
                Collectors.toMap(TrackerVo::getId, Function.identity()));

        List<TrackerItemVo> trackerItems = trackerItemService.findTrackerItemByIds(ids);
        Map<Long, TrackerItemVo> trackerItemMap = trackerItems.stream().collect(
                Collectors.toMap(TrackerItemVo::getId, Function.identity()));

        List<TrackerLinkTypeVo> linkTypes = trackerLinkTypeService.findLinkTypes(projectPage.getProjectId());

        List<TrackerLinkVo> links = trackerItemService.findTrackerLinksByItemIds(ids);

        List<ReqIFTrackerMapping> mappings = job.getMappings();


        try {
            File tempDir = Files.createTempDirectory("reqif").toFile();
            tempDir.mkdirs();
            new File(tempDir,"files").mkdirs();


            //已有reqifz文件，先解压
            if (job.getExportType() == 1 || job.getExportType() == 2) {
                File tempFile = File.createTempFile("req-", ".reqifz");
                InputStream inputStream = fileService.download(job.getReqIFZipFilePath());
                IOUtils.copy(inputStream, new FileOutputStream(tempFile, false));
                IOUtils.closeQuietly(inputStream);
                ZipUtils.unzip(tempFile.getAbsolutePath(), tempDir.getAbsolutePath());
            }
            //需要覆盖或创建的reqif文件
            File reqIfFile = new File(tempDir, job.getReqIFFileName());
            ReqIFDocumentHandler handler = new ReqIFDocumentHandler(tempDir, reqIfFile, project, trackerMap,
                                                                    trackerItemMap,
                                                                    linkTypes, links);

            if (reqIfFile.exists()) {
                handler.loadDocument();
                handler.updateDocumentFromBlocks(blocks, mappings);
                handler.saveDocument();

            } else {
                String documentName = smartDoc.getName();
                handler.createDocument(documentName);
                handler.updateDocumentFromBlocks(blocks, mappings);
                handler.saveDocument();
            }

            //download zip file
            File tempZipFile = File.createTempFile("req-", ".reqifz");
            ZipUtils.zip(tempDir.getAbsolutePath(), tempZipFile.getAbsolutePath());

            String fileName = null;
            if (StringUtils.isNotEmpty(job.getReqIFZipFileName())) {
                fileName = job.getReqIFZipFileName();
            }else{
                fileName = FilenameUtils.getBaseName(job.getReqIFFileName()) + ".reqifz";
            }
            String contentDisposition = "attachment; filename=\"" + fileName + "\"; filename*=UTF-8''"
                    + URLEncoder.encode(fileName);

            response.setHeader("Content-disposition", contentDisposition);
            response.setHeader(HttpHeaders.CONTENT_TYPE,
                               "application/zip");
            IOUtils.copy(new FileInputStream(tempZipFile), response.getOutputStream());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public ReqIFExportJobVo loadExportJob(Long pageId) {
        ReqIFExportJobVo job = DataUtils.toVo(reqIFExportJobDao.findJobByPageId(pageId), ReqIFExportJobVo.class);
        if (job.getMappings().isEmpty()) {
            ProjectPageVo pageVo = projectPageService.findOneProjectPage(pageId);

            List<Long> ids = new ArrayList<>();
            ids.add(pageVo.getTracker().getId());
            ids.addAll(pageVo.getPageSettingTrackers().stream().map(PageSettingTracker::getId).distinct()
                             .collect(Collectors.toList()));

            List<TrackerVo> trackerVos = new ArrayList<>();
            trackerVos.add(InternalTrackers.HEADING);
            trackerVos.addAll(trackerService.findTrackerByIds(ids));

            List<ReqIFTrackerMapping> mappings = new ArrayList<>();
            for (TrackerVo trackerVo : trackerVos) {
                ReqIFTrackerMapping trackerMapping = new ReqIFTrackerMapping();
                trackerMapping.setTarget(trackerVo.getId());
                trackerMapping.setSpecTypeName(trackerVo.getName());

                List<TrackerField> trackerFields = trackerVo.getTrackerFields();
                for (TrackerField trackerField : trackerFields) {
                    if (SystemFields.NAME.equals(trackerField.getSystemProperty())) {
                        ReqIFFieldMapping fieldMapping = new ReqIFFieldMapping();
                        fieldMapping.setTrackerFieldId(trackerField.getId());
                        fieldMapping.setReqIFAttributeName("ReqIF.ChapterName");
                        fieldMapping.setType(ReqIFDataTypes.STRING);
                        trackerMapping.getFieldMappings().add(fieldMapping);
                    } else if (SystemFields.DESCRIPTION.equals(trackerField.getSystemProperty())) {
                        ReqIFFieldMapping fieldMapping = new ReqIFFieldMapping();
                        fieldMapping.setTrackerFieldId(trackerField.getId());
                        fieldMapping.setType(ReqIFDataTypes.XHTML);
                        fieldMapping.setReqIFAttributeName("ReqIF.Text");
                        trackerMapping.getFieldMappings().add(fieldMapping);
                    } else if (SystemFields.ITEM_NO.equals(trackerField.getSystemProperty())) {
                        ReqIFFieldMapping fieldMapping = new ReqIFFieldMapping();
                        fieldMapping.setTrackerFieldId(trackerField.getId());
                        fieldMapping.setReqIFAttributeName("ReqIF.ForeignID");
                        fieldMapping.setType(ReqIFDataTypes.STRING);
                        trackerMapping.getFieldMappings().add(fieldMapping);
                    } else if (SystemFields.CREATE_BY.equals(trackerField.getSystemProperty())) {
                        ReqIFFieldMapping fieldMapping = new ReqIFFieldMapping();
                        fieldMapping.setTrackerFieldId(trackerField.getId());
                        fieldMapping.setReqIFAttributeName("ReqIF.ForeignCreatedBy");
                        fieldMapping.setType(ReqIFDataTypes.STRING);
                        trackerMapping.getFieldMappings().add(fieldMapping);
                    } else if (SystemFields.CREATE_DATE.equals(trackerField.getSystemProperty())) {
                        ReqIFFieldMapping fieldMapping = new ReqIFFieldMapping();
                        fieldMapping.setTrackerFieldId(trackerField.getId());
                        fieldMapping.setReqIFAttributeName("ReqIF.ForeignCreatedOn");
                        fieldMapping.setType(ReqIFDataTypes.DATETIME);
                        trackerMapping.getFieldMappings().add(fieldMapping);
                    }
                }
                mappings.add(trackerMapping);
            }
            job.setMappings(mappings);
        }
        return job;
    }

    @Override
    public void saveExportJob(Long pageId, ReqIFExportJobVo job) {
        job.setPageId(pageId);
        reqIFExportJobDao.saveJob(DataUtils.toEntity(job, ReqIFExportJobEntity.class));
    }
}
