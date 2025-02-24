<template>
    <a-modal v-if="visiable" v-model="visiable" title="选择工作项属性" @ok="onOK" @cancel="onCancel">
        <template slot="footer">
            <vxe-button content="取消" @click="onCancel"></vxe-button>
            <vxe-button status="primary" @click="onOK" content="确认"></vxe-button>
        </template>
        <!-- <div class="table-box">
            <vxe-table ref="vxeTable" row-id="id" style="cursor: pointer;" :data="trackerFields" :loading="loading"
            @checkbox-all="selectChangeEvent" @checkbox-change="selectChangeEvent" :row-config="{useKey:true,drag:true }"
            :checkbox-config="{ checkRowKeys: selectedRowKeys ,checkField: 'checked', trigger: 'row' }" :column-config="{useKey:true}">

                <vxe-column type="checkbox" title="" width="30" drag-sort></vxe-column>
                <vxe-column field="name" title="全选" drag-sort></vxe-column>
            </vxe-table>
        </div> -->
        <div class="ui-table" @mouseenter="rowDropHeader">
            <div class="table-head layout-fields-table-row order-row-header-bottom">
                <div style="">
                    <vxe-checkbox v-model="customRowCheckAll" :indeterminate="indeterminate"
                        @change="onChangeCustomRowAll" content="全选"></vxe-checkbox>
                </div>
            </div>
            <div style="overflow:auto;">
                <vxe-checkbox-group v-model="customRowCheck" @change="onChangeCustomRow">
                    <div ref="orderByRow" style="overflow: auto;">
                        <div class="order-row-header" v-for="item in customRowList"
                            :key="item.id">
                            <div class="layout-fields-table-row table-row">
                                <div style="padding-left: 0;width: 160px;">
                                    <vxe-checkbox :label="item.id"></vxe-checkbox>
                                    <i class="vxe-icon--menu" style="margin-right: 3px;"></i>
                                    {{ item.name }}
                                </div>
                            </div>
                        </div>
                    </div>
                </vxe-checkbox-group>
            </div>
        </div>
    </a-modal>
</template>
<script>
import {cloneDeep} from 'lodash'
import Sortable from "sortablejs"
export default {
    name: "TrackerFieldsSelectModal",
    components: {Sortable},
    data() {      
        return {
            loading: false,
            imageUrl: '',
            selectedRowKeys: [],
            selectedRows: [],

            customRowCheckAll: false,
            indeterminate:false,
            customRowCheck: [],
            customRowList: [],
        };
    },
    props: {
        isShowDialog: {
            required: true
        },
        initalData: {
            required: false
        },
        trackerFields: {
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
        isShowDialog: {
            handler: function (newVal, oldVal) {
                if(newVal){
                    this.customRowList = Object.assign([], this.trackerFields);
                    this.selectData();
                }
            }
        },
    },
    mounted() {
    },
    methods: {
        onChangeCustomRowAll () {
            if (this.customRowCheckAll) {
                this.customRowCheck = this.trackerFields.map(item => item.id)
            } else {
                this.customRowCheck = [];
            }
            this.indeterminate = false;
        },
        onChangeCustomRow () {
            if(this.customRowCheck.length == this.trackerFields.length){
                this.customRowCheckAll = true;
                this.indeterminate = false;
            }else if (this.customRowCheck.length == 0) {
                this.indeterminate = false;
            } else {
                this.indeterminate = true;
            }
            
        },

        selectData() {
            // 重置选中状态
            this.customRowCheck = [];
            
            // 将 trackerFields 分成两组：选中的和未选中的
            let selectedFields = [];
            let unselectedFields = [];

            this.initalData.forEach(id => {
                const field = this.trackerFields.find(f => f.id === id);
                if (field) {
                    selectedFields.push(field);
                    this.customRowCheck.push(field.id); // 更新选中状态
                }
            });
            unselectedFields=this.trackerFields.filter(f => !this.initalData.includes(f.id));
            
            // 更新 customRowList，将选中的排在前面
            this.customRowList = [...selectedFields, ...unselectedFields];
            
            // 更新全选和半选状态
            this.onChangeCustomRow();
        },
        loadData() {
           
        },
        onOK() {
            let sortedCustomRowCheck = this.customRowList
                .filter(item=>this.customRowCheck.includes(item.id)).map(item=>item.id);
            this.$emit("ok",sortedCustomRowCheck);
            this.initalData.splice(0,this.initalData.length, ...sortedCustomRowCheck)
            this.onCancel();
        },
        onCancel() {
            this.selectedRowKeys=[]
            this.selectedRows=[]
            // this.$refs["vxeTable"].clearCheckboxRow()
            this.$emit("cancel");
        },
        rowDropHeader(visiable) {
            if (visiable) {
                let that = this
                this.$nextTick(() => {
                    let xTable = that.$refs.orderByRow;
                    if (xTable) {
                        that.sortable = Sortable.create(
                            xTable,
                            {
                                handle: ".order-row-header",
                                animation: 150,
                                filter: '.order-row-header-bottom',
                                onEnd: ({ newIndex, oldIndex }) => {
                                    that.customRowList.splice(newIndex, 0, this.customRowList.splice(oldIndex, 1)[0]);
                                    var newArray = this.customRowList.slice(0);
                                    that.customRowList = [];
                                    that.$nextTick(function () {
                                        that.customRowList = newArray;
                                    });
                                },
                            }
                        );
                    }
                });
            }
        },

    },
    // beforeDestroy() {
    //     if (this.sortable) {
    //         this.sortable.destroy();
    //     }
    // },
    created() { }
};
</script>
<style lang="less" scoped>
.avatar-uploader{
    padding:0 30px;
    /deep/ .ant-upload {
        width: 100%;
        height: 128px;
    }
} 
.table-box {
    height: 480px;
    box-sizing: border-box;
    direction: ltr;
    position: relative;
    will-change: transform;
    overflow-y: auto;
    transition: height .25s;
}
.ui-table {
    // height: 400px;
    max-height: 480px;
    // border: solid 1px #dedede;
    border-radius: 3px;
    background-color: #fff;
    display: flex;
    flex-direction: column;
    ::v-deep .vxe-checkbox-group{
        width: 100%;
    }
    .table-head {
        width: 100%;
        padding: 0 20px;
        font-size: 14px;
        color: #303030;
        font-weight: 500;
        height: 43px;
        line-height: 43px;
        background-color: #f8f8f8;
        // border-bottom: solid 1px #dedede;
        box-sizing: border-box;
        border-top: 1px solid #e9ebed;
    }
    .layout-fields-table-row {
        display: flex;
        flex-direction: row;
        justify-content: space-between;
        min-height: 40px;
        padding-right: 20px;
        padding-left: 20px;
        padding-top: 10px;
        padding-bottom: 10px;
        align-items: center;
        border: 1px solid #e9ebed;
        border-top: none;
    }
    .table-row {
        width: 100%;
        padding: 0 20px;
        height: 43px;
        line-height: 43px;
        // border-bottom: solid 1px #dedede;
        font-size: 14px;
        color: #303030;
        box-sizing: border-box;
        cursor: move;
    }

    .table-row:hover {
        background-color: #f6f6f6;
    }
}
</style>
