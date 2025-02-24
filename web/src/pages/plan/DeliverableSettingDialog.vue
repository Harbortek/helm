<template>
    <a-modal v-model="visiable" title="设置交付物" @ok="onOK" @cancel="onCancel" centered :width="600">
        <vxe-table ref="xTable" :data="tableData" :row-config="{ isHover: true }" height="400"
            :edit-config="{ trigger: 'click', mode: 'cell', icon: 'vxe-icon-edit', showStatus: true }" show-footer
            :footer-method="footerMethod">
            <vxe-column width="30">
                <template #default>
                    <h-icon type="drag" />
                </template>
            </vxe-column>
            <vxe-column type="seq" title="序号" header-align="center" align="left" width="60">
                <template #footer="{}">
                    <vxe-button type="text" status="primary" icon="vxe-icon-add"
                        @click="onCreateDeliverable()">新建</vxe-button>
                </template>
            </vxe-column>
            <vxe-column field="name" title="交付物名称" header-align="left" :edit-render="{}">
                <template #edit="{ row }">
                    <vxe-input v-model="row.name" />
                </template>
                <template #default="{ row }">
                    {{ row?.name }}
                </template>
            </vxe-column>
            <vxe-column title="类型" header-align="center" align="center" width="100" :edit-render="{}">
                <template #edit="{ row }">
                    <vxe-select v-model="row.type" transfer>
                        <vxe-option key="FILE" value="FILE" :label="$t('deliverable.type.FILE')"></vxe-option>
                        <vxe-option key="URL" value="URL" :label="$t('deliverable.type.URL')"></vxe-option>
                    </vxe-select>
                </template>
                <template #default="{ row }">
                    {{ $t('deliverable.type.' + row?.type) }}
                </template>
            </vxe-column>
            <vxe-column title="操作" header-align="center" width="60">
                <template #default="{ row }">
                    <vxe-button type="text" circle icon="vxe-icon-delete" @click="onDeleteDeliverable(row)" />
                </template>
            </vxe-column>
        </vxe-table>
    </a-modal>
</template>
<script>
import {cloneDeep} from 'lodash'
import Sortable from "sortablejs"
export default {
    name: "DeliverableSettingDialog",
    components: { Sortable },
    data() {
        return {
            tableData: [],
        };
    },
    props: {
        isShowDialog: {
            required: true
        },
        projectId: {
            required: true
        },
        milestoneId: {
            required: true
        },
        deliverableList: {
            required: true,
            default: () => { return [] }
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
    mounted() {
        this.loadData();
    },
    methods: {
        loadData() {
            this.tableData = cloneDeep(this.deliverableList)
            if (!this.sortable) {
                this.rowDrop()
            }
        },
        onOK() {
            this.$emit("ok", this.tableData);
        },
        onCancel() {
            this.$emit("cancel");
        },
        footerMethod({ columns, data }) {
            return [
                columns.map(column => {
                    return null
                })
            ]
        },
        onCreateDeliverable() {
            this.tableData.push({ projectId: this.projectId, milestoneId: this.milestoneId, name: '', type: 'FILE' })
        },
        onDeleteDeliverable(row) {
            // this.tableData = this.tableData.filter(row => { row.id != deliverable.id })
            this.$refs.xTable.remove(row)
            this.tableData = this.$refs.xTable.getTableData().tableData
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
    created() { }
};
</script>
<style lang="less" scoped></style>
