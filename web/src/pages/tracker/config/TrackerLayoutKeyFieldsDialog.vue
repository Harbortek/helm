<template>  
    <a-modal :visible="isShowDialog"  width="1000px"  title="属性配置"  @cancel="onCancel">
        <template #footer>
            <a-button @click="onCancel">关闭</a-button>
        </template>
        <div class="page-description page-description-info manager-desc">
            <div class="page-description-border"></div>
            <div class="page-description-container">
            <p class="page-description-p style--font-14">
                定义该工作项类型下的工作项在详情里关键属性卡片显示哪些属性信息。最多可以添加 5 个属性。
            </p>
            </div>
        </div>
        <div style="height:45px;margin-top:10px;">
            <a-button style="float:right" type="primary" @click="onClickAddKeyFields">添加关键属性</a-button>
        </div>
        <a-card style="border:none" :bodyStyle="{padding:'24px 0'}" trigger="['click']]" placement="bottom" @mouseenter="rowDrop">
            <div class="ui-table">
                <div class="table-head layout-fields-table-row order-row-bottom">
                    <div style="">关键属性</div>
                    <div class="">操作</div>
                </div>
                <div ref="orderByTable">
                    <div class="order-row" v-for="item in keyFields" :key="item.id">
                            <div class="layout-fields-table-row table-row"  >   
                                <div style="padding-left: 0;width: 235px;" >
                                    <i class="vxe-icon--menu"></i>
                                    {{item.name}}
                                    <a-tag v-if="item.system" class="tag-box">系统</a-tag>
                                </div>
                                <vxe-button style="font-size: 14px;" title="移除" type="text" icon="vxe-icon-close"
                                    @click="onClickDeleteKeyFields(item)"></vxe-button>
                            </div>
                    </div>
                </div>
            </div>
        </a-card>

        <div class="preview-content">
            <div class="title-box">
                <span style="color: #909090;">预览效果</span>
            </div>
            <a-card style="border:none;padding-left:20px;" :bodyStyle="{padding:'15px 0'}">
            <span class="task-basic-title" >这是一个工作项实例</span>
            <div class="content-wrapper">
                <div v-for="item in keyFields" :key="item.id">
                    <quick-picker v-if="item.name=='状态'" title="未开始" :sub-title="'当前状态'">
                        <template slot="icon">
                            <a-avatar icon="arrow-right"
                                :style="{ color: '#fff', backgroundColor: 'rgb(51, 143, 229)' }" />
                        </template>
                    </quick-picker>
                    <quick-picker v-else-if="item.name=='负责人'" title="负责人" :sub-title="'负责人'"
                        icon-text="item.owner.name">
                        <template slot="icon">
                            <a-avatar class="avatar" size="default" style="backgroundColor:rgb(44,178,174)">负责人</a-avatar>
                        </template>
                    </quick-picker>
                    <quick-picker v-else-if="item.name=='优先级'" title="普通" :sub-title="'优先级'">
                        <template slot="icon">
                            <a-avatar :style="{ color: '#fff', backgroundColor: 'rgb(51, 143, 229)' }">
                                <a-icon slot="icon" component="priority" />
                            </a-avatar>
                        </template>
                    </quick-picker>
                    <quick-picker v-else title="未设置" :sub-title="item.name">
                        <template slot="icon">
                            <a-avatar :style="{ color: '#606060', backgroundColor: '#e8e8e8' }">
                                <a-icon type="book" />
                            </a-avatar>
                        </template>
                    </quick-picker>
                </div>
            </div>
            </a-card>
        </div>  
        <a-modal :visible="addKeyFieldsVisible" centered width="520px"  title="添加关键属性" @ok="onOKAddKeyFields"  @cancel="addKeyFieldsVisible=false">
            <a-form-model :model="formData">
                <a-form-model-item prop="keyfield">
                    <a-select placeholder="选择关键属性" style="width: 460px;" show-search optionFilterProp='children' v-model="formData.keyfield">
                        <a-select-option v-for="item in addKeyFields" :key="item.id" :value="item.id">{{item.name}}</a-select-option>   
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
    name: "TrackerLayoutKeyFieldsDialog",
    components: { ConfigPage, Sortable, QuickPicker },
    props: {
        isShowDialog: {
            required: true
        },
        customerFields: {
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
            addKeyFieldsVisible: false,
            keyFields:[],
        };
    },
    computed: {
        addKeyFields(){
            let fields = this.tracker.trackerFields || []
            fields = fields.filter(f => {
                return f.inputType === 'INTEGER' || f.inputType === 'TEXT' || f.inputType === 'STATUS'
                    || f.inputType === 'DATE' || f.inputType === 'USER' || f.inputType === 'OPTIONS'
            })
            console.log("fields",fields)
            fields = fields.filter(f => {
                return f.systemProperty !== 'name' && f.systemProperty !== 'createBy' 
                    && f.systemProperty !== 'createDate' && f.systemProperty !== 'lastModifiedBy' && f.systemProperty !== 'lastModifiedDate' 
                    && f.systemProperty !== 'assignedDate' && f.systemProperty !== 'planStartDate' 
                    && f.systemProperty !== 'planEndDate' && f.systemProperty !== 'closeDate'
            })

            let ids=this.keyFields.map(f => f.id);
            ids=[...ids,...this.customerFields.map(f => f.id)] 
            let result = fields.filter(f => { return ids.indexOf(f.id) < 0 })
            return result
        },
    },
    mounted() {

    },
    watch: {
        'isShowDialog': function (value) {
            if(value){
                this.initData();
            }
            
        }
  },
    methods: {
        initData(){
            
            let layout = this.findLayout()
            //keyFields
            if (layout) {
                const fields = layout.keyFields
                this.keyFields=[];
                fields.forEach(f => {
                    let field = this.findFields(f.id)
                    if (field) {
                        this.keyFields.push(field)
                    }
                })
            }
        },
        onClickAddKeyFields(){
            if(this.keyFields.length<5){
                this.addKeyFieldsVisible=true;
            }else{
                this.$message.warn('关键属性不能超过5个');
            }
        },
        onOKAddKeyFields(){
            console.log("oooonk",this.formData.keyfield)
            if(this.formData.keyfield){
                let field = this.findFields(this.formData.keyfield)
                if (field) {
                    this.keyFields.push(field)
                    let layout = this.findLayout()
                    console.log("ooook",layout)
                    layout.keyFields=Object.assign([],this.keyFields);
                    updateTrackerLayout(this.tracker.id,layout).then(resp => {
                        this.$message.success("操作成功")   
                        this.$emit("refresh",this.keyFields);
                    })
                }
                this.addKeyFieldsVisible=false;
                this.formData.keyfield=undefined;
            }
        },
        onClickDeleteKeyFields(row){
            for(let i in this.keyFields){
                if(this.keyFields[i].id==row.id){
                    this.$delete(this.keyFields, i);
                    let layout = this.findLayout()
                    layout.keyFields=Object.assign([],this.keyFields);
                    updateTrackerLayout(this.tracker.id,layout).then(resp => {
                        this.$message.success("操作成功") 
                        this.$emit("refresh",this.keyFields);
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
                layout = this.tracker.trackerLayouts.filter(lay => { return lay.name === "DETAIL" })[0]
            }
            if(!layout){
                layout = {name:"DETAIL",keyFields:[],fields:[],sections:[]}
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
            // console.log("aaa",this.keyFields)
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
                                    that.keyFields.splice(newIndex, 0, this.keyFields.splice(oldIndex, 1)[0]);
                                    var newArray = this.keyFields.slice(0);
                                    that.keyFields = [];
                                    that.$nextTick(function () {
                                        that.keyFields = newArray;  
                                        if(newIndex!=oldIndex){
                                            let layout = this.findLayout()
                                            layout.keyFields=Object.assign([],this.keyFields);
                                            updateTrackerLayout(this.tracker.id,layout).then(resp => {
                                                this.$message.success("操作成功") 
                                                this.$emit("refresh",this.keyFields);
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
.tag-box{
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
.task-basic-title {
    position: relative;
    border: none;
    resize: none;
    outline: none;
    box-shadow: none;
    font-size: 18px;
    color: #303030;
    font-weight: 500;
    line-height: 24px;
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
}

.page-description {
  display: flex;
  margin-top: 20px;
  line-height: 1.5;
  box-sizing: border-box;
  border-bottom: 1px solid #e8e8e8;
  border-radius: 0;
  border-right: 1px solid #e8e8e8;
  border-top: 1px solid #e8e8e8;
}

.page-description-border {
  border-radius: 0;
  flex: none;
  width: 4px;
}

.page-description-info .page-description-border {
  background: #338fe5;
}

.page-description-container {
  background: #fff;
  display: flex;
  flex: auto;
  padding: 10px;
}

.page-description-p {
  margin: 0 0 0 0;
  padding: 0;
  white-space: pre-wrap;
}

.style--font-14 {
  font-size: 14px;
  line-height: 22px;
}
</style>
