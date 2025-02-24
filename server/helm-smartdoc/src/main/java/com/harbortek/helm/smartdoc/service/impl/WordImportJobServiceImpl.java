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

import com.harbortek.helm.common.exception.ServiceException;
import com.harbortek.helm.common.vo.IdNameReference;
import com.harbortek.helm.smartdoc.constants.ConditionMatchTypes;
import com.harbortek.helm.smartdoc.constants.ConditionsMatchScopes;
import com.harbortek.helm.smartdoc.dao.WordImportJobDao;
import com.harbortek.helm.smartdoc.entity.WordImportJobEntity;
import com.harbortek.helm.smartdoc.importer.word.analysis.WordDocumentProperties;
import com.harbortek.helm.smartdoc.importer.word.conditions.ConditionsRegistry;
import com.harbortek.helm.smartdoc.importer.word.conditions.IParagraphCondition;
import com.harbortek.helm.smartdoc.importer.word.extractors.ExtractorRegistry;
import com.harbortek.helm.smartdoc.importer.word.extractors.IValueExtractor;
import com.harbortek.helm.smartdoc.importer.word.rules.*;
import com.harbortek.helm.smartdoc.service.WordImportJobService;
import com.harbortek.helm.smartdoc.utils.DocUtils;
import com.harbortek.helm.smartdoc.utils.WordUtils;
import com.harbortek.helm.smartdoc.vo.WordImportJobVo;
import com.harbortek.helm.system.service.EnumService;
import com.harbortek.helm.system.service.FileService;
import com.harbortek.helm.tracker.constants.BlockTypes;
import com.harbortek.helm.tracker.entity.block.*;
import com.harbortek.helm.tracker.service.*;
import com.harbortek.helm.tracker.vo.ProjectVo;
import com.harbortek.helm.tracker.vo.items.TrackerItemVo;
import com.harbortek.helm.tracker.vo.pages.ProjectPageVo;
import com.harbortek.helm.tracker.vo.tracker.TrackerVo;
import com.harbortek.helm.tracker.vo.tracker.fields.TrackerField;
import com.harbortek.helm.util.DataUtils;
import com.harbortek.helm.util.IDUtils;
import com.harbortek.helm.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zwobble.mammoth.DocumentConverter;
import org.zwobble.mammoth.Result;

import java.io.InputStream;
import java.util.*;

@Service
@Slf4j
public class WordImportJobServiceImpl implements WordImportJobService {
    @Autowired
    WordImportJobDao wordImportJobDao;
    @Autowired
    private DocService docService;
    @Autowired
    FileService fileService;

    @Autowired
    ProjectService projectService;

    @Autowired
    ProjectPageService projectPageService;

    @Autowired
    EnumService enumService;

    @Autowired
    TrackerService trackerService;

    @Autowired
    TrackerItemService trackerItemService;

    @Override
    public WordDocumentProperties analysis(WordImportJobVo job) {
        List<DocBlock> blocks = DocUtils.parseBlocks(job.getBlocksJSON());
        WordDocumentProperties documentProperties = new WordDocumentProperties();
        WordUtils wordUtils = new WordUtils();
        WordDocumentProperties.Heading lastHeading = null;
        for (DocBlock block : blocks) {
            if (BlockTypes.HEADING.equals(block.getType())) {
                HeaderBlockData data = (HeaderBlockData) block.getData();
                WordDocumentProperties.Heading heading = new WordDocumentProperties.Heading();
                heading.setId(block.getId());
                heading.setName(block.getData().getText());
                heading.setStyle("heading" + data.getLevel());
                String titleLevel = String.valueOf(data.getLevel());
                if (job.isAutoNumber()) {
                    heading.setNumbering(wordUtils.getHeadingNumber(titleLevel));
                } else {
                    heading.setNumbering("");
                }
                documentProperties.getHeadings().add(heading);
                documentProperties.getStyles().add(heading.getStyle());
                lastHeading = heading;
            } else if (BlockTypes.PARAGRAPH.equals(block.getType())) {
                String html = ((ParagraphBlockData) block.getData()).getText();
                if (html.contains("<table")) {
                    Document htmlDocument = Jsoup.parse(html);
                    Elements tables = htmlDocument.select("table");
                    for (Element table : tables) {
                        WordDocumentProperties.Table t = new WordDocumentProperties.Table();
                        Element headerRow = table.selectFirst("tr");
                        if (headerRow != null) {
                            Elements columns = headerRow.select("td");
                            for (Element column : columns) {
                                t.getHeaders().add(column.text());
                            }
                            if (lastHeading != null) {
                                lastHeading.getTables().add(t);
                            }
                        }
                    }
                }
            }
        }
        return documentProperties;
    }

    @Override
    public WordImportJobVo findExistedJob(Long projectId, Long pageId) {
        List<WordImportJobEntity> jobs = wordImportJobDao.findJobs(projectId, pageId);
        if (jobs.isEmpty()) {
            return null;
        }
        WordImportJobVo job = DataUtils.toVo(jobs.stream().findFirst().get(), WordImportJobVo.class);
        job.setDocProps(analysis(job));
        return job;
    }

    @Override
    public WordImportJobVo createJob(WordImportJobVo job) {
        wordImportJobDao.deleteJobs(job.getProjectId(), job.getPageId());

        String wordFilePath = job.getFilePath();
        InputStream inputStream = null;

        try {
            inputStream = fileService.download(wordFilePath);

            List<DocBlock> blocks = new ArrayList<>();
            DocumentConverter converter = new DocumentConverter();
            Result<String> result = converter.convertToHtml(inputStream);
            Document document = Jsoup.parse(result.getValue());
            WordUtils wordUtils = new WordUtils();

            //自动添加标题
            TitleBlockData titleBlockData = new TitleBlockData();
            ProjectPageVo pageVo = projectPageService.findOneProjectPage(job.getPageId());
            titleBlockData.setText(pageVo.getName());
            DocBlock titleBlock = new DocBlock(IDUtils.getShortId(), BlockTypes.TITLE, titleBlockData);
            blocks.add(titleBlock);

            //遍历HTML
            for (Element element : document.body().children()) {
                //自动转换章节
                if (element.is("h1,h2,h3,h4,h5,h6")) {
                    HeaderBlockData data = new HeaderBlockData();
                    data.setText(element.text());
                    data.setLevel(Long.parseLong(element.tagName().replace("h", "")));
                    DocBlock block = new DocBlock(IDUtils.getShortId(), BlockTypes.HEADING, data);
                    blocks.add(block);
                } else {
                    if (!isToc(element)) {
                        ParagraphBlockData data = new ParagraphBlockData();
                        data.setText(element.outerHtml());
                        DocBlock block = new DocBlock(IDUtils.getShortId(), BlockTypes.PARAGRAPH, data);
                        blocks.add(block);
                    }
                }
            }

            WordImportJobEntity jobEntity = DataUtils.toEntity(job, WordImportJobEntity.class);
            jobEntity.setBlocksJSON(JsonUtils.toJSONString(blocks));
            jobEntity.setId(IDUtils.getId());
            wordImportJobDao.createJob(jobEntity);

            job = DataUtils.toVo(jobEntity, WordImportJobVo.class);
            job.setDocProps(analysis(job));
            return job;
        } catch (Exception e) {
            throw new ServiceException(e.getMessage());
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    @Override
    public WordImportJobVo updateJob(WordImportJobVo job) {
        Long projectId = job.getProjectId();
        ProjectVo project = projectService.findOneProject(projectId);

        List<DocBlock> newBlocks = new ArrayList<>();
        //遍历HTML
        List<DocBlock> blocks = DocUtils.parseBlocks(job.getBlocksJSON());
        ListIterator<DocBlock> iterator = blocks.listIterator();
        Stack<DocBlock> headings = new Stack<>();
        Long blockLevel = 0L;
        while (iterator.hasNext()) {
            DocBlock block = iterator.next();
            if (BlockTypes.PARAGRAPH.equals(block.getType())) {
                ExtractionRule rule = getMatchRule(block, headings, job);
                if (rule != null) {
                    //工作项开始
                    List<DocBlock> segments = new ArrayList<>();
                    segments.add(block);
                    while (iterator.hasNext()) {
                        DocBlock ele = iterator.next();
                        if (BlockTypes.HEADING.equals(ele.getType()) || getMatchRule(ele, headings, job) != null) {
                            iterator.previous();
                            break;
                        } else {
                            segments.add(ele);
                        }
                    }
                    //将本章节的内容进行处理
                    if (!segments.isEmpty()) {
                        newBlocks.addAll(processSegments(rule, segments, job, project));
                    }
                } else {
                    newBlocks.add(block);
                }
            } else {
                newBlocks.add(block);
            }

            if (BlockTypes.HEADING.equals(block.getType())) {
                if (((HeaderBlockData) block.getData()).getLevel() > blockLevel) {
                    headings.push(block);
                    printHeading(headings);
                    blockLevel = ((HeaderBlockData) block.getData()).getLevel();
                } else if (((HeaderBlockData) block.getData()).getLevel().equals(blockLevel)) {
                    headings.pop();
                    headings.push(block);
                    printHeading(headings);
                    blockLevel = ((HeaderBlockData) block.getData()).getLevel();
                } else {
                    int remains = ((HeaderBlockData) block.getData()).getLevel().intValue() - 1;
                    while (headings.size() > remains) {
                        headings.pop();
                    }
                    headings.push(block);
                    printHeading(headings);
                    blockLevel = ((HeaderBlockData) block.getData()).getLevel();
                }
            }
        }

        job.setBlocksJSON(JsonUtils.toJSONString(newBlocks));
        WordImportJobEntity jobEntity = DataUtils.toEntity(job, WordImportJobEntity.class);
        wordImportJobDao.updateJob(jobEntity);

        job = DataUtils.toVo(jobEntity, WordImportJobVo.class);
        job.setDocProps(analysis(job));
        return job;

    }

    private void printHeading(Stack<DocBlock> headings) {
        StringBuilder sb = new StringBuilder();
        for (DocBlock heading : headings) {
            sb.append(heading.getData().getText()).append("/");
        }
        log.debug(sb.toString());
    }

    @Override
    public void completeJob(WordImportJobVo job) {
        Long projectId = job.getProjectId();
        Long pageId = job.getPageId();
        List<DocBlock> blocks = DocUtils.parseBlocks(job.getBlocksJSON());

        docService.saveBlocksAndTrackerItems(projectId, pageId, blocks);

        wordImportJobDao.deleteJob(job.getId());
    }

    @Override
    public void deleteJob(Long id) {
        wordImportJobDao.deleteJob(id);
    }

    @Override
    public WordImportJobVo withdrawJob(Long id) {
        WordImportJobVo job = null;
        wordImportJobDao.withdraw(id);

        job = DataUtils.toVo(wordImportJobDao.findOneJob(id), WordImportJobVo.class);
        job.setDocProps(analysis(job));

        return job;
    }

    private boolean isToc(Element element) {
        return element.outerHtml().startsWith("<p><a href=\"#_Toc");
    }


    private List<DocBlock> processSegments(ExtractionRule rule, List<DocBlock> segments,
                                           WordImportJobVo job, ProjectVo project) {
        log.info("processSegments:{}", JsonUtils.toJSONString(segments));

        if (rule instanceof ParagraphExtractionRule) {
            return processParagraphs((ParagraphExtractionRule) rule, segments, job, project);
        } else {
            return processTables((TableExtractionRule) rule, segments, job, project);
        }
    }

    private List<DocBlock> processParagraphs(ParagraphExtractionRule rule, List<DocBlock> segments,
                                             WordImportJobVo job, ProjectVo project) {

        return List.of(buildTrackBlock(rule, project, segments));

    }

    private DocBlock buildTrackBlock(ParagraphExtractionRule rule, ProjectVo project, List<DocBlock> paragraphs) {
        TrackerItemVo trackerItem = createWorkItem(WordUtils.blockElements(paragraphs), rule,
                                                   project);
        TrackerItemBlockData itemBlockData = new TrackerItemBlockData();
        itemBlockData.setTrackerItem(docService.fillTrackerItemVo(trackerItem));
        itemBlockData.setName(trackerItem.getName());
        itemBlockData.setText(trackerItem.getDescription());
        return new DocBlock(IDUtils.getShortId(), String.valueOf(trackerItem.getTracker().getId()),
                            itemBlockData);
    }

    private List<DocBlock> processTables(TableExtractionRule rule, List<DocBlock> segments,
                                         WordImportJobVo job, ProjectVo project) {
        List<DocBlock> newBlocks = new ArrayList<>();
        List<DocBlock> paragraphs = new ArrayList<>();
        boolean workItemStart = false;
        ListIterator<DocBlock> iterator = segments.listIterator();
        TrackerItemVo currentTracker = null;
        boolean changed = false;
        while (iterator.hasNext()) {
            DocBlock block = iterator.next();
            if (block.getType().equals(BlockTypes.HEADING)) {
                //不做处理
                newBlocks.add(block);
            } else if (block.getType().equals(BlockTypes.PARAGRAPH)) {
                String html = WordUtils.html(WordUtils.blockElements(block));
                if (!html.contains("<table")) {
                    //判断是否是工作项的开始
                    if (!workItemStart && conditionsMatch(rule.getConditions(),
                                                          rule.getConditionsMatchType(),
                                                          WordUtils.blockElements(block))) {
                        //工作项开始段落
                        workItemStart = true;
                        paragraphs.clear();
                        paragraphs.add(block);
                    } else if (workItemStart) {
                        paragraphs.add(block);
                    }
                } else {
                    //是表格，开始转换
                    Document htmlDocument = Jsoup.parse(html);
                    Elements tables = htmlDocument.select("table");
                    for (Element table : tables) {

                        for (TrackerConversion conversion : rule.getConversions()) {
                            Element paragraph = null;
                            if (ConditionsMatchScopes.WHOLE_TABLE.equals(conversion.getConditionsMatchScope())) {
                                paragraph = table;
                            } else if (ConditionsMatchScopes.HEADER_ROW.equals(
                                    conversion.getConditionsMatchScope())) {
                                paragraph = table.selectFirst("tr");
                            }

                            if (conditionsMatch(conversion.getConditions(),
                                                conversion.getConditionsMatchType(),
                                                paragraph)) {
                                //满足转换条件
                                List<Element> elements = WordUtils.blockElements(paragraphs);
                                List<TrackerItemVo> trackerItems =
                                        tableToTrackerItem(conversion, table, project, elements);
                                if (!trackerItems.isEmpty()) {
                                    changed = true;
                                    for (TrackerItemVo trackerItem : trackerItems) {
                                        TrackerItemBlockData itemBlockData = new TrackerItemBlockData();
                                        itemBlockData.setTrackerItem(docService.fillTrackerItemVo(trackerItem));
                                        itemBlockData.setName(trackerItem.getName());
                                        itemBlockData.setText(trackerItem.getDescription());
                                        DocBlock itemBlock =
                                                new DocBlock(IDUtils.getShortId(),
                                                             String.valueOf(trackerItem.getTracker().getId()),
                                                             itemBlockData);
                                        newBlocks.add(itemBlock);
                                    }
                                    break;
                                }
                            } else {
                                newBlocks.add(block);
                            }
                        }
                    }

                    //表格同时也意味着工作项结束
                    workItemStart = false;
                    paragraphs.clear();
                }
            }

        }
        return newBlocks;
    }


    private TrackerItemVo createWorkItem(List<Element> paragraphs, ParagraphExtractionRule pRule, ProjectVo
            project) {
        TrackerVo tracker = trackerService.findOneTracker(pRule.getTarget());
        TrackerItemVo trackerItem = new TrackerItemVo();
        trackerItem.setProject(new IdNameReference<>(project));
        trackerItem.setTracker(new IdNameReference<>(tracker));
        trackerItem.setItemNo("000");

        if (pRule.isFirstParagraphAsTitle()) {
            trackerItem.setName(paragraphs.get(0).text());
        }

        for (Element paragraph : paragraphs) {
            StringBuilder sb = new StringBuilder();
            if (conditionsMatch(pRule.getNextParagraph().getConditions(),
                                pRule.getNextParagraph().getConditionsMatchType(), paragraph)) {
                sb.append(WordUtils.html(paragraph)).append("\n");
            }
            if (StringUtils.isNotEmpty(sb.toString())) {
                trackerItem.setDescription(sb.toString());
            }
        }

        if (!pRule.getActions().isEmpty()) {
            for (FieldExtractAction action : pRule.getActions()) {
                for (Element paragraph : paragraphs) {
                    if (conditionsMatch(action.getConditions(), action.getConditionsMatchType(), paragraph)) {
                        extractFiledValue(action, paragraph, trackerItem);
                    }
                }
            }
        }

        //TODO 对于工作项的未导入的属性设置默认值
        if (trackerItem.getOwner() == null) {
            trackerItem.setOwner(trackerItem.getCreateBy());
        }

        return trackerItem;
    }


    private void extractFiledValue(FieldExtractAction action, Element paragraph, TrackerItemVo
            trackerItem) {
        IValueExtractor extractor = ExtractorRegistry.getValueExtractor(action.getActionType());
        if (extractor != null) {
            Object value = extractor.extractValue(paragraph, action.getArgument());
            if (value != null) {
                trackerItemService.setFieldValue(trackerItem, action.getFieldId(), value);
            }
        }
    }

    private boolean paragraphInScope(List<String> source, List<DocBlock> headings) {
        for (DocBlock heading : headings) {
            if (source.contains(heading.getId())) {
                return true;
            }
        }
        return false;
    }

    private ExtractionRule getMatchRule(DocBlock block, List<DocBlock> parentHeadingBlocks, WordImportJobVo config) {
        for (ExtractionRule rule : config.getRules()) {
            if (rule instanceof ParagraphExtractionRule) {
                ParagraphExtractionRule pRule = (ParagraphExtractionRule) rule;
                if ("ALL".equals(pRule.getScope()) || paragraphInScope(pRule.getSource(), parentHeadingBlocks)) {
                    if (conditionsMatch(pRule.getConditions(), pRule.getConditionsMatchType(),
                                        WordUtils.blockElements(block))) {
                        return rule;
                    }
                }
            } else if (rule instanceof TableExtractionRule) {
                TableExtractionRule tRule = (TableExtractionRule) rule;
                if ("ALL".equals(tRule.getScope()) || paragraphInScope(tRule.getSource(), parentHeadingBlocks)) {
                    if (conditionsMatch(tRule.getConditions(), tRule.getConditionsMatchType(),
                                        WordUtils.blockElements(block))) {
                        return rule;
                    }
                }
            }
        }
        return null;
    }

    private boolean conditionsMatch(List<ConditionWithArgument> conditions, String conditionsMatchType,
                                    Element paragraph) {
        if (ConditionMatchTypes.ALL.equals(conditionsMatchType)) {
            for (ConditionWithArgument condition : conditions) {
                if (!conditionMatch(condition, paragraph)) {
                    return false;
                }
            }
            return true;
        } else if (ConditionMatchTypes.ANY.equals(conditionsMatchType)) {
            for (ConditionWithArgument condition : conditions) {
                if (conditionMatch(condition, paragraph)) {
                    return true;
                }
            }
        } else if (ConditionMatchTypes.ALWAYS.equals(conditionsMatchType)) {
            return true;
        }
        return false;
    }

    private boolean conditionMatch(ConditionWithArgument condition, Element paragraph) {
        IParagraphCondition paragraphCondition =
                ConditionsRegistry.getTextCondition(condition.getConditionId());
        if (paragraphCondition != null) {
            return paragraphCondition.isSatisfied(paragraph, condition.getArgument());
        }
        return false;
    }

    private List<TrackerItemVo> tableToTrackerItem(TrackerConversion conversion, Element table,
                                                   ProjectVo project, List<Element> paragraphs) {

        Element headerRow = table.selectFirst("tr");
        List<TrackerItemVo> trackerItems = new ArrayList<>();

        List<Element> rows = new ArrayList<>(table.select("tr"));
        if (rows.size() > 1) {
            rows.remove(0);
        }


        for (Element row : rows) {
            TrackerVo tracker = trackerService.findOneTracker(conversion.getTarget());
            TrackerItemVo trackerItem = new TrackerItemVo();
            trackerItem.setProject(new IdNameReference<>(project));
            trackerItem.setTracker(new IdNameReference<>(tracker));
            trackerItem.setItemNo("000");

            List<Element> headers = headerRow.select("td");
            Map<Long, Integer> indexMap = new HashMap<>();
            for (int i = 0; i < headers.size(); i++) {
                Element header = headers.get(i);
                String key = header.text();
                String field = conversion.getColumnMap().get(key);
                if (StringUtils.isNotEmpty(field)) {
                    Optional<TrackerField> trackerField =
                            tracker.getTrackerFields().stream()
                                   .filter(f -> Objects.equals(String.valueOf(f.getId()), field) ||
                                           Objects.equals(f.getSystemProperty(), field))
                                   .findFirst();
                    if (trackerField.isPresent()) {
                        indexMap.put(trackerField.get().getId(), i);
                    }
                }
            }

            Elements columns = row.select("td");
            for (Long field : indexMap.keySet()) {
                int index = indexMap.get(field);
                if (index < columns.size()) {
                    Element column = columns.get(index);
                    String value = column.text();
                    if (StringUtils.isNotEmpty(value)) {
                        trackerItemService.setFieldValue(trackerItem, field, value);
                    }
                }
            }

            if (!conversion.getActions().isEmpty()) {
                for (FieldExtractAction action : conversion.getActions()) {
                    if (conditionsMatch(action.getConditions(), action.getConditionsMatchType(), row)) {
                        extractFiledValue(action, row, trackerItem);
                    }
                }
            }

            trackerItems.add(trackerItem);
        }

        return trackerItems;
    }
}
