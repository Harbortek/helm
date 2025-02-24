<template>  
    <a-modal :visible="isShowDialog"  width="1000px"  title="编辑列表属性"  @cancel="onCancel">
        <template #footer>
            <a-button @click="onCancel">关闭</a-button>
        </template>
        <config-page style="padding:0;" title="" description="定义该工作项类型下的工作项在“看板”视图可以显示的属性信息。除标题，描述和状态外，最多可以添加 5 个属性。选项配色请到配置中心的工作项属性里调整。">
            <div style="height:45px;">
                <a-button style="float:right" type="primary" @click="onClickAddListFields">添加列表属性</a-button>
            </div>
            <a-card style="border:none;" :bodyStyle="{padding:'24px 0'}"  trigger="['click']]" placement="bottom" @mouseenter="rowDrop">
                <div class="ui-table">
                    <div class="table-head layout-fields-table-row order-row-bottom">
                        <div style="">列表属性</div>
                        <div class="">操作</div>
                    </div>
                    <div ref="orderByTable">
                        <div class="order-row" v-for="item in listFields" :key="item.id">
                            <div class="layout-fields-table-row table-row"  >   
                                <div style="padding-left: 0;width: 235px;" >
                                    <i class="vxe-icon--menu"></i>
                                    {{item.name}}
                                    <a-tag v-if="item.system" class="system-tag-box">系统</a-tag>
                                </div>
                                <vxe-button style="font-size: 14px;" title="移除" type="text" icon="vxe-icon-close"
                                    @click="onClickDeleteListFields(item)"></vxe-button>
                            </div>
                        </div>
                    </div>
                </div>
            </a-card>

            <div class="preview-content">
                <div class="title-box">
                    <span style="color: #909090;">预览效果</span>
                </div>
                <div style="display:flex;flex-direction: row;">
                    <vxe-table style="background-color: #fff;width:95%;"
                         :show-header="false"  :data="[...itemData,...itemData,...itemData]"  >
                        <template v-for="item in listFields">
                            
                        <vxe-column v-if="item.systemProperty=='priority'" :field="item.systemProperty" :key="item.id" title="" :width="60">
                            <template #default="{ row }">
                                <a-tooltip :title="'优先级：'+row.priority.name" :overlayStyle="{fontSize:'10px'}">
                                <a-tag style="border:none;cursor: pointer;"
                                    :style="{ color: row.priority.color, backgroundColor: row.priority.backgroundColor }">{{
                                        row.priority.name }}</a-tag>
                                </a-tooltip>
                            </template>
                        </vxe-column>

                        <vxe-column v-else :field="item.systemProperty" :key="item.id" title="" :width="item.name.length*10+30">
                            <template  #default="{ row }">
                                <a-tooltip :title="item.name+'：'+(row[getsystemProperty(item.systemProperty)]?.name||item.name)" :overlayStyle="{fontSize:'10px'}">
                                <a-tag class="tag-box">
                                    {{ row[getsystemProperty(item.systemProperty)]?.name||item.name }}</a-tag>
                                </a-tooltip>
                            </template>
                        </vxe-column>

                        </template>

                        <vxe-column field="name" title="" width="">
                            <template #default="{ row }">
                                {{ row.name }}
                            </template>
                        </vxe-column>
                        
                        <vxe-column field="status.name" title="">
                            <template #default="{ row }">
                                <div class="transition-status">
                                    <span class="ui-tag-status"
                                        :style="{ color: row.meaning.color, 'border-color': row.meaning.color }">{{
                                            row.status.name }}</span>
                                </div>
                            </template>
                        </vxe-column>
                    </vxe-table>
                </div>
            </div>  
        </config-page>
        <a-modal :visible="addListFieldsVisible" centered width="520px"  title="添加列表属性" @ok="onOKAddListFields"  @cancel="addListFieldsVisible=false">
            <a-form-model :model="formData">
                <a-form-model-item prop="keyfield">
                    <a-select placeholder="选择列表属性" style="width: 460px;" show-search optionFilterProp='children' v-model="formData.keyfield">
                        <a-select-option v-for="item in addListFields" :key="item.id" :value="item.id">{{item.name}}</a-select-option>   
                    </a-select>
                </a-form-model-item>
            </a-form-model>
        </a-modal>
    </a-modal>
</template>
<script>
import ConfigPage from '../../../components/config-page/ConfigPage.vue';
import {
    updateTrackerLayout
} from "@/services/tracker/TrackerService";
import Sortable from "sortablejs"
import QuickPicker from '../../../components/select/QuickPicker.vue';
export default {
    name: "TrackerLayoutListFieldsDialog",
    components: { ConfigPage, Sortable, QuickPicker },
    props: {
        isShowDialog: {
            required: true
        },
        tracker: {
            required: true
        },
    },
    data() {
        return {
            loading: false,
            trackerId: '',
            formData: {
                keyfield: undefined,
            },
            addListFieldsVisible: false,
            listFields: [],
            itemData: [
                {
                    "id": "1183942482254106624",
                    "name": "这是一个工作项示例",
                    "createBy": {
                        "name": "胡隽",
                        "id": "1150207089906290688",
                        "icon": "/helm/2023-06-12/59c86c4f-13da-4283-b4df-b1bfc26f2b28.blob"
                    },
                    "createDate": "2023-06-07 16:59:39",
                    "lastModifiedBy": {
                        "name": "胡隽",
                        "id": "1150207089906290688",
                    },
                    "lastModifiedDate": "2023-06-07 16:59:39",
                    "project": {
                        "id": "1183917925904420864",
                        "name": "需求管理项目",
                    },
                    "itemNo": "1",
                    "owner": {
                        "name": "胡隽",
                        "id": "1150207089906290688",
                    },
                    "priority": {
                        "name": "最高",
                        "color": "rgb(255, 255, 255)",
                        "backgroundColor": "rgb(230, 52, 34)",
                    },
                    "status": {
                        "id": "1183917933584191500",
                        "name": "未开始",
                    },
                    "meaning": {
                        "id": "1",
                        "name": "未开始",
                        "color": "#338fe5",
                    },
                    "values": {
                        "1183917933584191526": "功能需求",
                        "1183917933584191527": "标准",
                        "1183917933584191528": "重复",
                        "1183917933584191529": "Not Completed"
                    },
                    "watchers": [
                        {
                            "type": "IDENTITY_SPECIAL_ROLE",
                            "name": "工作项负责人",
                            "id": "1183917930480406537"
                        },
                        {
                            "type": "IDENTITY_SPECIAL_ROLE",
                            "name": "工作项创建者",
                            "id": "1183917930480406538"
                        }
                    ],
                    "relatedWorkItems": [],
                    "attachments": []
                },
            ],
        };
    },
    computed: {
        addListFields(){
            let fields = this.tracker.trackerFields || []
            console.log("this.aa",this.tracker.trackerFields)
            fields = fields.filter(f => {
                return f.inputType === 'INTEGER' || f.inputType === 'TEXT' || f.inputType === 'STATUS'
                    || f.inputType === 'DATE' || f.inputType === 'USER' || f.inputType === 'OPTIONS'
                    || f.inputType === 'WORK_ITEM_NO'
            })
            fields = fields.filter(f => {
                return f.systemProperty !== 'name' && f.name !== 'status' && f.name !== 'description'
            })

            let ids=this.listFields.map(f => f.id);       
            let result = fields.filter(f => { return ids.indexOf(f.id) < 0 })
            return result
        },
    },
    mounted() {

    },
    watch: {
        'isShowDialog': function (value) {
            this.initData();
        }
  },
    methods: {
        getsystemProperty(systemProperty){
            if(systemProperty){
                systemProperty=systemProperty.toString();
                if(systemProperty.endsWith('Id')){
                    systemProperty=systemProperty.substring(0,systemProperty.length-2)
                }
            }
            return systemProperty
        },
        initData(){
            let layout = this.findLayout()
            //ListFields
            if (layout) {
                const fields = layout.fields
                this.listFields=[];
                fields.forEach(f => {
                    let field = this.findFields(f.id)
                    this.listFields.push(field)
                })
            }
        },
        onClickAddListFields(){
            if(this.listFields.length<5){
                this.addListFieldsVisible=true;
            }else{
                this.$message.warn('列表属性不能超过5个');
            }
        },
        onOKAddListFields(){
            console.log("oooonk",this.formData.keyfield)
            if(this.formData.keyfield){
                let field = this.findFields(this.formData.keyfield)
                if (field) {
                    this.listFields.push(field)
                    let layout = this.findLayout()
                    layout.fields=Object.assign([],this.listFields);
                    updateTrackerLayout(this.tracker.id,layout).then(resp => {
                        this.$message.success("操作成功")   
                        this.$emit("refresh",this.listFields);
                    })
                }
                this.addListFieldsVisible=false;
                this.formData.keyfield=undefined;
            }
        },
        onClickDeleteListFields(row){
            for(let i in this.listFields){
                if(this.listFields[i].id==row.id){
                    this.$delete(this.listFields, i);
                    let layout = this.findLayout()
                    layout.fields=Object.assign([],this.listFields);
                    updateTrackerLayout(this.tracker.id,layout).then(resp => {
                        this.$message.success("操作成功") 
                        this.$emit("refresh",this.listFields);
                    })
                    break;
                }
            }
        },
        onCancel(){
            this.$emit("cancel");
        },
        findLayout() {
            let layout;
            if (this.tracker && this.tracker.trackerLayouts && this.tracker.trackerLayouts.length > 0) {
                layout = this.tracker.trackerLayouts.filter(lay => { return lay.name === "LIST" })[0]
            }
            if(!layout){
                layout = {name:'LIST',keyFields:[],fields:[],sections:[]}
                this.tracker.trackerLayouts.push(layout)
            }
            return layout;
        },
        findFields(fieldId) {
            if (this.tracker && this.tracker.trackerFields && this.tracker.trackerFields.length > 0) {
                return this.tracker.trackerFields.filter(f => { return f.id === fieldId })[0]
            }
            return null;
        },
        rowDrop(visiable) {
            // console.log("aaa",this.listFields)
            // if (visiable) {
                let that = this
                this.$nextTick(() => {
                    let xTable = that.$refs.orderByTable;
                    if (xTable) {
                        that.sortable = Sortable.create(
                            xTable,
                            {
                                handle: ".order-row",
                                animation: 150,
                                filter: '.order-row-bottom',
                                onEnd: ({ newIndex, oldIndex }) => {
                                    that.listFields.splice(newIndex, 0, this.listFields.splice(oldIndex, 1)[0]);
                                    var newArray = this.listFields.slice(0);
                                    that.listFields = [];
                                    that.$nextTick(function () {
                                        that.listFields = newArray;  
                                        if(newIndex!=oldIndex){
                                            let layout = this.findLayout()
                                            layout.fields=Object.assign([],this.listFields);
                                            updateTrackerLayout(this.tracker.id,layout).then(resp => {
                                                this.$message.success("操作成功") 
                                                this.$emit("refresh",this.listFields);
                                            })
                                        }
                                        
                                    });
                                },
                            }
                        );
                    }
                });
            // }
        },
    },
};
</script>
<style lang="less" scoped>
.order-row {
    width: 100%;
    cursor: move;

    .icon-circle {
        font-size: 18px;
        display: flex;
        align-items: center;
        justify-content: center;
        width: 22px;
        height: 22px;
        // border: solid 1px currentColor;
        border-radius: 50%;
        cursor: pointer;
    }

    .icon-circle .disabled {
        font-size: 18px;
        opacity: .5;
        color: #909090;
        cursor: not-allowed;
        background-color: #e8e8e8;
        border-color: #979797;
    }
}
.system-tag-box{
  height: 18px;
  line-height: 15px;
  border-radius: 10px;
  color: #909090;
  background: #0000;
}

.ui-table {
    border: solid 1px #dedede;
    border-radius: 3px;
    background-color: #fff;
    overflow: hidden;
    display: flex;
    flex-direction: column;
}
.layout-fields-table-row{
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    min-height: 48px;
    padding-right: 20px;
    padding-left: 20px;
    padding-top: 10px;
    padding-bottom: 10px;
}
.table-head {
    width: 100%;
    padding: 0 20px;
    font-size: 14px;
    color: #303030;
    font-weight: 500;
    line-height: 48px;
    background-color: #f8f8f8;
    border-bottom: solid 1px #dedede;
    box-sizing: border-box;
}
.table-row {
    width: 100%;
    padding: 0 20px;
    height: 48px;
    line-height: 48px;
    border-bottom: solid 1px #dedede;
    font-size: 14px;
    color: #303030;
    box-sizing: border-box;
}
.transition-status {
    float: right;
    display: flex;
    align-items: center;
    margin-right: 20px;
    white-space: nowrap;

    .ui-tag-status {
        max-width: 110px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        font-size: 12px;
        box-sizing: border-box;
        display: inline-block;
        height: 20px;
        line-height: 18px;
        transition: border-color .2s;
        border: 1px solid;
        border-radius: 20px;
        padding: 0px 8px;
    }
}
.preview-content{
    width: 100%;
    padding: 20px;
    background: #f8f8f8;
    display:flex;
    flex-direction: column;
    .title-box{
        width: 100%;
        margin-bottom: 10px;
        display:flex;
        justify-content:space-between;
    }
    .content-wrapper{
        display:flex;
        flex-direction: row;
        margin-top:10px;
    }
    .tag-box{
        cursor: pointer;
        border:none;
        background-color: rgba(144,144,144,.15);
    }
}
</style>
