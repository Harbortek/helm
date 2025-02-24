<template>
    <div class="baseline-compare">
        <vxe-toolbar ref="xToolbar" size="medium">
            <template #buttons>

            </template>
            <template #tools>
                <a-space>
                    左侧基线:
                    <project-baseline-select ref="leftBaselineSelect" v-model="leftBaselineId" @change="onChangeBaseline" 
                        :projectId="projectId" :type="baselineType" style="width:200px;" />
                    右侧基线:
                    <project-baseline-select ref="rightBaselineSelect" v-model="rigtBaselineId" @change="onChangeBaseline"
                        :projectId="projectId" :type="baselineType" style="width:200px;" />
                    <a-button icon="diff" @click="onCompare">比较</a-button>
                    <a-button icon="file-excel" @click="onExport">导出</a-button>
                </a-space>
            </template>
        </vxe-toolbar>
        <vxe-table size="mini" :loading="loading" ref="compareTable" show-overflow border row-key :row-config="{ isHover: true }"
            :show-header="true" auto-resize :data="tableData" :checkbox-config="{ labelField: 'name' }"
            :cell-style="cellStyle"
            :tree-config="{ transform: true, accordion: false, line: true, rowField: 'id', parentField: 'parentId', iconOpen: 'vxe-icon-square-minus', iconClose: 'vxe-icon-square-plus' }">
            <vxe-column field="name" tree-node>
                <template #default="{ row }">
                    <div style="display: inline-flex;">
                        <div style="width:20px;"> <a-icon :component="row.icon" v-if="row.icon" /></div> {{ row.name }}
                    </div>
                </template>
            </vxe-column>
            <vxe-colgroup title="左侧" header-align="center">
                <vxe-column field="leftName" title="名称" header-align="center">
                    <template #default="{ row }">
                        {{ row.left?.name }}
                    </template>
                </vxe-column>
                <vxe-column field="leftRevision" title="版本" header-align="center">
                    <template #default="{ row }">
                        {{ row.left?.revision }}
                    </template>
                </vxe-column>
            </vxe-colgroup>
            <vxe-colgroup title="右侧" header-align="center">
                <vxe-column field="rightName" title="名称" header-align="center">
                    <template #default="{ row }">
                        {{ row.right?.name }}
                    </template>
                </vxe-column>
                <vxe-column field="rightRevision" title="版本" header-align="center">
                    <template #default="{ row }">
                        {{ row.right?.revision }}
                    </template>
                </vxe-column>
            </vxe-colgroup>
            <vxe-column title="操作" header-align="center">
                <template #default="{ row }">
                    <a-tooltip title="查看差异">
                        <vxe-button v-if="!row.folder" type="text" icon="vxe-icon-eye-fill"
                            @click="onViewDiff(row)"></vxe-button>
                    </a-tooltip>
                </template>
            </vxe-column>
        </vxe-table>
        <tracker-item-diff-dialog :isShowDialog="showTrackerItemDiff" :currentCompare="currentCompare" @cancel="showTrackerItemDiff = false" />

        <document-diff-dialog :isShowDialog="showDocumentDiff" :currentCompare="currentCompare" @cancel="showDocumentDiff = false" />
    </div>
</template>

<script>
import ProjectBaselineSelect from '@/components/select/ProjectBaselineSelect.vue';
import { compareBaseline } from '@/services/baseline/BaselineService'
import TrackerItemDiffDialog from './TrackerItemDiffDialog.vue';
import DocumentDiffDialog from './DocumentDiffDialog.vue';
export default {
    name: 'BaselineCompare',
    components: { ProjectBaselineSelect, TrackerItemDiffDialog,DocumentDiffDialog},
    props: {
        projectId: {
            required: true,
        }
    },
    data() {
        return {
            loading: false,
            leftBaselineId: undefined,
            rigtBaselineId: undefined,
            tableData: [],
            showTrackerItemDiff: false,
            showDocumentDiff: false,
            currentCompare: undefined,
            baselineType: '',
        }
    },
    mounted() {

    },
    methods: {
        loadData(){
            this.$refs.leftBaselineSelect.loadData();
            this.$refs.rightBaselineSelect.loadData();
        },
        onCompare() {
            if (this.leftBaselineId && this.rigtBaselineId) {
                this.loading=true;
                compareBaseline(this.projectId, this.leftBaselineId, this.rigtBaselineId).then(resp => {
                    this.tableData = resp
                    this.loading= false;
                    this.$nextTick(() => {
                        this.$refs.compareTable.setAllTreeExpand(true)
                    })
                })
            }
        },
        onExport() {

        },
        cellStyle({ row, column }) {
            if (column.field === 'leftName' || column.field === 'leftRevision') {
                if (row.mode == 'ADD') {
                    return {
                        color: '#189FFF',
                        backgroundImage: '-webkit-gradient(linear, left top, left bottom, from(#e8eaec), to(#e8eaec)), -webkit-gradient(linear, left top, left bottom, from(#e8eaec), to(#e8eaec))',
                        backgroundImage: 'linear-gradient(#e8eaec, #e8eaec), linear-gradient(#e8eaec, #e8eaec)',
                        backgroundRepeat: 'no-repeat',
                        backgroundSize: '1px 100%, 100% 1px',
                        backgroundPosition: '100% 0, 100% 100%',
                    }
                } else if (row.mode === 'UPDATE') {
                    if (parseInt(row.left.revision) > parseInt(row.right.revision)) {
                        return {
                            color: '#f5222f',
                            backgroundImage: '-webkit-gradient(linear, left top, left bottom, from(#e8eaec), to(#e8eaec)), -webkit-gradient(linear, left top, left bottom, from(#e8eaec), to(#e8eaec))',
                            backgroundImage: 'linear-gradient(#e8eaec, #e8eaec), linear-gradient(#e8eaec, #e8eaec)',
                            backgroundRepeat: 'no-repeat',
                            backgroundSize: '1px 100%, 100% 1px',
                            backgroundPosition: '100% 0, 100% 100%',
                        }
                    } else {
                        return {
                            color: '#908b8b',
                            backgroundImage: '-webkit-gradient(linear, left top, left bottom, from(#e8eaec), to(#e8eaec)), -webkit-gradient(linear, left top, left bottom, from(#e8eaec), to(#e8eaec))',
                            backgroundImage: 'linear-gradient(#e8eaec, #e8eaec), linear-gradient(#e8eaec, #e8eaec)',
                            backgroundRepeat: 'no-repeat',
                            backgroundSize: '1px 100%, 100% 1px',
                            backgroundPosition: '100% 0, 100% 100%',
                        }
                    }

                }
            } else if (column.field === 'rightName' || column.field === 'rightRevision') {
                if (row.mode === 'DELETE') {
                    return {
                        color: '#189FFF',
                        backgroundImage: '-webkit-gradient(linear, left top, left bottom, from(#e8eaec), to(#e8eaec)), -webkit-gradient(linear, left top, left bottom, from(#e8eaec), to(#e8eaec))',
                        backgroundImage: 'linear-gradient(#e8eaec, #e8eaec), linear-gradient(#e8eaec, #e8eaec)',
                        backgroundRepeat: 'no-repeat',
                        backgroundSize: '1px 100%, 100% 1px',
                        backgroundPosition: '100% 0, 100% 100%',
                    }
                } else if (row.mode === 'UPDATE') {
                    if (parseInt(row.left.revision) > parseInt(row.right.revision)) {
                        return {
                            color: '#908b8b',
                            backgroundImage: '-webkit-gradient(linear, left top, left bottom, from(#e8eaec), to(#e8eaec)), -webkit-gradient(linear, left top, left bottom, from(#e8eaec), to(#e8eaec))',
                            backgroundImage: 'linear-gradient(#e8eaec, #e8eaec), linear-gradient(#e8eaec, #e8eaec)',
                            backgroundRepeat: 'no-repeat',
                            backgroundSize: '1px 100%, 100% 1px',
                            backgroundPosition: '100% 0, 100% 100%',
                        }
                    } else {
                        return {
                            color: '#f5222f',
                            backgroundImage: '-webkit-gradient(linear, left top, left bottom, from(#e8eaec), to(#e8eaec)), -webkit-gradient(linear, left top, left bottom, from(#e8eaec), to(#e8eaec))',
                            backgroundImage: 'linear-gradient(#e8eaec, #e8eaec), linear-gradient(#e8eaec, #e8eaec)',
                            backgroundRepeat: 'no-repeat',
                            backgroundSize: '1px 100%, 100% 1px',
                            backgroundPosition: '100% 0, 100% 100%',
                        }
                    }
                }
            }
            return {
                backgroundImage: '-webkit-gradient(linear, left top, left bottom, from(#e8eaec), to(#e8eaec)), -webkit-gradient(linear, left top, left bottom, from(#e8eaec), to(#e8eaec))',
                backgroundImage: 'linear-gradient(#e8eaec, #e8eaec), linear-gradient(#e8eaec, #e8eaec)',
                backgroundRepeat: 'no-repeat',
                backgroundSize: '1px 100%, 100% 1px',
                backgroundPosition: '100% 0, 100% 100%',
            }
        },
        onViewDiff(row) {
            console.log("onViewDiff",row)
            this.currentCompare = row
            if (row.type === 'wiki') {
                this.showDocumentDiff= true;
            } else if (row.type === 'tracker') {
                this.showTrackerItemDiff = true
            }
        },
        onChangeBaseline(v,row){
            if(!row[0]){
                this.leftBaselineId=undefined;
                this.rigtBaselineId=undefined;
            }
            this.baselineType=row[0]?.type
        },
    },
}
</script>

<style lang="less" scoped>
.baseline-compare {
    margin: 10px;
    height: calc(100% - 350px);
    overflow: auto;
}
</style>
