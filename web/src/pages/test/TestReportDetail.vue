<template>
    <config-page :routes="routes()" style="padding:20px;">
        <div ref="print" id="pdfDom">
            <vxe-button style="float: right;" @click="exportPDF">下载</vxe-button>
            <h3>概览</h3>
            <a-form-model :layout="'inline'">
                <a-row :gutter="15">
                    <a-col :span="24">
                        <a-form-model-item label="项目名称">{{ report.project?.name }}</a-form-model-item>
                    </a-col>
                    <a-col :span="6">
                        <a-form-model-item label="测试人员">{{ report.owner?.name || '胡隽' }}</a-form-model-item>
                    </a-col>
                    <a-col :span="6">
                        <a-form-model-item label="测试开始时间">{{ report.planStartDate || '2023-11-22' }}</a-form-model-item>
                    </a-col>
                    <a-col :span="6">
                        <a-form-model-item label="测试结束时间">{{ report.planEndDate || '2023-12-01' }}</a-form-model-item>
                    </a-col>
                </a-row>
                <a-row :gutter="15">
                    <a-col :span="6">
                        <a-form-model-item label="用例总数">{{ report.totalCount }}</a-form-model-item>
                    </a-col>
                    <a-col :span="6">
                        <a-form-model-item label="通过总数">{{ report.passedCount }}</a-form-model-item>
                    </a-col>
                    <a-col :span="6">
                        <a-form-model-item label="失败总数">{{ report.failedCount }}</a-form-model-item>
                    </a-col>
                    <a-col :span="6">
                        <a-form-model-item label="通过率">{{ ((report.passedCount * 100 / report.totalCount) ||
        0).toFixed(2) }}
                            %</a-form-model-item>
                    </a-col>
                    <a-col :span="6">
                        <a-form-model-item label="失败率">{{ ((report.failedCount * 100 / report.totalCount) ||
        0).toFixed(2) }}
                            %</a-form-model-item>
                    </a-col>
                    <a-col :span="6">
                        <a-form-model-item label="关联工作项数">{{ relateItemData.length }}</a-form-model-item>
                    </a-col>


                </a-row>
            </a-form-model>
            <a-divider style="margin-top:0px;" />

            <a-row style="margin-top:10px;">
                <a-icon type="file" /><span style="margin-left:3px;color:#000">报告总结</span>
                <span style="border-left: 1px solid rgba(0,0,0,.07);height: 12px;margin: 0 8px;"></span>
                <vxe-button v-action="'PAGE_WRITE|' + pageId" v-show="report.resultDesc && !isShowEditor"
                    @click="isShowEditor = true" type="text" status="primary" content="编辑" />
                <div style="margin-top:10px;">
                    <vxe-button v-show="!report.resultDesc && !isShowEditor" @click="isShowEditor = true" size="mini"
                        content="添加内容" />
                    <div @click="editorInEditMode = true">
                        <simple-editor v-show="isShowEditor" ref="simpleEditor" v-model="formData.resultDesc"
                            :showToolbar="editorInEditMode" @focus="editorInEditMode = true" />
                    </div>
                    <div style="display: flex;justify-content: flex-end;margin-top: 10px;" v-show="editorInEditMode">
                        <div>
                            <a-button @click="cancelSimpleEditor">取消</a-button>
                            <a-button type="primary" @click="onSaveSimpleEditor">保存</a-button>
                        </div>
                    </div>
                    <div class="editor-box" v-if="formData.resultDesc && !isShowEditor" v-html="formData.resultDesc">
                    </div>
                </div>
            </a-row>
            <a-divider style="margin-top:20px;" />

            <div style="color:#000">测试运行执行情况</div>
            <vxe-table style="cursor: pointer;" :data="report.testRuns" :loading="loading"
                :row-config="{ isHover: true }">
                <vxe-column type="seq" width="60"></vxe-column>
                <vxe-column field="name" title="关联测试运行"></vxe-column>
                <vxe-column field="owner" title="执行人" width="120">
                    <template #default="{ row }">
                        {{ row.owner.name }}
                    </template>
                </vxe-column>
                <vxe-column field="passRate" title="通过率" width="120">
                    <template #default="{ row }">
                        {{ (row.passedCount * 100 / row.totalCount || 0).toFixed(2) }}%
                    </template>
                </vxe-column>
                <vxe-column field="executedCount" title="已测用例" width="120">
                    <template #default="{ row }">
                        {{ row.executedCount }}/{{ row.totalCount }}
                    </template>
                </vxe-column>
            </vxe-table>
            <a-divider style="margin-top:20px;" />

            <div style="color:#000">测试结果关联工作项</div>
            <vxe-table style="cursor: pointer;" :data="relateItemData" :loading="loading"
                :row-config="{ isHover: true }">
                <vxe-column field="" title="编号" :width="100">
                    <template #default="{ row }">
                        {{ currentProjectKeyName + '-' + row.itemNo }}
                    </template>
                </vxe-column>
                <vxe-column field="name" title="工作项名称">
                    <template #default="{ row }">
                        <div style="display: inline-flex;flex-direction: row;">
                            <div v-if="row?.icon" style="margin-right:5px;"><a-icon :component="row?.icon" /></div>
                            <span>{{ row.name }}</span>
                        </div>
                    </template>
                </vxe-column>
                <vxe-column field="tracker.name" title="工作项类型"></vxe-column>
                <vxe-column field="createBy.name" title="创建人">
                </vxe-column>
                <vxe-column field="createDate" title="创建日期">
                </vxe-column>
                <vxe-column field="owner.name" title="负责人：">

                </vxe-column>
                <vxe-column field="" title="优先级">
                    <template #default="{ row }">
                        <a-tag style="border:none"
                            :style="{ color: row.priority?.color, backgroundColor: row.priority?.backgroundColor }">{{
        row.priority?.name }}</a-tag>
                    </template>
                </vxe-column>
                <vxe-column field="status.name" title="状态" header-class-name="hidden-cell">
                    <template #default="{ row }">
                        <div v-if="row.status" class="transition-status">
                            <span class="ui-tag-status"
                                :style="{ color: row.meaning?.color, 'border-color': row.meaning?.color }">{{
        row.status?.name }}</span>
                        </div>
                    </template>
                </vxe-column>

            </vxe-table>
            <a-divider style="margin-top:20px;" />

            <div style="height:350px;">
                <div style="color:#000">测试用例结果分布</div>
                <div style="font-size:12px;color:#87888a">总共统计 {{ report.totalCount }} 条数据</div>
                <div id="charts" style="width:350px;height: 250px;margin-left:150px;"></div>
            </div>
            <a-divider style="margin-top:10px;" />
        </div>
    </config-page>
</template>

<script>
import HAvatar from '@/components/avatar/h-avatar.vue';
import ConfigPage from '@/components/config-page/ConfigPage.vue';
import ContentPage from '@/components/page/content/ContentPage.vue';
import { mapGetters } from "vuex";
import { Pie } from '@antv/g2plot';
import { findOneTestReport, updateTestReport, findDownstreamTrackerItems, countTestResultsGroupByResult } from '@/services/test/TestReportService'
import SimpleEditor from '@/components/editor/SimpleEditor.vue';
import { VXETable } from 'vxe-table';

import htmlToPdf from '@/utils/htmlToPdf.js'

export default {
    name: 'TestReportDetail',
    components: { ContentPage, HAvatar, ConfigPage, SimpleEditor },
    data() {
        return {
            loading: false,
            keyword: '',
            report: '',
            relateItemData: [],
            editorInEditMode: false,
            isShowEditor: false,
            formData: {
                resultDesc: undefined,
            },
            htmlTitle: "测试导出文件",
            chartData: [],

            routes: function () {
                return [{
                    name: "testReport", meta: {
                        icon: "blank",
                        title: "测试报告",
                        show: false,
                    },
                }, {
                    name: "testReportDetail",
                    meta: {
                        icon: "blank",
                        title: this.report.name || '测试报告',
                        show: false,
                    },
                }]
            },
        }
    },
    computed: {
        ...mapGetters("project", ["currentProjectKeyName"]),
        projectId() {
            return this.$route.params.projectId
        },
        pageId() {
            return this.$route.params.pageId
        },
        reportId() {
            return this.$route.params.reportId
        }
    },
    mounted() {
        this.loadData();
    },
    methods: {
        exportPDF() {
            htmlToPdf.getPdf('测试报告_' + this.report.name);
        },
        cancelSimpleEditor() {
            this.formData.resultDesc = this.report.resultDesc;
            this.editorInEditMode = false
            this.isShowEditor = false;
        },
        onSaveSimpleEditor() {
            this.report.resultDesc = this.formData.resultDesc;
            this.editorInEditMode = false
            this.isShowEditor = false;
            updateTestReport(this.report).then(res => {
                this.report = res;
                VXETable.modal.message({ content: '保存成功', status: 'success' })
            })
        },
        async loadData() {
            this.loading = true
            await findOneTestReport(this.reportId).then(res => {
                this.report = res

                countTestResultsGroupByResult(this.reportId).then(res => {
                    this.chartData = res;
                    this.drawChart();
                    this.loading = false
                })
            })


            this.loadRelatedItemData();
        },
        loadRelatedItemData() {
            findDownstreamTrackerItems(this.reportId).then(res => {
                this.relateItemData = res;
                console.log("itemIDs", this.relateItemData)
            })
        },
        drawChart() {
            const data = this.chartData;
            const piePlot = new Pie('charts', {
                forceFit: false,
                data: this.chartData,
                innerRadius: 0.5,
                radius: 0.9,
                angleField: 'count',
                colorField: 'name',
                // 根据数据项的属性动态返回颜色
                color: (name) => {
                    let v = data.filter(item => item.name === name.name)
                    if (v.length > 0) {
                        return v[0].color;
                        
                    }
                },
                statistic: {
                    title: false,
                    content: false,
                },
                label: false,

            });

            piePlot.render();
        },
    },
}
</script>

<style lang="less" scoped>
.editor-box {
    margin-left: 15px;

    /deep/ p {
        //v-html显示 p标签间隔
        margin: 0;
    }
}


.transition-status {
    min-width: 110px;
    display: flex;
    align-items: center;
    margin-right: 20px;
    white-space: nowrap;

    .ui-tag-status {
        max-width: 110px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        font-size: 12px;
        box-sizing: border-box;
        display: inline-block;
        height: 20px;
        line-height: 18px;
        transition: border-color .2s;
        border: 1px solid;
        border-radius: 20px;
        padding: 0px 8px;
    }
}
</style>