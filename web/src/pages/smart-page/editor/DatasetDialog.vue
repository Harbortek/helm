<template>
    <a-modal width="1400px" v-model="visiable" title="数据集" @ok="onOK" @cancel="onCancel">
        <div class="table-view">
            <a-row>
                <a-col span="12" style="height: 40px;">
                    <a-form-model ref="datasetForm" layout="horizontal" :model="formData" :rules="rules"
                        labelAlign="left" :labelCol="{ span: 7 }" :wrapperCol="{ span: 16, offset: 1 }">
                        <a-form-model-item label="数据集名称" prop="name">
                            <a-input v-model="formData.name" />
                        </a-form-model-item>
                        <!-- <a-form-model-item label="数据集获取方式" prop="type">
                            <a-select v-model="formData.type" placeholder="请选择数据集获取方式" @change="onTypeChange">
                                <a-select-option value="SQL" key="">SQL</a-select-option>
                                <a-select-option value="JavaScript" key="">JavaScript</a-select-option>
                                <a-select-option value="JSON" key="">JSON</a-select-option>
                            </a-select>
                        </a-form-model-item> -->
                    </a-form-model>
                </a-col>
            </a-row>

            <a-row>
                <div style="height: 52px;margin-top: 0px;">
                    <vxe-toolbar>
                        <template #buttons>
                            <span style="font-weight: 600;" v-if="formData.type === 'SQL'">SQL语句：</span>
                            <span style="font-weight: 600;" v-if="formData.type === 'JavaScript'">JavaScript语句：</span>
                            <span style="font-weight: 600;" v-if="formData.type === 'JSON'">JSON数据</span>
                        </template>
                        <template #tools>
                            <vxe-button status="default" @click="example">例子</vxe-button>
                        </template>
                    </vxe-toolbar>
                </div>
                <div class="code-container">
                    <div id="code-editor" ref="code-editor" class="code-editor" style=""></div>
                </div>
                <a-alert
                    message="可以使用诸如 [[PROJECT]PROJECT_NAME] [[TRACKER]TRACKER_NAME|SECOND_TRACKER_NAME|*] [[VERSION]*] [[SPRINT]*] 这样的虚拟表和诸如[[CURRENT_USER_ID]]的虚拟变量"
                    type="info" />
            </a-row>
            <div>
                <vxe-toolbar>
                    <template #buttons>

                    </template>
                    <template #tools>
                        <vxe-button status="primary" @click="executeSQL">预览</vxe-button>
                    </template>
                </vxe-toolbar>
                <a-tabs default-active-key="DATA_PREVIEW" :active-key="currentTab" @change="onTabChange"
                    style="margin-right: 15px;" size="small">
                    <template slot="tabBarExtraContent">
                        <div><span class="table-count">显示 100 行</span></div>
                    </template>
                    <a-tab-pane key="DATA_PREVIEW" tab="数据预览">
                        <div class="preview-datatable">
                            <vxe-table border height="300" :data="tableData" :column-config="{ resizable: true }"
                                :header-cell-style="tableHeaderStyle" :cell-style="tableItemStyle">
                                <vxe-column v-for="config in tableColumn" :key="config.key" :type="config.type"
                                    :field="config.field" :title="config.title" :fixed="config.fixed"
                                    :min-width="config.minWidth" :filters="config.filters" show-header-overflow
                                    show-overflow show-footer-overflow>
                                    <template #header="{ column }">
                                        <a-icon component="field_type_text" style="color: #222222;"
                                            v-show="column.type === 'TEXT'" />
                                        <a-icon component="field_type_number" style="color: cadetblue;"
                                            v-show="column.type === 'NUM'" />
                                        <a-icon component="date" style="color:cadetblue;"
                                            v-show="column.type === 'DATE'" />
                                        {{ column.title }}
                                    </template>

                                </vxe-column>
                            </vxe-table>
                        </div>
                    </a-tab-pane>
                    <a-tab-pane key="FIELD_SET" tab="字段设置">
                        <div>
                            <vxe-table border align="center" height="300" :data="tableColumn"
                                :header-cell-style="tableHeaderStyle" :cell-style="tableItemStyle"
                                :edit-config="{ trigger: 'click', mode: 'cell', icon: 'vxe-icon-edit', showStatus: true }">
                                <vxe-column type="seq" width="60px" title="序号"></vxe-column>
                                <vxe-column field="name" title="字段名称"></vxe-column>
                                <vxe-column field="title" title="标题" :edit-render="{ name: 'input' }">
                                    <template #edit="{ row }">
                                        <vxe-input v-model="row.title" type="text"></vxe-input>
                                    </template>
                                    <template #default="{ row }">
                                        {{ row.title }}
                                    </template>
                                </vxe-column>
                                <vxe-column field="type" title="类型" :edit-render="{}">
                                    <template #edit="{ row }">
                                        <vxe-select v-model="row.type" transfer>
                                            <vxe-option key="string" :value="'TEXT'"
                                                :label="formatType('TEXT')"></vxe-option>
                                            <vxe-option key="number" :value="'NUM'"
                                                :label="formatType('NUM')"></vxe-option>
                                            <vxe-option key="date" :value="'DATE'"
                                                :label="formatType('DATE')"></vxe-option>
                                        </vxe-select>
                                    </template>
                                    <template #default="{ row }">
                                        {{ formatType(row.type) }}
                                    </template>
                                </vxe-column>
                            </vxe-table>
                        </div>
                    </a-tab-pane>
                </a-tabs>
            </div>
        </div>
    </a-modal>
</template>
<script>
import Cookies from "js-cookie";
import {cloneDeep} from 'lodash'
import { getSQLPreview } from '@/services/smart-page/DatasetService.js'
import * as monaco from 'monaco-editor';
import MonacoEditorCopilot from 'monaco-editor-copilot';
export default {
    name: "DatasetDialog",
    components: {},
    data() {
        const that = this;
        const nameValidator = (rule, value, callback) => {
            let found = false;
            let i = 0;
            for (i = 0; i < that.datasetNames.length; i++) {
                if (that.datasetNames[i] === value) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return callback();
            } else {
                return callback(new Error("已存在同样名称的数据"));
            }
        };
        return {
            currentTab: 'DATA_PREVIEW',
            formData: {
                name: '',
                type: 'SQL',
            },
            rules: {
                name: [
                    { required: true, message: "请输入数据集名称", trigger: "blur" },
                    {
                        min: 2,
                        max: 20,
                        message: "长度在2到20之间",
                        trigger: "blur",
                    },
                    { validator: nameValidator, trigger: "change" },
                ],
            },

            loading: false,
            sql: '',
            tableColumn: [],
            tableData: [],
            tableHeaderStyle: {
                fontSize: '12px',
                color: '#606266',
                backgroundColor: '#ffffff',
                height: '36px'
            },
            tableItemStyle: {
                fontSize: '12px',
                color: '#606266',
                backgroundColor: '#ffffff',
                height: '36px'
            },
            monacoEditor: null,
        };
    },
    props: {
        isShowDialog: {
            required: true
        },
        editMode: {
            required: true
        },
        pageId: {
            required: true
        },
        dataset: {
            required: true
        },
        datasets: {
            required: true
        },
    },
    computed: {
        visiable: {
            get() {
                return this.isShowDialog
            },
            set(newValue) {
                return newValue
            }
        },
        datasetNames() {
            let names = []
            this.datasets.forEach(f => {
                if (f.id !== this.dataset.id) {
                    names.push(f.name)
                }
            })
            return names
        }
    },
    watch: {
        visiable: {
            handler: function (newVal, oldVal) {
                if (newVal) {
                    this.$nextTick(() => {
                        this.createEditor()
                        this.loadData();
                    })

                } else {
                    if (this.monacoEditor) {
                        this.monacoEditor.dispose();
                    }
                }
            }
        },
        tableData(valArr) {
            this.tableColumn.forEach((item) => {
                item.minWidth = Math.round(this.getMaxLength(item) + 40) + "px"; // 每列内容最大的宽度 + 表格的内间距(依据实际情况而定)
                item.fixed = false
            });
        },
    },
    methods: {
        createEditor() {
            const el = document.getElementById("code-editor")
            if (el) {
                this.monacoEditor = monaco.editor.create(el, {
                    value: this.rawContent, // 初始文字
                    readOnly: false, // 是否只读
                    automaticLayout: true, // 自动布局
                    theme: 'vs-light', // vs | hc-black | vs-dark
                    minimap: {
                        enabled: false,// 关闭小地图
                    },
                    tabSize: 2, // tab缩进长度
                    fontSize: 14, // 文字大小,
                    scrollBeyondLastLine: false,
                    wordWrap: 'off',
                    wordWrapColumn: 80, // 自动换行
                    formatOnType: true,
                    formatOnPaste: true,
                });
                const config = {
                    openaiKey: Cookies.get("Authorization").slice(7),
                    openaiUrl: process.env.VUE_APP_API_BASE_URL + '/ai/openai',
                    assistantMessage: 'use sql mysql',
                    openaiParams: {
                        model: '',
                    },
                };

                const dispose = MonacoEditorCopilot(this.monacoEditor, config);

                this.setSQL()
            }
        },
        setSQL() {
            let language = 'sql'
            if (this.formData.type === 'SQL') {
                language = 'sql'
                //this.sql = format(this.sql,'mysql')
            } else if (this.formData.type === 'JavaScript') {
                language = 'javascript'
            } else if (this.formData.type === 'JSON') {
                language = 'json'
            }
            monaco.editor.getModels().forEach(model => model.dispose());
            const model = monaco.editor.createModel(
                this.sql,
                language
            )

            this.monacoEditor.setModel(model)
            this.monacoEditor.onDidChangeModelContent(() => {
                this.sql = this.monacoEditor.getValue()
            })
        },
        getSQL() {
            return this.sql
        },
        onTypeChange() {
            this.sql = ''
            this.setSQL()
        },
        onTabChange(activeKey) {
            this.currentTab = activeKey
        },
        formatType(type) {
            if (type === 'TEXT') {
                return '文本'
            } else if (type === 'NUM') {
                return '数字'
            } else if (type === 'DATE') {
                return '日期'
            } else if (type === 'ENUM') {
                return '枚举'
            }
        },
        closeDialog() {
            this.$emit("close");
        },
        loadData() {
            this.formData.name = this.dataset.name
            this.formData.type = this.dataset.type || 'SQL'
            this.sql = this.dataset.sql || ''
            this.setSQL()
            this.tableColumn = this.dataset.fields
            this.tableColumn.forEach((item, index) => {
                item.key = index
                item.field = item.name
                item.title = item.name
            })
            if (this.sql.length > 0) {
                this.executeSQL()
            }
        },
        async insertEvent(row) {
            const $table = this.$refs.paramsTable
            const record = {
                name: '',
                type: 'TEXT',
                value: ''
            }
            const { row: newRow } = await $table.insertAt(record, row)
            await $table.setActiveCell(newRow, 'name')
        },
        example() {
            if (this.formData.type === 'SQL') {
                this.sql = 'SELECT req.* FROM [[TRACKER]系统需求] req'
            } else if (this.formData.type === 'JavaScript') {
                this.sql = ""
            } else if (this.formData.type === 'JSON') {
                this.sql = '[\n{ "field1": "text", "field2": 2, "field3": "2023-12-01" }\n]'
            }

            this.setSQL()
        },
        executeSQL() {
            let dataset = {
                name: this.formData.name,
                type: this.formData.type,
                sql: this.getSQL(),
            }
            let request = {
                dataset: dataset,
                dataRequest: { }
            }
            this.tableData = []
            getSQLPreview(this.pageId, request).then(resp => {
                this.tableColumn = resp.fields
                this.tableColumn.forEach((item, index) => {
                    item.key = index
                    item.field = item.name
                    item.title = item.name
                })
                this.tableData = resp.data
            }).catch(error => {
                this.$message.error(error)
                this.tableData = []
            })
        },
        onOK() {
            this.$refs["datasetForm"].validate((valid) => {
                if (valid) {
                    let result = cloneDeep(this.dataset)
                    result.name = this.formData.name
                    result.sql = this.sql
                    result.type = this.formData.type
                    this.$emit("ok", result);
                }
            })
        },
        onCancel() {
            this.$emit("cancel");
        },
        getMaxLength(item) {
            return this.getTextWidth(item.title);
        },
        // 获取文本宽度
        getTextWidth(str) {
            let width = 0;
            let span = document.createElement("span");
            span.innerText = str;
            document.querySelector("body").appendChild(span);
            width = span.getBoundingClientRect().width;
            span.remove();
            return width;
        },
    },
    created() { }
};
</script>
<style lang="less" scoped>
.code-container {
    background: #f5f6f7;
    box-sizing: border-box;
    min-height: 248px;
    color: var(--deTextPrimary, #1f2329);
    border: solid 1px #ccc;

    .code-editor {
        width: 100%; //570px;
        height: 248px;
    }
}

.box {
    width: 20px;
    height: 20px;
    border: solid 1px black;
}

.table-view {
    .first-col {
        position: relative;
        height: 20px;
    }

    .first-col:before {
        content: "";
        position: absolute;
        left: -14px;
        top: 10px;
        width: 204px;
        height: 1px;
        transform: rotate(13deg);
        background-color: #e8eaec;
    }

    .first-col .first-col-top {
        position: absolute;
        right: 4px;
        top: -10px;
    }

    .first-col .first-col-bottom {
        position: absolute;
        left: 4px;
        bottom: -10px;
    }
}
</style>
