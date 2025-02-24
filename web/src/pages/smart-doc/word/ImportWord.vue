<template>
    <div style="width: 100;height: 100%;">
        <a-row type="flex" justify="space-around" align="top" class="import-rule-container">
            <a-col ref="word-view" class="word-view" :span="14">
                <div style="display: flex; flex-direction: column;height: 100%;">
                    <div class="word-container">
                        <!-- <HIframe :src="htmlLocation" /> -->
                        <smart-doc ref="liveDoc" :pageId="pageId" :projectId="projectId" displayMode="preview"
                            :previewDoc="doc" :autoNumber="autoNumber" />
                    </div>
                </div>
            </a-col>
            <a-col class="rule-view" :span="10">
                <import-word-rules :data="rules" :revision="content.revision" :docProps="content.docProps"
                    :projectId="projectId" :pageId="pageId" @change="onRulesChange" />
            </a-col>
        </a-row>
        <a-row type="flex" justify="space-around" align="top" class="import-footer">
            <a-col class="word-footer" :span="14">
                <a-space>
                    <a-checkbox :checked="autoNumber" @change="onAutoNumberChange">章节自动编号</a-checkbox>
                </a-space>
            </a-col>
            <a-col class="rules-footer" :span="10">
                <a-space>
                    <a-button @click="onCancel">取消</a-button>
                    <a-button @click="onWithdraw" v-if="content.revision > 1">撤销上一次转换</a-button>
                    <a-button @click="onPreview" :disabled="rules.length == 0">转换</a-button>
                    <a-button type="primary" @click="onImport" icon="check">导入</a-button>
                </a-space>
            </a-col>
        </a-row>
        <LoadingDialog :isShowDialog="loading" />
    </div>
</template>

<script>
import HIframe from '@/components/iframe';
import ImportWordRules from './ImportWordRules.vue';
import { findWordImportJob, updateWordImportJob, deleteWordImportJob, withdrawWordImportJob, completeWordImportJob } from '@/services/smart-doc/WordImportService';
import SmartDoc from '@/components/smart-doc/SmartDoc.vue'
import cloneDeep from 'lodash/cloneDeep';
import { isConditionHasArgument, isActionHasArgument } from '@/pages/smart-doc/util/rules'
import LoadingDialog from '@/components/loading/LoadingDialog.vue';
export default {
    name: 'ImportWord',
    components: { HIframe, ImportWordRules, SmartDoc, LoadingDialog },
    data() {
        return {
            previewContent: '',
            loading: false,
            autoNumber: false,
            rules: [],
            content: {
                pageId: this.pageId,
                projectId: this.projectId,
                wordFilePath: this.filePath,
                rules: [],
                docProps: { headings: [], styles: [] },
            },
            doc: {
                blocks: [],
            }
        }
    },
    computed: {
        projectId() {
            return this.$route.params.projectId;
        },
        pageId() {
            return this.$route.params.wikiId;
        },
    },
    mounted() {
        this.loadData();
    },
    methods: {
        loadData() {
            findWordImportJob(this.projectId, this.pageId).then(resp => {
                this.content = resp
                this.doc = { blocks: JSON.parse(this.content.blocksJSON) || [] }
                this.autoNumber = this.content.autoNumber
                console.log(this.doc.blocks)
                this.rules = cloneDeep(this.rules)
            })
        },
        onAutoNumberChange() {
            this.autoNumber = !this.autoNumber
            this.onPreview({ rules: this.content.rules })
        },
        onRulesChange(config) {
            this.rules = config.rules
        },
        onPreview() {
            if (!this.checkRulues()) {
                return
            }
            let content = cloneDeep(this.content)
            content.rules = this.rules
            content.blocksJSON = JSON.stringify(this.$refs.liveDoc.doc.editorBlocks)
            content.autoNumber = this.autoNumber
            this.loading = true
            updateWordImportJob(this.pageId, content).then(resp => {
                this.content = resp
                this.doc = { blocks: JSON.parse(this.content.blocksJSON) || [] }
                this.autoNumber = this.content.autoNumber
                this.rules = cloneDeep(this.rules)
                this.loading = false
            }).catch(() => {
                this.loading = false
            })
        },
        onCancel() {
            deleteWordImportJob(this.pageId, this.content.id).then(resp => {
                this.$router.push({
                    name: 'projectWiki',
                    params: {
                        projectId: this.projectId,
                        pageId: this.pageId,
                    }
                })
            })
        },
        onWithdraw() {
            this.loading = true
            withdrawWordImportJob(this.pageId, this.content.id).then(resp => {
                this.content = resp
                this.doc = { blocks: JSON.parse(this.content.blocksJSON) || [] }
                this.autoNumber = this.content.autoNumber
                this.rules = cloneDeep(this.rules)
                this.loading = false
            }).catch(() => {
                this.loading = false
            })
        },
        onImport() {
            let content = cloneDeep(this.content)
            content.rules = this.rules
            content.blocksJSON = JSON.stringify(this.$refs.liveDoc.doc.editorBlocks)
            content.autoNumber = this.autoNumber
            this.loading = true
            completeWordImportJob(this.pageId, content).then(resp => {
                this.loading = false
                this.$router.push({
                    name: 'projectWiki',
                    params: {
                        projectId: this.projectId,
                        pageId: this.pageId,
                    }
                })
            }).catch(() => {
                this.loading = false
            })
        },
        checkRulues() {
            if (this.rules.length == 0) {
                //this.$message.error('请配置转换规则')
                return true
            }
            for (let i = 0; i < this.rules.length; i++) {
                const rule = this.rules[i]
                if (rule.type == 'PARAGRAPH') {
                    if (rule.scope !== 'ALL' && (!Array.isArray(rule.source) || rule.source.length == 0)) {
                        this.$message.error('请设置转换范围')
                        return false
                    }
                    if (!rule.target) {
                        this.$message.error('请设置工作项类型')
                        return false
                    }
                    if (!this.checkCondtions(rule.conditions)) {
                        return false
                    }
                    if (!this.checkCondtions(rule.nextParagraph.conditions)) {
                        return false
                    }
                    if (!this.checActions(rule.actions)) {
                        return false
                    }
                } else if (rule.type == 'TABLE') {
                    if (rule.scope !== 'ALL' && (!Array.isArray(rule.source) || rule.source.length == 0)) {
                        this.$message.error('请设置转换范围')
                        return false
                    }
                    if (!this.checkCondtions(rule.conditions)) {
                        return false
                    }
                    if (!this.checkConversions(rule.conversions)) {
                        return false
                    }
                }
            }

            return true
        },
        checkCondtions(conditions) {
            if (!Array.isArray(conditions) || conditions.length == 0) {
                return true
            }

            for (let i = 0; i < conditions.length; i++) {
                const condition = conditions[i]
                if (isConditionHasArgument(condition) && condition.argument === '') {
                    this.$message.error('请配置条件参数')
                    return false
                }
            }
            return true
        },
        checActions(actions) {
            if (!Array.isArray(actions) || actions.length == 0) {
                return true
            }
            for (let i = 0; i < actions.length; i++) {
                const action = actions[i]
                if (!action.fieldId) {
                    this.$message.error('请设置动作涉及的工作项属性')
                    return false
                }
                if (action.argumentType == 'TABLE' || action.argumentType == 'TEST_STEP') {
                    if (!action.columnMap) {
                        this.$message.error('请设置表格映射字段')
                        return false
                    }
                } else {
                    if (isActionHasArgument(action) && action.argument === '') {
                        this.$message.error('请设置动作参数')
                        return false
                    }
                }

                if (!this.checkCondtions(action.conditions)) {
                    return false
                }
            }
            return true
        },
        checkConversions(conversions) {
            if (!Array.isArray(conversions) || conversions.length == 0) {
                return true
            }
            for (let i = 0; i < conversions.length; i++) {
                const conversion = conversions[i]
                if (!conversion.target) {
                    this.$message.error('请设置工作项类型')
                    return false
                }
                if (!this.checkCondtions(conversion.conditions)) {
                    return false
                }
                if (!this.checActions(conversion.actions)) {
                    return false
                }
            }
            return true
        },
    }
}
</script>

<style lang="less" scoped>
.import-rule-container {
    width: 100%;
    height: calc(100% - 50px);
    overflow: hidden;
    display: flex;

    .word-view {
        height: 100%;
        overflow-y: hidden;
        background-color: #f2f3f5;

        .word-container {
            flex: auto;
            height: 100%;
        }
    }

    .rule-view {
        height: 100%;
        overflow-y: auto;
        background-color: #fff;
    }
}

.import-footer {
    height: 50px;
    padding: 5px;
    background: white;
    border-top: solid 1px #e8e8e8;

    .word-footer {
        background-color: #fff;
        padding-top: 5px;
        text-align: left;
        background: transparent;
    }

    .rules-footer {
        padding-top: 5px;
        padding-right: 5px;
        text-align: right;
        background: transparent;
    }
}
</style>