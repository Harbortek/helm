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

package com.harbortek.helm.pipeline.jenkins;

import java.io.StringReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import com.harbortek.helm.pipeline.vo.BuildInfo;
import com.harbortek.helm.pipeline.vo.PipelineDetail;
import com.harbortek.helm.pipeline.vo.PipelineVo;
import com.harbortek.helm.pipeline.vo.Stage;
import com.harbortek.helm.pipeline.vo.Step;
import com.harbortek.helm.util.JsonUtils;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JenkinsApi {
    public static final String FLOW_GRAPH_ACTION = "org.jenkinsci.plugins.workflow.job.views.FlowGraphAction";
    public static final String FLOW_START_NODE = "org.jenkinsci.plugins.workflow.graph.FlowStartNode";
    public static final String STEP_START_NODE = "org.jenkinsci.plugins.workflow.cps.nodes.StepStartNode";
    public static final String STEP_ATOM_NODE = "org.jenkinsci.plugins.workflow.cps.nodes.StepAtomNode";
    public static final String STEP_END_NODE = "org.jenkinsci.plugins.workflow.cps.nodes.StepEndNode";
    public static final String FLOW_END_NODE = "org.jenkinsci.plugins.workflow.graph.FlowEndNode";
    String serverUrl;
    String username;
    String password;

    RestTemplate restTemplate;

    /**
     * 构造函数
     * @param serverUrl
     * @param username
     * @param password
     */
    public JenkinsApi(String serverUrl, String username, String password) {
        this.serverUrl = StringUtils.removeEnd(serverUrl, "/");
        this.username = username;
        this.password = password;
        this.restTemplate = getRestTemplate();
    }

    /**
     * 获取 RestTemplate
     * @return
     */
    private RestTemplate getRestTemplate() {
        if (serverUrl.toUpperCase().startsWith("HTTPS")) {
            return JenkinsRequestUtils.httpsRestTemplate(username, password);
        } else {
            return JenkinsRequestUtils.httpRestTemplate(username, password);
        }
    }

    /**
     * 获取版本号
     * @return
     */
    public String getVersion() {
        HttpHeaders headers = restTemplate.headForHeaders(this.serverUrl);
        return headers.getFirst("X-Jenkins");
    }

    /**
     * 获取构建信息
     * @return
     */
    public List<PipelineVo> findPipelines() {
        String url = this.serverUrl + "/api/json";
        String responseBody = restTemplate.getForObject(url, String.class);
        JSONObject jsonObject = JsonUtils.parseObject(responseBody);
        JSONArray jobsArray = jsonObject.getJSONArray("jobs");

        return jobsArray.stream().map(item -> {
            PipelineVo pipeline = new PipelineVo();
            pipeline.setName(((JSONObject) item).getStr("name"));
            pipeline.setUrl(((JSONObject) item).getStr("url"));
            pipeline.setMultiBranch("org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject".equals(
                    ((JSONObject) item).getStr("_class")));
            return pipeline;
        }).collect(Collectors.toList());
    }

    /**
     * 获取构建信息
     * @param url
     * @return
     */
    public PipelineDetail findPipeline(String url) {
        String fullUrl = url + "/api/json?depth=2";
        String responseBody = restTemplate.getForObject(fullUrl, String.class);

        return JsonUtils.toObject(responseBody, PipelineDetail.class);
    }

    /**
     * 获取构建信息
     * @param pipeline
     * @param buildInfo
     * @return
     */
    public List<Stage> findStages(PipelineDetail pipeline, BuildInfo buildInfo) {
        String fullUrl =
                buildInfo.getUrl() + "/api/json?tree=actions[nodes[iconColor,running,displayName,id,parents,url]]";
        String responseBody = restTemplate.getForObject(fullUrl, String.class);
        JSONObject jsonObject = JSONUtil.parseObj(responseBody);
        JSONArray actions = jsonObject.getJSONArray("actions");
        for (int i = 0; i < actions.size(); i++) {
            JSONObject action = actions.getJSONObject(i);
            String clz = action.getStr("_class");
            if (FLOW_GRAPH_ACTION.equals(clz)) {
                JSONArray nodes = action.getJSONArray("nodes");
                StringBuilder sb = new StringBuilder();
                int indent = 0;
                for (int j = 0; j < nodes.size(); j++) {
                    JSONObject nodeJSON = nodes.getJSONObject(j);
                    String nodeType = nodeJSON.getStr("_class");
                    if (FLOW_START_NODE.equals(nodeType)) {
                        sb.append("<flow ");
                        sb.append("id=").append("\"").append(nodeJSON.getStr("id")).append("\" ");
                        sb.append("name=").append("\"").append(nodeJSON.getStr("displayName")).append("\" ");
                        sb.append("running=").append("\"").append(nodeJSON.getStr("running")).append("\"");
                        sb.append(">\n");
                        indent += 2;
                    } else if (STEP_START_NODE.equals(nodeType)) {
                        String nodeName = nodeJSON.getStr("displayName");
                        if ("Allocate node : Start".equals(nodeName) ||
                                "Allocate node : Body : Start".equals(nodeName) ||
                                "Stage : Start".equals(nodeName)) {
                            continue;
                        }
                        sb.append(StringUtils.repeat(' ', indent));
                        sb.append("<stage ");
                        sb.append("id=").append("\"").append(nodeJSON.getStr("id")).append("\" ");
                        sb.append("name=").append("\"").append(nodeJSON.getStr("displayName")).append("\" ");
                        sb.append("status=").append("\"").append(nodeJSON.getStr("iconColor")).append("\" ");
                        sb.append("url=").append("\"").append(nodeJSON.getStr("url")).append("\" ");
                        sb.append("running=").append("\"").append(nodeJSON.getStr("running")).append("\"");
                        sb.append(">\n");
                        indent += 2;
                    } else if (STEP_ATOM_NODE.equals(nodeType)) {
                        sb.append(StringUtils.repeat(' ', indent));
                        sb.append("<step ");
                        sb.append("id=").append("\"").append(nodeJSON.getStr("id")).append("\" ");
                        sb.append("name=").append("\"").append(nodeJSON.getStr("displayName")).append("\" ");
                        sb.append("status=").append("\"").append(nodeJSON.getStr("iconColor")).append("\" ");
                        sb.append("url=").append("\"").append(nodeJSON.getStr("url")).append("\" ");
                        sb.append("running=").append("\"").append(nodeJSON.getStr("running")).append("\"");
                        sb.append("/>\n");
                    } else if (STEP_END_NODE.equals(nodeType)) {
                        String nodeName = nodeJSON.getStr("displayName");
                        if ("Allocate node : Body : End".equals(nodeName) ||
                                "Allocate node : End".equals(nodeName) ||
                                "Stage : Body : End".equals(nodeName)) {
                            continue;
                        }
                        indent -= 2;
                        sb.append(StringUtils.repeat(' ', indent));
                        sb.append("</stage>\n");
                    } else if (FLOW_END_NODE.equals(nodeType)) {
                        indent -= 2;
                        sb.append("</flow>\n");
                    }
                }
                return getStages(sb.toString(), pipeline, buildInfo);
            }
        }
        return Collections.<Stage>emptyList();
    }

    /**
     * 获取构建信息
     * @param xml
     * @param pipeline
     * @param buildInfo
     * @return
     */
    private List<Stage> getStages(String xml,PipelineDetail pipeline, BuildInfo buildInfo) {
        SAXReader saxReader = new SAXReader();
        try {
            @Cleanup StringReader is = new StringReader(xml);
            Document document = saxReader.read(is);
            Node root = document.getRootElement();
            ArrayList<Stage> stages = new ArrayList<>();
            @SuppressWarnings("unchecked")
            List<Node> children = root.selectNodes("//stage/step");
            for (Node node : children) {
                Element stageEl = node.getParent();
                Stage stage =
                        stages.stream().filter(s -> s.getId().equals(stageEl.valueOf("@id"))).findFirst().orElse(null);
                if (stage == null) {
                    stage = new Stage();
                    stage.setName(stageEl.valueOf("@name"));
                    stage.setId(stageEl.valueOf("@id"));
                    stage.setRunning(Boolean.valueOf(stageEl.valueOf("@running")));
                    stage.setStatus(stageEl.valueOf("@status"));
                    stage.setUrl(stageEl.valueOf("@url"));
                    stage.setLogUrl(buildStageLogUrl(pipeline, buildInfo, stage));
                    @SuppressWarnings("unchecked")
                    List<Node> stepNodes = node.selectNodes("step");
                    for (Node child : stepNodes) {
                        Step step = new Step();
                        step.setName(child.valueOf("@name"));
                        step.setId(child.valueOf("@id"));
                        step.setRunning(Boolean.valueOf(child.valueOf("@running")));
                        step.setStatus(child.valueOf("@status"));
                        step.setUrl(child.valueOf("@url"));
                        step.setLogUrl(buildStepLogUrl(pipeline, buildInfo, stage, step));
                        stage.getSteps().add(step);
                    }
                    stages.add(stage);
                }
            }
            return stages;
        } catch (DocumentException e) {
            log.error(e.getMessage());
        }
        return new ArrayList<>();
    }

    /**
     * 获取构建信息
     * @param pipeline
     * @param buildInfo
     * @param stage
     * @return
     */
    private String buildStageLogUrl(PipelineDetail pipeline, BuildInfo buildInfo, Stage stage) {
        if (pipeline.isBranchJob()) {
            return null;
        } else {
            String pipelineName = pipeline.getName();
            int buildNo = buildInfo.getNumber();
            String nodeId = stage.getId();
            String url = MessageFormat.format(
                    "/blue/rest/organizations/jenkins/pipelines/{0}/runs" +
                            "/{1}/nodes/{2}/log/?start=0",
                    pipelineName, buildNo, nodeId);
            return url;
        }
    }
    
    /**
     * 获取构建信息
     * @param pipeline
     * @param buildInfo
     * @param stage
     * @param step
     * @return
     */
    private String buildStepLogUrl(PipelineDetail pipeline,BuildInfo buildInfo, Stage stage,Step step){
        if (pipeline.isBranchJob()){
            String fullName = pipeline.getFullName();
            String pipelineName = fullName.substring(0,fullName.lastIndexOf("/"));
            String  branchName = org.apache.commons.lang.StringUtils.substringAfter(fullName, "/");
            int buildNo = buildInfo.getNumber();
            String stepId = step.getId();
            String url = MessageFormat.format(
                    "/blue/rest/organizations/jenkins/pipelines/{0}/branches/{1}/runs" +
                            "/{2}/steps/{3}/log/?start=0",pipelineName,branchName,buildNo,stepId);
            return url;
        }else{
            String pipelineName = pipeline.getName();
            int buildNo = buildInfo.getNumber();
            String nodeId = stage.getId();
            String stepId = step.getId();
            String url = MessageFormat.format(
                    "/blue/rest/organizations/jenkins/pipelines/{0}/runs" +
                            "/{1}/nodes/{2}/steps/{3}/log/?start=0",pipelineName,buildNo,nodeId,stepId);
            return url;
        }
    }

    /**
     * 获取构建信息
     * @param url
     * @return
     */
    public Step findStep(String url){
        String fullUrl = serverUrl+"/"+ url + "/wfapi";
        String responseBody = restTemplate.getForObject(fullUrl, String.class);
        return JsonUtils.toObject(responseBody, Step.class);
    }

    /**
     * 获取构建信息
     * @param url
     * @return
     */
    public String findStepLog(String url){
        String fullUrl = serverUrl+"/"+url;
        return restTemplate.getForObject(fullUrl, String.class);
    }

    /**
     * 获取构建信息
     * @param fullName
     * @param buildNo
     * @return
     */
    public String findExecutionFullLog( String fullName, String buildNo) {
        String pipelineName = fullName.substring(0,fullName.lastIndexOf("/"));
        String  branchName = org.apache.commons.lang.StringUtils.substringAfter(fullName, "/");
        if (StringUtils.isNotEmpty(branchName)){
            String url = MessageFormat.format(
                    "/blue/rest/organizations/jenkins/pipelines/{0}/branches/{1}/runs" +
                            "/{2}/log/?start=0",pipelineName,branchName,buildNo);
            String fullUrl = serverUrl+"/"+url;
            return restTemplate.getForObject(fullUrl, String.class);
        }else{
            String url = MessageFormat.format(
                    "/blue/rest/organizations/jenkins/pipelines/{0}/runs" +
                            "/{1}/log/?start=0",pipelineName,buildNo);
            String fullUrl = serverUrl+"/"+url;
            return restTemplate.getForObject(fullUrl, String.class);
        }
    }
}
