<template>
    <div class="parameter-panel">

        <!-- <div class="title-bar">
            <span class="title-bar-title">显示的页面参数</span>
        </div> -->
        <div>
            <div class="prop-name">
                <div class="data-area-label">
                    <span>查询字段</span>
                </div>
                <a-icon type="delete" class="data-area-clear" @click="clearData()" />
            </div>
            <div class="prop-value">
                <draggable v-model="formData.fields" v-bind="{ group: { name: 'drag', pull: 'clone' }, sort: true }"
                    animation="300" :move="onMove" class="drag-block-style" @add="addQueryField">
                    <transition-group class="draggable-group">
                        <query-item v-for="(item, index) in formData.fields" :key="item.name" :index="index"
                            :item="item" :pageQueryComponent="component" :datasets="datasets" :charts="charts"
                            @onQueryItemChange="queryItemChange" @onQueryItemRemove="queryItemRemove"
                            @editQueryItem="editQueryItem" @onNameEdit="showRename" />
                    </transition-group>
                </draggable>
                <div v-show="!formData.fields || formData.fields.length === 0" class="drag-placeholder-style">
                    <span class="drag-placeholder-style-span">{{ $t('chart.placeholder_field') }}</span>
                </div>
            </div>
        </div>
        <div class="field-list-container">
            <div>
                <div class="prop-name"><span class="data-area-label">数据集</span></div>
                <div class="prop-value">
                    <a-select v-model="formData.datasetId" @change="onDatasetChange" style="width: 330px" size="small">
                        <a-select-option v-for="item in datasets" :key="item.id" :value="item.id">{{ item.name
                            }}</a-select-option>
                    </a-select>
                </div>
            </div>

            <div class="prop-name">
                <span class="data-area-label">数据集字段
                </span>
            </div>

            <div class="field-list">
                <draggable v-model="fields" v-bind="{ group: { name: 'drag', pull: 'clone' }, sort: false }"
                    animation="300" :move="onMove" class="drag-list">
                    <transition-group>
                        <div class="field-item" :key="f.name" v-for="f in fields">
                            <a-icon component="field_type_text" style="color: #222222;" v-show="f.type === 'TEXT'" />
                            <a-icon component="field_type_number" style="color: cadetblue;" v-show="f.type === 'NUM'" />
                            <a-icon component="date" style="color:cadetblue;" v-show="f.type === 'DATE'" />
                            {{ f.title }}
                        </div>
                    </transition-group>
                </draggable>
            </div>
        </div>
        <page-query-config-dialog :isShowDialog="showQueryConfigDialog" :queryItems="formData.fields" :charts="charts" :datasets="datasets" :pageId="pageId"
            :selectedQuery="selectedQuery" @ok="onQueryConfigOK" @cancel="onQueryConfigCancel" />
    </div>
</template>

<script>
import draggable from 'vuedraggable'
import { deepCopy } from '@/pages/smart-page/util/canvasUtils'
import { findDatasetsByPageId, getSQLPreview } from '@/services/smart-page/DatasetService.js'
import QueryItem from '@/pages/smart-page/propertyeditor/dragItem/QueryItem.vue'
import PageQueryConfigDialog from '@/pages/smart-page/propertyeditor/query/PageQueryConfigDialog.vue'

export default {
    name: 'PageQueryEditor',
    props: {
        pageId: {
            type: String,
        },
        component: {
            type: Object,
            required: false
        },
        charts: {
            type: Array,
            required: false
        }
    },
    components: {
        draggable, QueryItem, PageQueryConfigDialog
    },
    data() {
        return {
            datasets: [],
            fields: [],
            showDialog: false,
            currentParameter: {},
            parameterList: [],
            loading: false,
            formData: {
                datasetId: '',
                fields: []
            },
            showQueryConfigDialog: false,
            selectedQuery: null,
        }
    },
    watch: {
        component: function (newVal, oldVale) {
            this.prepareFormData(newVal)
        }
    },
    mounted() {
        this.loadData()
    },
    methods: {
        loadData() {
            findDatasetsByPageId(this.pageId).then(resp => {
                this.datasets = resp || []
                this.prepareFormData(this.component)
            })
        },
        clearData() {
            this.formData.fields = []
        },
        prepareFormData(newVal) {
            this.formData = deepCopy(newVal)
            this.formData.datasetId = newVal.datasetId || ''
            this.formData.fields = newVal.fields ? JSON.parse(newVal.fields) : []
            console.log('formdata', this.formData)
            this.fields = this.getFieldsFromDataset()
        },
        onDatasetChange() {
            this.fields = this.getFieldsFromDataset()
            this.formData.fields = []
            this.emitDataChange()
        },
        getFieldsFromDataset() {
            if (this.formData.datasetId && this.datasets && this.datasets.length > 0) {
                const datasetId = this.formData.datasetId
                let filterd = this.datasets.filter(item => { return item.id === datasetId })
                let fields = filterd.length > 0 ? filterd[0].fields : []
                for (let i = 0; i < fields.length; i++) {
                    fields[i].datasetId = datasetId
                }
                return fields
            }
            return []
        },
        emitDataChange() {
            let comp = deepCopy(this.formData)
            comp.fields = JSON.stringify(this.formData.fields)
            this.$emit('change', comp)
        },
        onMove(e, originalEvent) {
            this.moveId = e.draggedContext.element.name
            return true
        },

        dragMoveDuplicate(list, e, mode) {
            if (mode === 'ds') {
                list.splice(e.newDraggableIndex, 1)
            } else {
                const that = this
                const dup = list.filter(function (m) {
                    return m.name === that.moveId
                })
                if (dup && dup.length > 1) {
                    list.splice(e.newDraggableIndex, 1)
                }
            }
        },
        dragRemoveChartField(list, e) {
            const that = this
            const dup = list.filter(function (m) {
                return m.id === that.moveId
            })
            if (dup && dup.length > 0) {
                if (dup[0].chartId) {
                    list.splice(e.newDraggableIndex, 1)
                }
            }
        },
        addQueryField(e) {
            this.dragMoveDuplicate(this.formData.fields, e)
            this.formData = { ...this.formData }
            this.emitDataChange()
        },
        queryItemChange(item) {
            this.formData.fields[item.index] = item
            this.emitDataChange()
        },
        queryItemRemove(item) {
            this.formData.fields.splice(item.index, 1)
            this.emitDataChange()
        },
        editQueryItem(item) {
            this.selectedQuery= item
            this.showQueryConfigDialog = true
        },
        onQueryConfigOK(queryFields) {
            this.formData.fields = queryFields
            this.emitDataChange()
            this.showQueryConfigDialog = false
        },
        onQueryConfigCancel() {
            this.showQueryConfigDialog = false
        },
        showRename() {

        }
    },
}
</script>

<style lang="less" scoped>
.parameter-panel {
    width: 100%;
    height: 100%;
    padding-left: 10px;
    padding-right: 10px;


    .title-bar {
        height: 30px;
        display: flex;
        flex-direction: row;
        align-items: center;
        border-bottom: 1px solid #e0dbdb;

        .title-bar-title {
            flex: 1 1 auto;
            padding: 0 16px;
        }

        .title-bar-button {
            flex: 0 0;
            margin-right: 10px;
        }
    }

    .prop-name {
        width: 100%;
        height: 22px;
        margin-top: 15px;
        font-size: 12px;
        line-height: 12px;
        text-align: left;
        position: relative;
        width: 100%;
        display: inline-flex;

        .data-area-label {
            flex: 1 1 auto;
            font-weight: bold;

            .data-area-clear {
                color: #646a73;
                cursor: pointer;
                margin-top: 2px;
                margin-right: 2px;
            }
        }
    }

    .prop-value {
        width: 100%;
        min-height: 26px;
    }



    .drag-block-style {
        padding: 2px 0 0 0;
        width: 100%;
        min-height: 32px;
        border-radius: 4px;
        border: 1px solid #DCDFE6;
        overflow-x: hidden;
        display: flex;
        align-items: center;
        background-color: white;
    }

    .draggable-group {
        display: block;
        width: 100%;
        height: calc(100% - 6px);
    }

    .drag-placeholder-style {
        position: relative;
        top: -26px;
        left: 0;
        width: 100%;
        color: #CCCCCC;
    }

    .drag-placeholder-style-span {
        padding-left: 16px;
    }

    .parameter-table {
        height: 100%;
        padding: 10px 10px;
    }

    .field-list-container {
        margin-top: 20px;
        border-top: 1px solid #e0dbdb;
        padding-top: 10px;

        .field-list {
            overflow: auto;
            padding: 2px 0;
            background-color: #f0f2f5;

            .prop-name {
                width: 100%;
                height: 22px;
                margin-top: 15px;
                font-size: 12px;
                line-height: 12px;
                text-align: left;
                position: relative;
                width: 100%;
                display: inline-block;

                .data-area-label {
                    flex: 1 1 auto;
                    font-weight: bold;

                    .data-area-clear {
                        color: #646a73;
                        cursor: pointer;
                        margin-top: 2px;
                        margin-right: 2px;
                    }
                }
            }

            .drag-list {
                .field-item {
                    font-size: 12px;
                    height: 26px;
                    background-color: #fff;
                    margin: 5px 5px;
                    padding: 2px 5px;

                    &:hover {
                        color: #1890ff;
                        border-color: #a3d3ff;
                        cursor: pointer;
                    }
                }
            }
        }
    }

}
</style>