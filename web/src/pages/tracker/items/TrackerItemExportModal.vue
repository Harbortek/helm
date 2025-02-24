<template>
    <a-modal v-if="visiable" v-model="visiable" title="选择导出属性" @ok="onOK" @cancel="onCancel">
        <template slot="footer">
            <vxe-button content="关闭" @click="onCancel"></vxe-button>
            <vxe-button status="primary" :disabled="selectedRowKeys.length==0" @click="onOK" content="导出"></vxe-button>
        </template>
        <div class="table-box">
            <vxe-table ref="vxeTable" row-id="id" style="cursor: pointer;" :data="customRowList" :loading="loading"
            @checkbox-all="selectChangeEvent" @checkbox-change="selectChangeEvent" :row-config="{ isHover: true }"
            :checkbox-config="{ checkRowKeys: selectedRowKeys ,checkField: 'checked', trigger: 'row' }">

                <vxe-column type="checkbox" title="" width="30"></vxe-column>
                <vxe-column field="name" title="全选"></vxe-column>
            </vxe-table>
        </div>
    </a-modal>
</template>
<script>
import {cloneDeep} from 'lodash'
export default {
    name: "TrackerItemExportModal",
    components: {},
    data() {      
        return {
            loading: false,
            imageUrl: '',
            selectedRowKeys: [],
            selectedRows: [],
        };
    },
    props: {
        isShowDialog: {
            required: true
        },
        customRowList: {
            required: true
        },
        customRowCheck: {
            required: false
        },
        trackerId:{
            required: false
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
                if(newVal){
                    this.selectData();
                }
            }
        },
    },
    mounted() {
    },
    methods: {
        selectChangeEvent({ checked, records, reserves }) {
            this.selectedRowKeys = [...reserves.map(v => v.id), ...records.map(v => v.id)];
            this.selectedRows = [...reserves, ...records];
        },
        selectData() {   
            this.selectedRowKeys=[]
            this.selectedRows=[]
            for (let i = 0; i < this.customRowCheck.length; i++) {
                this.selectedRowKeys.push(this.customRowCheck[i])
            }
            this.$nextTick(() => {
                for (let item2 of this.customRowList) {
                    for (let item of this.customRowCheck) {
                        if (item == item2.id){
                            this.selectedRows.push(item2);
                            this.$refs["vxeTable"].setCheckboxRow(item2, true)
                        }
                    }
                }
            })
        },
        loadData() {
           
        },
        onOK() {
           console.log("onCok",this.selectedRows)
           this.$emit("ok",this.selectedRowKeys);
           this.onCancel();
        },
        onCancel() {
            this.selectedRowKeys=[]
            this.selectedRows=[]
            this.$refs["vxeTable"].clearCheckboxRow()
            this.$emit("cancel");
        }
    },
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
</style>
