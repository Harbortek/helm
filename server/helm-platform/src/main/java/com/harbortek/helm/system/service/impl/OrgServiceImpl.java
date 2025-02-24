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

package com.harbortek.helm.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.harbortek.helm.system.dao.OrgDao;
import com.harbortek.helm.system.dao.UserDao;
import com.harbortek.helm.system.entity.OrgEntity;
import com.harbortek.helm.system.entity.UserEntity;
import com.harbortek.helm.system.service.FileService;
import com.harbortek.helm.system.service.OrgService;
import com.harbortek.helm.system.vo.OrgVo;
import com.harbortek.helm.util.*;
import com.harbortek.helm.util.excel.ExcelLog;
import com.harbortek.helm.util.excel.ExcelLogs;
import com.harbortek.helm.util.excel.ExcelUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
@Slf4j
public class OrgServiceImpl implements OrgService {
    @Autowired
    private OrgDao orgDao;
    @Autowired
    private UserDao userDao;

    @Autowired
    private FileService fileService;

    public OrgVo createOrganiztion(OrgVo org) {
        String maxCode = orgDao.findMAXCode(org.getParentId());
        if (maxCode == null) {
            OrgEntity parent = orgDao.findOneOrg(org.getParentId());
            if (parent == null) {
                maxCode = "000";
            } else {
                maxCode = parent.getHierarchyCode() + "000";
            }
        }
        org.setHierarchyCode(CodeUtils.autoCode(maxCode, 0, 3));
        //判断是否为父节
        if (org.getParentId() == null) {
            org.setParentId(0L);
        }
        OrgEntity orgEntity = DataUtils.toEntity(org, OrgEntity.class);
        orgDao.createOrg(orgEntity);

        LogUtils.log("系统管理", "部门管理", "部门 {0} 创建成功", org.getName());
        return DataUtils.toVo(orgEntity, OrgVo.class);
    }

    public void updateOrganiztion(OrgVo org) {
        OrgEntity oldOrg = orgDao.findOneOrg(org.getId());
        BeanUtil.copyProperties(org, oldOrg,
                                CopyOptions.create().ignoreNullValue().setIgnoreProperties(OrgEntity.Fields.parentId,
                                                                                           OrgEntity.Fields.childCount));
        orgDao.updateOrg(oldOrg);
        LogUtils.log("系统管理", "部门管理", "部门 {0} 更新成功", org.getName());
    }

    public boolean deleteOrganization(Long orgId) {
        OrgEntity org = orgDao.findOneOrg(orgId);
        Page<OrgVo> data = findOrganizations("", Pageable.unpaged());
        List<Long> orgIds = MenuTreeUtil.ChildOrdId(data.getContent(), orgId);
        Page<UserEntity> users = userDao.findUsers(null, Pageable.unpaged(), orgIds);
        if (ObjectUtils.isNotEmpty(users.getContent())) {
            return Boolean.FALSE;
        }

        if (org != null) {
            orgDao.deleteAllChild(org.getHierarchyCode());//删除子元素

            orgDao.deleteOrg(org);//删除本身

            List<OrgEntity> allBrother = orgDao.findBrother(org.getParentId(), org.getHierarchyCode());//查询所有兄弟
            if (allBrother != null) {
                String tempHierarchyCode = org.getHierarchyCode();
                for (int i = 0; i < allBrother.size(); i++) {
                    OrgEntity brother = allBrother.get(i);
                    String newHierCode = tempHierarchyCode;
                    tempHierarchyCode = allBrother.get(i).getHierarchyCode();
                    List<OrgEntity> childList = orgDao.findOrgByHierarchyCode(brother.getHierarchyCode());
                    orgDao.changeHierarchyCode(brother, newHierCode, childList);
                }
            }
            LogUtils.log("系统管理", "部门管理", "部门 {0} 删除成功", org.getName());
        }
        return Boolean.TRUE;
    }

    public OrgVo findOneOrganization(Long orgId) {
        return DataUtils.toVo(orgDao.findOneOrg(orgId), OrgVo.class);
    }

    public Page<OrgVo> findOrganizations(String keyword, Pageable pageable) {
        return DataUtils.toVo(orgDao.findOrgies(keyword, pageable), OrgVo.class);
    }

    public Page<OrgVo> findOrganizationsWithCount(Pageable pageable) {
        return DataUtils.toVo(orgDao.findOrgiesWithCount(pageable), OrgVo.class);
    }

    @Override
    public void moveUpOrganization(Long upOrgId) {
        OrgEntity upOrg = orgDao.findOneOrg(upOrgId);
        Long parentOrgId = upOrg.getParentId();
        OrgEntity siblingOrg = null;
        List<OrgEntity> children = orgDao.findByParent(parentOrgId);
        for (int i = children.size() - 1; i >= 0; i--) {
            OrgEntity org = children.get(i);
            if ((org.getId().longValue() == upOrgId.longValue()) && i > 0) {
                siblingOrg = children.get(i - 1);
                break;
            }
        }
        if (siblingOrg != null) {
            updateHierarchyCode(upOrg, siblingOrg);
        }
    }

    @Override
    public void moveDownOrganization(Long downOrgId) {
        OrgEntity downOrg = orgDao.findOneOrg(downOrgId);
        Long parentOrgId = downOrg.getParentId();
        OrgEntity siblingOrg = null;
        List<OrgEntity> children = orgDao.findByParent(parentOrgId);
        for (int i = 0; i < children.size(); i++) {
            OrgEntity org = children.get(i);
            if ((org.getId().longValue() == downOrgId.longValue()) && i < children.size() - 1) {
                siblingOrg = children.get(i + 1);
                break;
            }
        }
        if (siblingOrg != null) {
            updateHierarchyCode(siblingOrg, downOrg);
        }
    }

    public void updateHierarchyCode(OrgEntity upOrg, OrgEntity downOrg) {
        List<OrgEntity> upChild = orgDao.findOrgByHierarchyCode(upOrg.getHierarchyCode());
        List<OrgEntity> downChild = orgDao.findOrgByHierarchyCode(downOrg.getHierarchyCode());
        String upHierarchyCode = upOrg.getHierarchyCode();
        String downHierarchyCode = downOrg.getHierarchyCode();
        orgDao.changeHierarchyCode(upOrg, downHierarchyCode, upChild);
        orgDao.changeHierarchyCode(downOrg, upHierarchyCode, downChild);
    }

    public void updateHierarchyCodeByInset(Long childOrgId, Long parentOrgId) {
        OrgEntity childOrg = orgDao.findOneOrg(childOrgId);//需要嵌入的子元素
        OrgEntity parentOrg = orgDao.findOneOrg(parentOrgId);//需要被嵌入的父元素
        //在获得这些元素的各自的子元素
        List<OrgEntity> childOrgOfChild = orgDao.findOrgByHierarchyCode(childOrg.getHierarchyCode());
        List<OrgEntity> parentOrgOfChild = orgDao.findOrgByHierarchyCode(parentOrg.getHierarchyCode());

        Integer len = parentOrgOfChild.size();

        String parentHierarchyCode = parentOrg.getHierarchyCode();

        orgDao.changeHierarchyCodeByInset(childOrg, parentOrg, parentHierarchyCode, parentOrg.getId(), len,
                                          childOrgOfChild);

        if (childOrgOfChild != null && childOrgOfChild.size() > 1) {
            for (int i = 1; i < childOrgOfChild.size(); i++) {
                OrgEntity child = childOrgOfChild.get(i);
                updateHierarchyCodeByInset(child.getId(), child.getParentId());
            }
        }
    }

    public boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    public ExcelLogs importOrganizationsPreCheck(String uploadFile) {
        InputStream inputStream = null;
        ExcelLogs logs = new ExcelLogs();
        try {
            inputStream = fileService.download(uploadFile);

            List<Map> orgList =
                    (List<Map>) ExcelUtil.importExcel(Map.class, inputStream, "", logs, (Map org) -> {
                        String hierarchyCode = org.get("组织层次代码").toString();
                        OrgEntity existOrg = orgDao.findOneOrgByHierarchyCode(hierarchyCode);
                        String result = null;

                        if (!isNumeric(hierarchyCode)) {
                            result = MessageFormat.format("层级代码必须位数字,错误层级代码为 [{0}]", hierarchyCode);
                        }
                        if (existOrg != null) {
                            result = MessageFormat.format("组织层级代码 [{0}] 已存在", hierarchyCode);
                        }

                        return result;
                    });

            if (logs.getHasError()) {
                //将错误日志写入文件
                Map headers = new LinkedHashMap<>();
                headers.put("rowNum", "行号");
                headers.put("log", "错误");
                List<ExcelLog> logList = logs.getLogList();
                File tempFile = File.createTempFile("error", ".xls");
                FileOutputStream outputStream = new FileOutputStream(tempFile);
                ExcelUtil.exportExcel(headers, logList, outputStream, "yyy-MM-dd");
                IOUtils.closeQuietly(outputStream);

                String destPath = DateUtils.getCurrDate();
                FileInputStream inputStream1 = new FileInputStream(tempFile);
                String errorLogFilePath = fileService.upload(inputStream1, tempFile.getName(), destPath);
                logs.setErrorLogFilePath(errorLogFilePath);
                IOUtils.closeQuietly(inputStream1);


                logs.setTotalRowCount(orgList.size());
                logs.setErrorRowCount(logList.size());
                //只保留三条记录用作预览
                if (logList.size() > 3) {
                    for (int i = logList.size() - 1; i >= 3; i--) {
                        logList.remove(i);
                    }
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            logs.setHasError(true);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        return logs;
    }


    public void importOrganizations(String uploadFile) {
        InputStream inputStream = null;
        ExcelLogs logs = new ExcelLogs();
        try {
            inputStream = fileService.download(uploadFile);

            List<Map> orgList = (List<Map>) ExcelUtil.importExcel(Map.class, inputStream, "", logs);
            ArrayList<OrgEntity> entities = new ArrayList<>();

            int createCount = 0;
            int updateCount = 0;
            for (Map org : orgList) {
                OrgEntity orgEntity = new OrgEntity();
                BeanUtils.copyProperties(org, orgEntity);
//                orgEntity.setCreateDate(DateUtils.now());
//                orgEntity.setCreateBy(SecurityUtils.getCurrentUser().getId());
                orgEntity.setName(ChineseStringUtils.normalize(org.get("组织名称").toString()));
                orgEntity.setHierarchyCode(ChineseStringUtils.normalize(org.get("组织层次代码").toString()));
                if (ObjectUtils.isNotEmpty(org.get("parentId"))) {
                    orgEntity.setParentId(0L);
                }
                orgEntity.setId(IDUtils.getId());
                orgDao.createOrg(orgEntity);
                entities.add(orgEntity);
                createCount++;
            }

            for (OrgEntity orgEntity : entities) {
                String hierarchyCode = orgEntity.getHierarchyCode();
                String parentCode = StringUtils.substring(hierarchyCode, 0, hierarchyCode.length() - 3);
                OrgEntity parent = orgDao.findOneOrgByHierarchyCode(parentCode);
                if (parent != null) {
                    orgEntity.setParentId(parent.getId());
                    orgDao.updateOrg(orgEntity);
                    updateCount++;
                }
            }
            LogUtils.log("系统管理", "部门管理", "部门导入成功，共新增{0}条记录，更新{1}条记录", createCount,
                         updateCount);
        } catch (Exception ex) {
            ex.printStackTrace();
            logs.setHasError(true);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    @Override
    public Boolean checkExistsByName(String name) {
        return orgDao.checkExistsByName(name);
    }

    public void batchUpdateParentIdAndHcode(List<Map> content) {
        List<Map> sourceList = new ArrayList<>();

        int index = 0;
        for (Map tmp : content) {
            index++;
            List<Map> childrenList = (List<Map>) tmp.get("childList");
            Long pid = 0L;
            String hierarchyCode = String.format("%03d", index);
            tmp.put("hierarchyCode", hierarchyCode);
            tmp.put("pid", pid);

            Long orgId = Long.valueOf(tmp.get("id").toString());

            //            orgService.updateParentIdAndHcode(companyId, orgId, pid, hierarchyCode);
            sourceList.add(tmp);

            traverseOrgList(orgId, childrenList, hierarchyCode, sourceList);
        }
        //按照sourceList批量更新所有org
        //System.out.println(sourceList);
//		orgDao.batchUpdateParentIdAndHcode(companyId, sourceList);
    }

    private void traverseOrgList(Long parentId, List<Map> childrenList, String parentHierarchyCode,
                                 List<Map> sourceList) {
        int index = 0;
        for (Map tmp : childrenList) {
            index++;
            tmp.put("pid", parentId);
            Long orgId = Long.valueOf(tmp.get("id").toString());
            List<Map> subList = (List<Map>) tmp.get("childList");

            String hCode = parentHierarchyCode + String.format("%03d", index);
            tmp.put("hierarchyCode", hCode);
//            System.out.println(hCode);

//            orgService.updateParentIdAndHcode(companyId, orgId, parentId, hCode);

            sourceList.add(tmp);
            traverseOrgList(orgId, subList, hCode, sourceList);
        }
    }
}
