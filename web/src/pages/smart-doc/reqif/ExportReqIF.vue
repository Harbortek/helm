<template>
    <config-page description="将工作项的属性映射到REQIF文件中。">
        <div style="overflow-y:auto;background-color: white;">
            <vxe-toolbar style="margin-right: 5px;flex:0 0 32px;">
                <template #buttons>
                    <a-space>
                        <a-button @click="onCancel">关闭</a-button>
                        <a-button type="primary" @click="onExport" icon="export">导出</a-button>
                    </a-space>
                </template>
                <template #tools>
                    <a-space>
                        <a-button @click="onLoadTemplate" icon="load">加载</a-button>
                        <a-button @click="onSaveTemplate" icon="save">保存</a-button>
                    </a-space>
                </template>
            </vxe-toolbar>
            <div class="rules-conatiner" ref="rulesContainer">

                <a-card v-for="(rule, index) in job.mappings" :key="index" style="margin-top: 5px;">
                    <template #title>
                        <a-row type="flex" justify="space-between" align="middle" :gutter="16"
                            style="font-size: 14px;font-weight: bold;">
                            <a-col flex="200px">ReqIF {{ rule.specTypeName }}</a-col>
                            <a-col flex="auto">
                                来自于 工作项 {{ rule.specTypeName }}
                            </a-col>
                            <a-col flex="50px">

                            </a-col>
                        </a-row>
                    </template>

                    <SpecTypeRule :data="rule" :projectId="projectId" @change="e => onRuleChange(e, index)">
                    </SpecTypeRule>
                </a-card>
            </div>

            <save-template-dialog :showDialog="showSaveDialog" :config="templateToSave" :projectId="projectId"
                @ok="onSaveOK" @cancel="
                            showSaveDialog = false" />
            <load-template-dialog :showDialog="showLoadDialog" :projectId="projectId" @ok="onLoadTemplateOK" @cancel="
                            showLoadDialog = false" />


        </div>
    </config-page>
</template>

<script>
import SaveTemplateDialog from './SaveTemplateDialog.vue'
import {cloneDeep} from 'lodash'
import LoadTemplateDialog from './LoadTemplateDialog.vue';
import ConfigPage from "@/components/config-page/ConfigPage.vue";
import SpecTypeRule from './SpecTypeRule.vue';

import { loadExportJob, saveExportJob, exportReqIF } from '@/services/smart-doc/ReqIFExportService.js'
import { de } from 'date-fns/locale';
export default {
    name: 'ExportReqIF',
    components: { SpecTypeRule, SaveTemplateDialog, LoadTemplateDialog, ConfigPage },
    data() {
        return {
            showSaveDialog: false,
            showLoadDialog: false,
            rulesHeight: 600,
            job: {
                mappings: [],
            },
            templateToSave: {},
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
        setTimeout(() => {
            this.$nextTick(() => {
                if (this.$refs.rulesContainer) {
                    this.rulesHeight =
                        window.innerHeight - this.$refs.rulesContainer.offsetTop - 100;
                }

                // 监听窗口大小变化
                let self = this;
                window.onresize = function () {
                    if (self.$refs.rulesContainer) {
                        self.rulesHeight =
                            window.innerHeight - self.$refs.rulesContainer.offsetTop - 100;
                    }
                };
            })
        }, 100)
        this.loadData()
    },
    methods: {
        loadData() {
            loadExportJob(this.pageId).then(resp => {
                this.job = resp
            })
        },
        onLoadTemplate() {
            this.showLoadDialog = true
        },
        onSaveTemplate() {
            this.templateToSave = cloneDeep(this.job)
            delete this.templateToSave.id
            delete this.templateToSave.pageId
            delete this.templateToSave.projectId
            this.showSaveDialog = true
        },
        onRuleChange(e, index) {
            this.job.mappings[index] = e
        },
        onSaveOK(template) {
            this.showSaveDialog = false
        },
        onLoadTemplateOK(template) {
            console.log(template)
            let newRuleMap = template.mappings.reduce((map, element) => (map[element.specTypeName] = element, map), {});

            for (let i = 0; i < this.job.mappings.length; i++) {
                let rule = this.job.mappings[i]
                if (newRuleMap[rule.specTypeName]) {
                    this.$set(this.job.mappings, i, newRuleMap[rule.specTypeName])

                }
            }

            console.log(this.job)

            this.showLoadDialog = false
        },
        onCancel() {
            this.$router.push({
                name: 'projectWiki',
                params: {
                    projectId: this.projectId,
                    pageId: this.pageId,
                }
            })
        },
        onExport() {
            saveExportJob(this.pageId, this.job).then(resp => {
                exportReqIF(this.pageId)
            })
        }
    },
}
</script>

<style lang="less" scoped></style>