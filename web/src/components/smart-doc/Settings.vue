<template>
    <a-modal v-model="visiable" title="设置" width="800px" @ok="onOK" @cancel="onCancel" centered>
        <a-form-model ref="settingForm" layout="horizontal" :model="formData" :rules="rules">

            <!-- <a-form-model-item ref="trackerId" label="默认工作项" prop="trackerId">
                <tracker-select style="width:50%;" v-model="formData.trackerId" @change="onChangeTracker" :projectId="projectId" />
            </a-form-model-item> -->
            <!-- <a-form-model-item ref="trackerIds" label="可选工作项" prop="trackerIds">
                <tracker-select :mode="'tags'" v-model="formData.trackerIds" :projectId="projectId"/>
            </a-form-model-item> -->
            <a-form-model-item ref="pageSettingTrackers" label="工作项显示内容" prop="pageSettingTrackers">
                <vxe-table ref="rtable" size="medium" :data="getTableData"
                    show-footer :footer-method="footerMethod"  max-height="400">
                    <vxe-column title="工作项">
                        <template #default="{ row }">
                            <a-icon v-if="row.trackerType?.icon" :style="{'color':row.trackerType?.color}" :component="row.trackerType?.icon" />
                            &nbsp;{{ row.name }}
                        </template>
                        <template #footer="{}">
                            <a-select v-model="selectItem" placeholder="请选择" @change="onChangeSelect">
                                <a-select-option v-for="item in getTrackerList" :key="item.id" :title="item.name" :value="item.id">
                                    <a-icon v-if="item.trackerType?.icon" :style="{'color':item.trackerType?.color}" :component="item.trackerType?.icon" />
                                        &nbsp;{{ item.name }}
                                </a-select-option>  
                            </a-select>
                        </template>
                    </vxe-column>
                    <vxe-column title="内容" show-overflow>
                        <template #default="{ row }">
                            <a-select v-model="row.content">
                                <a-select-option title="标题" value="title">标题</a-select-option>
                                <a-select-option title="描述" value="description">描述</a-select-option>
                                <a-select-option title="标题和描述" value="title,description">标题和描述</a-select-option>
                            </a-select>
                        </template>
                    </vxe-column>
                    <vxe-column title="操作" align="center"  show-overflow >
                        <template #default="{ row }">
                            <vxe-button @click="onClickDelete(row)" status="padding-left:0" type="text" icon="vxe-icon-delete"></vxe-button>
                            <a-button @click="onClickSelect(row)">选择字段</a-button>
                        </template>
                    </vxe-column>
                </vxe-table>
            </a-form-model-item>
            
        </a-form-model>
        <tracker-fields-select-modal :isShowDialog="isShowSelectDialog" :initalData="tracker.fieldIds" @cancel="onCancelSelect"
            :trackerFields="tracker.trackerFields||[]"></tracker-fields-select-modal>
    </a-modal>
</template>
<script>
import TrackerSelect from '@/components/select/TrackerSelect.vue';
import TrackerFieldsSelectModal from '@/components/dialog/TrackerFieldsSelectModal.vue'
import {
  findTrackers,findOneTracker
} from "@/services/tracker/TrackerService";
export default {
    name: "SmartDocSettingsDialog",
    components: { TrackerSelect,TrackerFieldsSelectModal},
    data() {
        return {
            formData: {
                // trackerId: '',
                pageSettingTrackers: [],
            },
            rules: {
                // trackerId: [{ required: true, message: "请选择默认工作项类型", trigger: "change" },]
            },
            tableData: [],
            trackerList: [],
            selectItem: undefined,
            isShowSelectDialog: false,
            tracker:{},
        };
    },
    props: {
        isShowDialog: {
            required: true
        },
        projectId: {
            required: true
        },
        // trackerId: {
        //     required: false,
        //     default: ''
        // },
        pageSettingTrackers: {
            required: false,
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
        getTrackerList(){
            let trackerIds=this.tableData.map(t=>t.id);
            if(trackerIds){
                return this.trackerList.filter(t=>trackerIds.indexOf(t.id)==-1);
            }
            return this.trackerList; 
        },
        getTableData(){
            this.tableData.forEach(item=>{
                if(!item.name||!item.trackerFields){
                    let tracker=this.trackerList.find(t=>t.id==item.id)
                    item.name=tracker?.name
                    item.trackerFields=tracker?.trackerFields||[]
                }
            })
            return this.tableData;
        }
    },
    watch: {
        isShowDialog: {
            handler: function (newVal, oldVal) {
                if (newVal) {
                    this.loadData();
                }
            }
        },
        // trackerId: {
        //     immediate: true,
        //     handler: function (newVal, oldVal) {
        //         this.formData.trackerId = newVal;
        //     }
        // },
        pageSettingTrackers: {
            immediate: true,
            handler: function (newVal, oldVal) {
                if(newVal){
                    this.tableData = newVal.map(item=>{
                        item.fieldIds=item.fields.map(_=>_.id)
                        return item;
                    })
                    // if(!this.tableData.find(t=>t.id==this.formData.trackerId)){
                    //     findOneTracker(this.trackerId).then(res=>{
                    //         this.tableData.unshift({id:this.formData.trackerId,name:res.name,
                    //             content:'title,description',fieldIds:[],trackerFields:res.trackerFields});
                    //     })                        
                    // }
                }
            }
        }
    },
    mounted() {
        this.loadData();
    },
    methods: {
        // onChangeTracker(trackerId){
        //     let tarcker=this.trackerList.find(e=>e.id==trackerId)
        //     this.tableData.splice(0,1,{id:this.formData.trackerId,name:tarcker.name,
        //         content:'title,description',fieldIds:[],trackerFields:tarcker.trackerFields})
        // },
        onClickSelect(row){
            this.tracker=row;
            if(!this.tracker.fieldIds){
                this.tracker.fieldIds=[]
            }
            this.isShowSelectDialog=true;
        },
        onCancelSelect(){
            this.isShowSelectDialog=false;
        },
        onClickDelete(row){
            this.tableData=this.tableData.filter(e=>e.id!=row.id);
        },
        onChangeSelect(v){
            let item=this.trackerList.find(e=>e.id==v)
            if(item){
                this.tableData.push({id:item.id,name:item.name,content:'description',fieldIds:[],trackerFields:item.trackerFields});
            }
            this.selectItem=undefined;
        },
        footerMethod: function ({ columns, data }) {
            return [
                columns.map(column => {
                    return null
                })
            ]
        },
        closeDialog: function () {
            this.$emit("close");
        },
        loadData: function () {
            let projectId=this.projectId
            if(this.trackerList.length==0){
                findTrackers({projectId}).then(res=>{
                    res.forEach(item=>{
                        item.trackerFields=item.trackerFields.filter(e=>e.systemProperty!=='description'&&e.systemProperty!=='name')
                    })
                    this.trackerList=res;
                })
            }
        },  
        onOK: function () {
            this.$refs["settingForm"].validate((valid) => {
                if (valid) {
                    this.tableData.forEach(item=>{
                        this.$delete(item,"trackerFields");
                        this.formData.pageSettingTrackers.push(item)
                    })
                    this.$emit("SAVE_SETTINGS", this.formData);
                }
            })
        },
        onCancel: function () {
            this.$emit("CANCEL");
        }
    }
};
</script>
<style lang="less" scoped></style>
