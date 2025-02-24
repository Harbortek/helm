<template>
    <a-modal v-model="visiable" title="视图管理" centered width="600" @cancel="onClose">
        <div class="dialog-description dialog-description-info manager-desc">
            <div class="dialog-description-border"></div>
            <div class="dialog-description-container">
                <p class="dialog-description-p style--font-14">
                    公共视图由管理员设置，私人视图仅自己可以查看。视图位置的调整，仅对自己的界面生效。
                </p>
            </div>
        </div>
        <vxe-toolbar size="medium">
            <template #tools>
                <vxe-button status="primary" content="新建视图" @click="onCreateView"></vxe-button>
            </template>
        </vxe-toolbar>

        <vxe-table ref="viewTable" :loading="loading" :data="tableData" :row-config="{ isHover: true }" height="400"
            :edit-config="{ trigger: 'manual', mode: 'row' }">
            <vxe-column field="" title="" width="40">
                <template #default="{ row }">
                    <div :class="['status-sorting-item']">
                        <i class="vxe-icon--menu"></i>
                    </div>
                </template>
            </vxe-column>
            <vxe-column field="name" title="视图名称" width="200">
                <template #default="{ row }">
                    {{ row.name }}
                </template>
            </vxe-column>
            <vxe-column field="type" title="视图类型" width="200">
                <template #default="{ row }">
                    {{ row.viewType === 'PUBLIC' ? '公共' : '私有' }}
                </template>
            </vxe-column>
            <vxe-column field="name" title="视图显示" width="200">
                <template #default="{ row }">
                    <a-switch :checked="row.display" @change="changeDisplay(row)" />
                </template>
            </vxe-column>
            <vxe-column field="name" title="操作" width="200">
                <template #default="{ row }">
                    <vxe-button type="text" status="primary" @click="onRenameView(row)">重命名</vxe-button>
                    <vxe-button :disabled="row.system" type="text" status="primary" @click="onDeleteView(row)">删除</vxe-button>
                </template>
            </vxe-column>
        </vxe-table>
        <template slot="footer">
            <a-button key="close" @click="onClose">
                关闭
            </a-button>
        </template>
        <create-view-dialog :isShowDialog="isShowCreateDialog" :editMode="viewEditMode" :view="currentView"
            :views="tableData" @ok="onCreateViewDialogOK" @cancel="isShowCreateDialog = false" />
    </a-modal>
</template>
<script>
import Sortable from "sortablejs"
import VXETable from 'vxe-table';
import {cloneDeep} from 'lodash'
import { findViewsByObjectId, createView, renameView, deleteView, updateView,updateViewsOrdinary } from "@/services/tracker/ViewService"
import CreateViewDialog from "./CreateViewDialog.vue";
export default {
    name: "ViewListDialog",
    components: { Sortable, CreateViewDialog },
    data() {
        return {
            showHelpTip1: false,
            tableData: [],
            isShowCreateDialog: false,
            viewEditMode: 'create',
            currentView: {},
            userId:'',
            loading:false,
            isChange:false,
        };
    },
    props: {
        isShowDialog: {
            required: true
        },
        objectId: {
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
    },
    watch: {
        objectId: {
            handler: function (newVal, oldVal) {
            }
        },
        isShowDialog: {
            handler: function (newVal, oldVal) {
                if(newVal){
                    this.loadData();
                }

            }
        },
    },
    mounted() {
        this.userId = this.$store.getters['account/user'].id;
    },
    methods: {
        loadData() {
            if (this.objectId) {
                findViewsByObjectId(this.objectId,this.userId,false).then(resp => {
                    this.tableData = resp
                    console.log("tableData",this.tableData)
                }).finally(()=>{
                })                
            }

            if (!this.sortable) {
                this.rowDrop()
            }
        },
        changeDisplay(view) {
            view.display = !view.display
            console.log("row",view)
            this.isChange=true;
            updateView(view).then((res)=>{
                console.log("ers",res)
            })
        },
        onOK() {
            let index = 1;
            this.tableData.forEach(item => { item.ordinary = index++ })
            this.$emit("ok", this.tableData);
        },
        onClose() {
            this.$emit("close",this.isChange);
        },
        rowDrop() {
            let that = this
            this.$nextTick(() => {
                let xTable = that.$refs.viewTable;
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
                                let ordinary=newArray[newIndex].ordinary
                                newArray[newIndex].ordinary=newArray[oldIndex].ordinary
                                newArray[oldIndex].ordinary=ordinary;
                                that.$nextTick(function () {
                                    that.tableData = newArray;
                                    updateViewsOrdinary([newArray[newIndex],newArray[oldIndex]]).then(()=>{
                                        this.isChange=true;
                                        this.loadData();
                                    })
                                });
                            },
                        }
                    );
                }
            });
        },
        onCreateView() {
            this.viewEditMode='create',
            this.currentView = {
                objectId: this.objectId,
                name: '',
                viewType: 'PRIVATE',
                display: true,
                system: false
            }
            this.isShowCreateDialog = true
        },
        onRenameView(row) {
            this.viewEditMode = 'edit'
            this.currentView = row
            this.isShowCreateDialog = true
        },
        onDeleteView(row) {
            VXETable.modal.confirm({
                title: '删除视图',
                content: '「' + row.name + '」将被删除，该动作不可恢复'
            }).then(type => {
                if (type === 'confirm') {
                    this.isChange=true;
                    deleteView(row.id).then(resp => {
                        VXETable.modal.message({ content: '「' + row.name + '」已被删除', status: 'info' })
                        this.loadData()
                    })
                }
            })
        },
        onCreateViewDialogOK(view) {
            this.isChange=true;
            if (this.viewEditMode === 'create') {
                const order = this.tableData.map(f => f.ordinary).filter(f =>f) || [0];
                console.log("order",Array.isArray(order),order)
                let MAXID = 0
                if (Array.isArray(order) && order.length > 0) {
                    MAXID = order.reduce((a, b) => {
                        console.log("reduce",a,b)
                        return Math.max(a, b);
                    });
                }
                view.ordinary = MAXID + 1
                createView(view).then(resp => {
                    this.isShowCreateDialog = false
                    this.loadData()
                })
            } else {
                renameView(view).then(resp => {
                    this.isShowCreateDialog = false
                    this.loadData()
                })
            }
        }
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
