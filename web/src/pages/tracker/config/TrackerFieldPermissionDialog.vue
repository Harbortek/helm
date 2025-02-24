<template>
    <a-modal width="800px" v-model="visiable" title="编辑工作项属性权限" @ok="onOK" @cancel="onCancel">
            <div v-if="trackerField.permission" class="table-view">
                <div style="">
                    <span style="font-size:14px;font-weight: 700;color: rgb(48,48,48);">权限类型：</span>
                    <vxe-radio-group style="margin-left:20px;" v-model="trackerField.permission.type">
                        <vxe-radio style="line-height: 3;"  :content="$t('tracker.field.permission.type.Unrestricted')" label="Unrestricted"></vxe-radio>
                        <vxe-radio :content="$t('tracker.field.permission.type.Single')" size="small" label="Single"></vxe-radio>
                        <vxe-radio :content="$t('tracker.field.permission.type.PerStatus')" size="small" label="PerStatus"></vxe-radio>
                    </vxe-radio-group>
                </div>
                <div v-if="trackerField.permission.type=='Single'">
                    <role-members-table :projectId="projectId" :value="trackerField.permission.singlePermissions" scope="SCOPE_TRACKER"
                        title="以下成员域拥有此操作权限" tableHeight="auto" @delete="e => onDeleteSingle(e, trackerField.permission.type)"
                        @add="e => onAddSingle(e, trackerField.permission.type)" />
                </div>
                
                <div v-else-if="trackerField.permission.type=='PerStatus'">
                    <vxe-table border size="medium" show-footer :footer-method="footerMethod" stripe
                        :data="trackerField.permission.statusPermissions?tracker.trackerStatuses:[]" :loading="loading" :row-config="{ isHover: true }"
                    > 
                        <vxe-column :width="200">
                            <template #header>
                                <div class="first-col">
                                    <div class="first-col-top">角色</div>
                                    <div class="first-col-bottom">状态</div>
                                </div>
                            </template>
                            <template #default="{ row }">
                                {{ row.name }}
                            </template>
                            <template #footer>
                                <project-role-member-select :projectId="projectId" scope="SCOPE_TRACKER" :exclude="tableData"
                                    style="width:400px;z-index: 1;" @change="onChangeRolePerStatus" />
                            </template>
                        </vxe-column>
                        <template v-if="trackerField.permission.statusPermissions">
                            <vxe-column v-for="item in tableData" :key="item.id" align="center" show-overflow>
                                <template #header>
                                    <span>{{item.name}}</span>
                                    <vxe-button style="font-size: 12px;float:right" title="移除" type="text" icon="vxe-icon-close" @click="onClickDeletePerStatus(item.id,trackerField)"></vxe-button>
                                </template>
                                <template #default="{ row }">
                                    <vxe-checkbox v-if="trackerField.permission.statusPermissions[row.id]" 
                                      @change="onCheckChangedField(trackerField.permission.statusPermissions[row.id],item)" :value="getCheckBoxValue(trackerField.permission.statusPermissions[row.id],item)"></vxe-checkbox>
                                </template>
                            </vxe-column>
                        </template>
                    </vxe-table>
                </div>
                <!-- <div v-else>
                    <div style="height:200px"></div>
                </div> -->
            </div>
    </a-modal>
</template>
<script>
import VxeTable from 'vxe-table'
import Verte from 'verte';
import 'verte/dist/verte.css';
import {cloneDeep} from 'lodash'
import Vue from "vue";
import ProjectRoleMemberSelect from "@/components/select/ProjectRoleMemberSelect.vue"
import RoleMembersTable from '../../../components/table/RoleMembersTable.vue';
export default {
    name: "TrackerFieldPermissionDialog",
    components: { Verte,ProjectRoleMemberSelect,RoleMembersTable },
    data() {
        return {
            projectId: '',
            tableData: [],
            loading:false,
            selectValue:undefined,
        };
    },
    props: {
        isShowDialog: {
            required: true
        },
        editMode: {
            required: true
        },
        trackerField: {
            required: true
        },
        tracker: {
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
        trackerField: {
            handler: function (newVal, oldVal) {
                this.loadData();
            }
        }
    },
    mounted() {
        this.projectId = this.$route.params.projectId
    },
    methods: {
        closeDialog() {
            this.$emit("close");
        },
        loadData() {
            console.log("loadData",this.getRoleMembers())
            this.tableData=this.getRoleMembers();
        },
        getCheckBoxValue(row,item){
            for(let role of row){
                if(role.id==item.id){
                    return true;
                }
            }
            return false;
        },
        getRoleMembers(){
            let roleMember = [];
            let falg=true;
            for (let item of this.tracker.trackerStatuses) {
                if(!this.trackerField.permission?.statusPermissions||!this.trackerField.permission.statusPermissions[item.id]){
                    break;
                }
                for(let item2 of this.trackerField.permission.statusPermissions[item.id]){
                    falg=true;
                    for(let i in roleMember){
                        if(roleMember[i].id==item2.id){
                            falg=false;
                            break;
                        }
                    }
                    if(falg){
                        roleMember.push(item2)
                    }
                }
            }
            return roleMember;
        },
        onOK() {
        
            let tf = cloneDeep(this.trackerField)
            if(tf.permission.type=="Single"){
                tf.permission.statusPermissions=null;
            }else if(tf.permission.type=="PerStatus"){
                tf.permission.singlePermissions=null;
            }else{
                tf.permission.statusPermissions=null;
                tf.permission.singlePermissions=null;
            }
           
            this.$emit("ok", tf);
        },
        onCancel() {
            this.$emit("cancel");
        },
        //1
        onDeleteSingle(row, permission) {
             this.trackerField.permission.singlePermissions = 
                this.trackerField.permission.singlePermissions.filter(item => { return item.id != row.id })
        },
        onAddSingle(row, permission) {
            
        },
        onChangeRolePerStatus(v,row){
            this.tableData.push(row)
            this.loading=true;
            this.trackerField.permission.statusPermissions
            if(!this.trackerField.permission.statusPermissions){
                this.trackerField.permission.statusPermissions=Vue.observable({}) //生成Observer响应式对象
            }
            for (let item of this.tracker.trackerStatuses) {
                if(!this.trackerField.permission.statusPermissions[item.id]){
                    this.trackerField.permission.statusPermissions[item.id]=Vue.observable([])
                }
                this.trackerField.permission.statusPermissions[item.id].push(row)
            }
            this.loading=false;
            
        },
        onClickDeletePerStatus(roleId,record){
            this.loading=true;
            for (let item of this.tracker.trackerStatuses) {
                for(let i in this.trackerField.permission.statusPermissions[item.id]){
                    if(roleId==this.trackerField.permission.statusPermissions[item.id][i].id){
                        this.$delete(this.trackerField.permission.statusPermissions[item.id], i);
                        break;
                    }
                }
                if(this.trackerField.permission.statusPermissions[item.id].length==0){
                    this.trackerField.permission.statusPermissions=null;
                    break;
                }
            }
            this.tableData=this.tableData.filter(v=> { return v.id != roleId })
            this.loading=false;
        },
        onCheckChangedField(row,item){
            this.loading=true; 
            if(this.getCheckBoxValue(row,item)){
                for(let i in row){
                    if(row[i].id==item.id){
                        this.$delete(row,i);
                        break;
                    }
                }
            }else{
                row.push(item)
            }
            this.loading=false;
        },        
        footerMethod: function ({ columns, data }) {
            return [
                columns.map(column => {
                    return null
                })
            ]
        },
    },
    created() { }
};
</script>
<style lang="less" scoped>
.box {
    width: 20px;
    height: 20px;
    border: solid 1px black;
}
.table-view {
    .first-col {
        position: relative;
        height: 20px;
    }

    .first-col:before {
        content: "";
        position: absolute;
        left: -14px;
        top: 10px;
        width: 204px;
        height: 1px;
        transform: rotate(13deg);
        background-color: #e8eaec;
    }

    .first-col .first-col-top {
        position: absolute;
        right: 4px;
        top: -10px;
    }

    .first-col .first-col-bottom {
        position: absolute;
        left: 4px;
        bottom: -10px;
    }
}
</style>
