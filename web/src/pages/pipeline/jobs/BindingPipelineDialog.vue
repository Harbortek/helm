<template>
    <a-modal v-model="visiable" title="关联已有流水线" @ok="onOK" @cancel="onCancel" centered :width="600">
        <div>
            <span>流水线服务器</span>
        </div>
        <a-select v-model="repositoryId" style="width: 100%;" @change="onSelectRepository">
            <a-select-option v-for=" item in repositories" :key="item.id">{{ item.serverUrl }}</a-select-option>
        </a-select>
        <div style="width: 100%;height: 10px;">
        </div>

        <vxe-table size="small" ref="xTable" show-overflow border row-key :show-header="true" height="400"
            :data="tableData" :checkbox-config="{}" :row-config="{ isCurrent: true }">
            <vxe-column type="checkbox" width="50px" header-align="center" align="center">
            </vxe-column>
            <vxe-column field="name" title="名称" header-align="center" align="center">
            </vxe-column>
        </vxe-table>
    </a-modal>
</template>

<script>
import { findPipelineRepositories, findPipelines } from '@/services/pipeline/PipelineService'
export default {
    name: 'BindingPipelineDialog',
    props: {
        isShowDialog: {
            required: true
        },
        bindingPipelines: {
            required: true,
            default: () => { return [] }
        }
    },
    data() {
        return {
            repositoryId: '',
            repositories: [],
            allPiepelines: []
        }
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
        tableData() {
            return this.allPiepelines.filter(item => {
                return this.notExists(item)
            })
        }
    },
    watch: {
        isShowDialog: {
            handler: function (newVal, oldVal) {
                if (newVal) {
                    this.loadData();
                }
            }
        }
    },
    methods: {
        loadData() {

            findPipelineRepositories().then(resp => {
                this.repositories = resp
            })

        },
        notExists(item) {
            return !this.bindingPipelines.some(p => { return p.repositoryId === item.repositoryId && p.name === item.name })
        },
        onSelectRepository(newVal) {
            findPipelines(newVal).then(resp => {
                this.allPiepelines = resp || []
                const repositoryId = this.repositoryId
                this.allPiepelines.forEach(item => { item.repositoryId = repositoryId })
            })
        },
        onOK() {
            const records = this.$refs.xTable.getCheckboxRecords()
            if (records.length > 0) {
                this.$emit('ok', records)
            }
        },
        onCancel() {
            this.$emit('cancel')
        }

    },
}
</script>

<style lang="less"></style>