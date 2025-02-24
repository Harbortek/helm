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

package com.harbortek.helm.tracker.dao;

import cn.hutool.core.comparator.CompareUtil;
import com.harbortek.helm.common.dao.BaseJdbcDao;
import com.harbortek.helm.tracker.constants.PageContexts;
import com.harbortek.helm.tracker.constants.PageScopes;
import com.harbortek.helm.tracker.entity.smartpage.SmartPageEntity;
import com.harbortek.helm.tracker.vo.smartpage.*;
import com.harbortek.helm.tracker.vo.smartpage.filter.DataFilter;
import com.harbortek.helm.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class SmartPageDao extends BaseJdbcDao {

    public Long getObjectId(Long pageId){
        SmartPageEntity entity = findById(pageId, SmartPageEntity.class);
        return entity.getObjectId();
    }

    public String getScope(Long pageId){
        SmartPageEntity entity = findById(pageId, SmartPageEntity.class);
        return entity.getScope();
    }


    public void updatePageDefinition(Long pageId, PageDefinitionVo pageDefinition) {
        SmartPageEntity entity = findById(pageId, SmartPageEntity.class);
        assert entity != null;
        entity.setDefinition(JsonUtils.toJSONString(pageDefinition));
        save(entity);
    }

    public PageDefinitionVo findPageDefinition(Long pageId) {
        SmartPageEntity entity = findById(pageId, SmartPageEntity.class);
        if (entity == null || StringUtils.isEmpty(entity.getDefinition())) {
            return  PageDefinitionVo.builder().id(pageId).build();
        }
        PageDefinitionVo pageDefinitionVo = JsonUtils.toObject(entity.getDefinition(), PageDefinitionVo.class);
        if (pageDefinitionVo==null){
            return  PageDefinitionVo.builder().id(pageId).name(entity.getName()).build();
        }
        pageDefinitionVo.setName(entity.getName());
        return pageDefinitionVo;
    }

    public DatasetVo createDataSet(Long pageId, DatasetVo datasetVo) {
        PageDefinitionVo pageDefinition = findPageDefinition(pageId);
        List<DatasetVo> dataSetVos = pageDefinition.getDatasets();
        if (dataSetVos==null){
            dataSetVos = new ArrayList<>();
            pageDefinition.setDatasets(dataSetVos);
        }

        datasetVo.setId(IDUtils.getId());
        dataSetVos.add(datasetVo);

        updatePageDefinition(pageId, pageDefinition);
        return datasetVo;
    }


    public void updateDataSet(Long pageId, DatasetVo datasetTable) {
        PageDefinitionVo pageDefinition = findPageDefinition(pageId);
        List<DatasetVo> dataSetVos = pageDefinition.getDatasets();
        DatasetVo existedDataSet =
                dataSetVos.stream()
                          .filter(t -> {
                              return CompareUtils.compare(t.getId(), datasetTable.getId()) == 0;
                          })
                          .findFirst().orElse(null);
        if (existedDataSet != null) {
            BeanCopyUtils.copyWithoutNullProperties(datasetTable,existedDataSet);
            updatePageDefinition(pageId, pageDefinition);
        }
    }

    public void deleteDataSet(Long pageId, Long datasetId) {
        PageDefinitionVo pageDefinition = findPageDefinition(pageId);
        List<DatasetVo> dataSetVos = pageDefinition.getDatasets();

        dataSetVos =
                dataSetVos.stream().filter(s -> CompareUtil.compare(s.getId(), datasetId) != 0)
                          .collect(
                                  Collectors.toList());
        pageDefinition.setDatasets(dataSetVos);

        updatePageDefinition(pageId, pageDefinition);
    }

    public List<DatasetVo> findDatasets(Long pageId) {
        PageDefinitionVo pageDefinition = findPageDefinition(pageId);

        return pageDefinition.getDatasets();
    }

    public List<DatasetVo> findDatasets(Long pageId, List<Long> ids) {
        List<DatasetVo> datasetTables = findDatasets(pageId);
        return datasetTables.stream().filter(d -> ids.contains(d.getId())).collect(Collectors.toList());
    }

    public DatasetVo findOneDataset(Long pageId, Long datasetId) {
        PageDefinitionVo pageDefinition = findPageDefinition(pageId);
        List<DatasetVo> dataSetVos = pageDefinition.getDatasets();

        return dataSetVos.stream()
                 .filter(t -> {
                      return CompareUtils.compare(t.getId(), datasetId) == 0;
                  })
                 .findFirst().orElse(new DatasetVo());
    }


    public void createDatasetField(Long pageId, Long datasetId, DatasetField field) {
        PageDefinitionVo pageDefinition = findPageDefinition(pageId);
        List<DatasetVo> dataSetVos = pageDefinition.getDatasets();
        DatasetVo existedDataSet =
                dataSetVos.stream()
                          .filter(t -> {
                              return CompareUtils.compare(t.getId(), datasetId) == 0;
                          })
                          .findFirst().orElse(new DatasetVo());
        existedDataSet.getFields().add(field);

        updatePageDefinition(pageId, pageDefinition);
    }

    public void updateDatasetField(Long pageId, Long datasetId, DatasetField field) {
        PageDefinitionVo pageDefinition = findPageDefinition(pageId);
        List<DatasetVo> dataSetVos = pageDefinition.getDatasets();
        DatasetVo existedDataSet =
                dataSetVos.stream()
                          .filter(t -> {
                              return CompareUtils.compare(t.getId(), datasetId) == 0;
                          })
                          .findFirst().orElse(new DatasetVo());
        DatasetField existedField = existedDataSet.getFields().stream().filter(f -> {
            return CompareUtils.compare(f.getId(), field.getId()) == 0;
        }).findFirst().orElse(null);
        BeanCopyUtils.copyWithoutNullProperties(field, existedField);

        updatePageDefinition(pageId, pageDefinition);
    }

    public void deleteDatasetField(Long pageId, Long datasetId, Long fieldId) {
        PageDefinitionVo pageDefinition = findPageDefinition(pageId);
        List<DatasetVo> dataSetVos = pageDefinition.getDatasets();
        DatasetVo existedDataSet =
                dataSetVos.stream()
                          .filter(t -> {
                              return CompareUtils.compare(t.getId(), datasetId) == 0;
                          })
                          .findFirst().orElse(new DatasetVo());
        List<DatasetField> existedFields = existedDataSet.getFields().stream().filter(f -> {
            return CompareUtils.compare(f.getId(), fieldId) != 0;
        }).collect(Collectors.toList());
        existedDataSet.setFields(existedFields);

        updatePageDefinition(pageId, pageDefinition);
    }

    public List<DatasetField> findDatasetFields(Long pageId, Long datasetId) {
        PageDefinitionVo pageDefinition = findPageDefinition(pageId);
        List<DatasetVo> dataSetVos = pageDefinition.getDatasets();

        DatasetVo dataSetVo = ObjectUtils.findById(dataSetVos, datasetId, DatasetVo.class);
        return Objects.requireNonNull(dataSetVo).getFields();
    }

    public DatasetField findOneDatasetField(Long pageId, Long datasetId, Long fieldId) {
        List<DatasetField> fieldDTOS = findDatasetFields(pageId, datasetId);
        return fieldDTOS.stream().filter(f -> f.getId().equals(fieldId)).findFirst().get();
    }

//    public Collection<DataFilter> findParameters(Long pageId) {
//        PageDefinitionVo pageDefinition = findPageDefinition(pageId);
//
//        return pageDefinition.getPageParameters();
//    }

//    public DataFilter findParameter(Long pageId, Long id) {
//        PageDefinitionVo pageDefinition = findPageDefinition(pageId);
//        List<DataFilter> parameterVos = pageDefinition.getPageParameters();
//
//        return parameterVos.stream()
//                         .filter(t -> {
//                             return CompareUtils.compare(t.getId(), id) == 0;
//                         })
//                         .findFirst().orElse(new DataFilter());
//    }
//
//    public DataFilter createParameter(Long pageId, DataFilter parameter) {
//        PageDefinitionVo pageDefinition = findPageDefinition(pageId);
//        List<DataFilter> parameterVos = pageDefinition.getPageParameters();
//        if (parameterVos==null){
//            parameterVos = new ArrayList<>();
//            pageDefinition.setPageParameters(parameterVos);
//        }
//
//        parameter.setId(IDUtils.getId());
//        parameterVos.add(parameter);
//
//        updatePageDefinition(pageId, pageDefinition);
//        return parameter;
//    }
//
//    public DataFilter updateParameter(Long pageId, DataFilter parameter) {
//        PageDefinitionVo pageDefinition = findPageDefinition(pageId);
//        List<DataFilter> parameterVos = pageDefinition.getPageParameters();
//        DataFilter existed =
//                parameterVos.stream()
//                          .filter(t -> {
//                              return CompareUtils.compare(t.getId(), parameter.getId()) == 0;
//                          })
//                          .findFirst().orElse(null);
//        if (existed != null) {
//            BeanCopyUtils.copyWithoutNullProperties(parameter,existed);
//            updatePageDefinition(pageId, pageDefinition);
//        }
//        return parameter;
//    }
//
//    public void deleteParameter(Long pageId, Long id) {
//        PageDefinitionVo pageDefinition = findPageDefinition(pageId);
//        List<DataFilter> parameterVos = pageDefinition.getPageParameters();
//
//        parameterVos =
//                parameterVos.stream().filter(s -> CompareUtil.compare(s.getId(), id) != 0)
//                          .collect(
//                                  Collectors.toList());
//        pageDefinition.setPageParameters(parameterVos);
//
//        updatePageDefinition(pageId, pageDefinition);
//    }
//
//    public void batchUpdateParameters(Long pageId, List<DataFilter> parameters) {
//        PageDefinitionVo pageDefinition = findPageDefinition(pageId);
//        pageDefinition.setPageParameters(parameters);
//        updatePageDefinition(pageId, pageDefinition);
//    }

    public SmartPageEntity createSmartPage(SmartPageEntity entity) {
        return save(entity);
    }

    public SmartPageEntity updateSmartPage(SmartPageEntity entity) {
        return save(entity);
    }

    public void deleteSmartPage(Long id) {
        markAsDeleted(id, SmartPageEntity.class);
    }

    public SmartPageContext buildContext(Long pageId){
        String scope = getScope(pageId);
        Long objectId = getObjectId(pageId);
        SmartPageContext context = new SmartPageContext();
        context.setAttribute(PageContexts.SCOPE_KEY, scope);
        if (PageScopes.SCOPE_PROJECT.equals(scope)) {
            context.setAttribute(PageContexts.PROJECT_ID_KEY,objectId);
        }else if (PageScopes.SCOPE_PRODUCT_LINE.equals(scope)){
            context.setAttribute(PageContexts.REPORT_ID_KEY,objectId);
        }
        return context;
    }

    public void createSmartPages(List<SmartPageEntity> smartPages) {
        saveAll(smartPages);
    }
}
