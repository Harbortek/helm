<template>
    <a-modal v-model="visiable" title="取消关联已有流水线" @ok="onOK" @cancel="onCancel" centered :width="600"
        dialogClass="tool-dialog">

        <vxe-table size="small" ref="xTable" show-overflow border row-key :show-header="true" height="400"
            :data="bindingPipelines" :checkbox-config="{}" :row-config="{ isCurrent: true }">
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
    name: 'UnbindingPipelineDialog',
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