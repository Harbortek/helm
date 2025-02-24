<template>
    <div style="width: 100;height: 100%;">
        <a-row type="flex" justify="space-around" align="top" class="import-rule-container">
            <a-col ref="word-view" class="word-view" :span="14">
                <div style="display: flex; flex-direction: column;height: 100%;">
                    <div class="word-container">
                        <smart-doc ref="liveDoc" :pageId="pageId" :projectId="projectId" displayMode="preview"
                            :previewDoc="doc" :autoNumber="true" />
                    </div>
                </div>
            </a-col>
            <a-col class="rule-view" :span="10">
                <ImportReqIFRules :data="rules" :revision="content.revision" :projectId="projectId" :pageId="pageId"
                    @change="onRulesChange" />
            </a-col>
        </a-row>
        <a-row type="flex" justify="space-around" align="top" class="import-footer">
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
import ImportReqIFRules from './ImportReqIFRules.vue';
import { findReqIFImportJob, updateReqIFImportJob, deleteReqIFImportJob, withdrawReqIFImportJob, completeReqIFImportJob } from '@/services/smart-doc/ReqIFImportService';
import SmartDoc from '@/components/smart-doc/SmartDoc.vue'
import cloneDeep from 'lodash/cloneDeep';
import LoadingDialog from '@/components/loading/LoadingDialog.vue';
export default {
    name: 'ImportReqIF',
    components: { ImportReqIFRules, SmartDoc, LoadingDialog },
    data() {
        return {
            previewContent: '',
            loading: false,
            autoNumber: true,
            showPreviewDialog: false,
            rules: [],
            content: {
                pageId: this.pageId,
                projectId: this.projectId,
                rules: [],
            },
            doc: {
                blocks: [],
            },
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
            findReqIFImportJob(this.projectId, this.pageId).then(resp => {
                this.content = resp
                this.doc = { blocks: JSON.parse(this.content.blocksJSON) || [] }
                if (this.rules.length === 0) {
                    this.rules = cloneDeep(this.content.rules)
                } else {
                    this.rules = cloneDeep(this.rules)
                }
                console.log(this.doc.blocks)
            })
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
            this.loading = true
            updateReqIFImportJob(this.pageId, content).then(resp => {
                this.content = resp
                this.doc = { blocks: JSON.parse(this.content.blocksJSON) || [] }
                this.autoNumber = this.content.autoNumber
                this.rules = cloneDeep(this.rules)
                this.loading = false
                console.log(this.doc.blocks)
            }).catch(() => {
                this.loading = false
            })
        },
        onCancel() {
            deleteReqIFImportJob(this.pageId, this.content.id).then(resp => {
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
            withdrawReqIFImportJob(this.pageId, this.content.id).then(resp => {
                this.content = resp
                this.doc = { blocks: JSON.parse(this.content.blocksJSON) || [] }
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
            this.loading = true
            completeReqIFImportJob(this.pageId, content).then(resp => {
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
                this.$message.error('请配置转换规则')
                return false
            }
            for (let i = 0; i < this.rules.length; i++) {
                const rule = this.rules[i]

                if (!rule.target) {
                    this.$message.error('请设置工作项类型')
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