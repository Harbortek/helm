<template>
    <a-modal v-model="visiable" title="工作项类型排序" @ok="onOK" @cancel="onCancel">
        <div class="dialog-description dialog-description-info manager-desc">
            <div class="dialog-description-border"></div>
            <div class="dialog-description-container">
                <p class="dialog-description-p style--font-14">
                    拖拽工作项类型，调整其展示顺序。
                </p>
            </div>
        </div>
        <vxe-table ref="xTable" :data="tableData" :row-config="{ isHover: true }" height="400"
            :edit-config="{ trigger: 'manual', mode: 'row' }">
            <vxe-column field="name" title="工作项名称" header-align="center">
                <template #default="{ row }">
                    <div :class="['status-sorting-item']">
                        <h-icon type="drag" />
                        {{ row.name }}
                    </div>
                </template>
            </vxe-column>
        </vxe-table>
    </a-modal>
</template>
<script>
import Sortable from "sortablejs"
import {cloneDeep} from 'lodash'
import { updateTrackerOrdinary } from '@/services/tracker/TrackerService'
export default {
    name: "TrackerListSortDialog",
    components: { Sortable },
    data() {
        return {
            showHelpTip1: false,
            tableData: [],
        };
    },
    props: {
        isShowDialog: {
            required: true
        },
        trackerList: {
            required: true
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
        trackerList: {
            handler: function (newVal, oldVal) {
                this.loadData();
            }
        },
        isShowDialog: {
            handler: function (newVal, oldVal) {
                this.loadData();
            }
        },
    },
    mounted() {
        this.loadData();
    },
    methods: {
        closeDialog() {
            this.$emit("close");
        },
        loadData() {
            this.tableData = cloneDeep(this.trackerList)
            if (!this.sortable) {
                this.rowDrop()
            }
        },
        onOK() {
            let index = 1;
            this.tableData.forEach(item => { item.ordinary = index++ })
            updateTrackerOrdinary(this.tableData).then(resp => {
                this.$emit("ok");
            })
        },
        onCancel() {
            this.$emit("cancel");
        },
        rowDrop() {
            let that = this
            this.$nextTick(() => {
                let xTable = that.$refs.xTable;
                if (xTable) {
                    that.sortable = Sortable.create(
                        xTable.$el.querySelector(".body--wrapper>.vxe-table--body tbody"),
                        {
                            handle: ".vxe-body--row",
                            animation: 150,
                            filter: '.start-state',
                            onEnd: ({ newIndex, oldIndex }) => {
                                that.tableData.splice(newIndex, 0, this.tableData.splice(oldIndex, 1)[0]);
                                var newArray = this.tableData.slice(0);
                                that.tableData = [];
                                that.$nextTick(function () {
                                    that.tableData = newArray;
                                });
                            },
                        }
                    );
                }
            });
        },
    },
    created() {

    },
    beforeDestroy() {
        if (this.sortable) {
            this.sortable.destroy();
        }
    },
};
</script>
<style lang="less" scoped>
.status-sorting-item {
    max-width: 478px;
    // border-bottom: 1px solid #dedede;
    cursor: move;
    line-height: 34px;
    padding: 0 20px;

    &:hover {
        background: #f8f8f8;
    }

    .pin-status-tag {
        line-height: 34px;
        font-size: 12px;
        color: #909090;
        margin-left: 5px;
        padding: 1px 7px;
        border-radius: 100px;
        border: solid 1px #c7c7c7;
    }
}

.start-state {
    cursor: default;
}

.dialog-description {
    display: flex;
    margin-bottom: 10px;
    line-height: 1.5;
    box-sizing: border-box;
    border-bottom: 1px solid #e8e8e8;
    border-radius: 0;
    border-right: 1px solid #e8e8e8;
    border-top: 1px solid #e8e8e8;
}

.dialog-description-border {
    border-radius: 0;
    flex: none;
    width: 4px;
}

.dialog-description-info .dialog-description-border {
    background: #338fe5;
}

.dialog-description-container {
    background: #fff;
    display: flex;
    flex: auto;
    padding: 10px;
}

.dialog-description-p {
    margin: 0 0 0 0;
    padding: 0;
    white-space: pre-wrap;
}

.style--font-14 {
    font-size: 14px;
    line-height: 22px;
}
</style>
