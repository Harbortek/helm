<template>
    <a-modal v-model="visiable" title="查看工作项差异" @cancel="onCancel" width="900px" centered>
        <template slot="footer">
            <a-button key="close" type="primary" @click="onCancel">
                关闭
            </a-button>
        </template>
        <vxe-grid class="reverse-table" v-bind="gridOptions1" :loading="loading">
            <template #left_default="{ row }">
                <span v-if="row.item=='描述'" v-html="row.left" :style="{color:cellStyle(row,'left')}"></span>
                <span v-else :style="{color:cellStyle(row,'left')}">{{ row.left }}</span>
            </template>
            <template #right_default="{ row }">
                <span v-if="row.item=='描述'" v-html="row.left" :style="{color:cellStyle(row,'right')}"></span>
                <span v-else :style="{color:cellStyle(row,'right')}">{{ row.right }}</span>
            </template>
        </vxe-grid>

    </a-modal>
</template>

<script>
import { findItemsHistory } from '@/services/baseline/BaselineService'
import { findOneTracker} from "@/services/tracker/TrackerService";
export default {
    name: 'TrackerItemDiffDialog',
    components: {},
    props: {
        isShowDialog: {
            required: true
        },
        currentCompare: {
            required: true
        }
    },
    data() {
        return {
            loading:false,
            tableData:[],
            tracker:{},
            gridOptions1: {
                border: true,
                showHeader: false,
                columns: [
                    { field: 'item', width: 100 },
                    { field: 'left',slots: { default: 'left_default' } },
                    { field: 'right',slots: { default: 'right_default' }  },
                ],
                data: [
                ]
            },
        }
    },
    watch: {
        isShowDialog: {
            handler: async function (newVal, oldVal) {
                if(newVal){
                    this.loading=true;
                    if(this.currentCompare?.parentId){
                       await findOneTracker(this.currentCompare.parentId).then(resp=>{
                            this.tracker=resp
                            console.log("tracker",resp)
                        })
                    }
                    let historyIds=[]
                    if(this.currentCompare.left?.id){
                        historyIds.push(this.currentCompare.left.historyId)
                    }
                    if(this.currentCompare.right?.id){
                        historyIds.push(this.currentCompare.right.historyId)
                    }
                    let leftData=[],rightData=[]
                    findItemsHistory(historyIds).then(res=>{
                        if(res){
                            let filter=['historyId','id','deleted',"revision","itemNo","name","description","statusId","values","watchers"]
                            filter.push(...["relatedWorkItems","icon","relatedWikis","meaningId","attachments","createBy","createDate"])
                            filter.push(...["project","owner","hyperlinks","testSteps","priority","assignTo","sprint","status","tracker","lastModifiedBy"])
                            console.log("res",res)
                            res.forEach(element => {
                                if(element.revision==this.currentCompare.left?.revision){
                                    leftData.push({'left':element?.revision,'item':'版本'})
                                    leftData.push({'left':element?.itemNo,'item':'编号'})
                                    leftData.push({'left':element?.name,'item':'标题'})
                                    leftData.push({'left':element?.description,'item':'描述'})
                                    leftData.push({'left':element?.priority?.name,'item':'优先级'})
                                    leftData.push({'left':element?.owner?.name,'item':'负责人'})
                                    leftData.push({'left':element?.assignedTo?.name,'item':'分配给'})
                                    leftData.push({'left':element?.sprint?.name,'item':'所属迭代'})
                                    leftData.push({'left':element?.lastModifiedBy?.name,'item':'修改者'})
                                    leftData.push({'left':this.tracker.trackerStatuses.find(v=>v.id==element?.statusId)?.name,'item':'状态'})
                                    // values //watchers relatedWorkItems //attachments
                                    for(let item in element){
                                        if(filter.indexOf(item)==-1){
                                            let value=this.tracker.trackerFields.find(v=>v.systemProperty==item)?.name||item
                                            if(typeof element[item]!="object"){
                                                leftData.push({'left':element[item],'item':value})
                                            }else{
                                                leftData.push({'left':element[item]?.name,'item':value})
                                            }
                                        }
                                    }
                                }else{
                                    rightData.push({'right':element?.revision,'item':'版本'})
                                    rightData.push({'right':element?.itemNo,'item':'编号'})
                                    rightData.push({'right':element?.name,'item':'标题'})
                                    rightData.push({'right':element?.description,'item':'描述'})
                                    rightData.push({'right':element?.priority?.name,'item':'优先级'})
                                    rightData.push({'right':element?.owner?.name,'item':'负责人'})
                                    rightData.push({'right':element?.assignedTo?.name,'item':'分配给'})
                                    rightData.push({'right':element?.sprint?.name,'item':'所属迭代'})
                                    rightData.push({'right':element?.lastModifiedBy?.name,'item':'修改者'})
                                    rightData.push({'right':this.tracker.trackerStatuses.find(v=>v.id==element?.statusId)?.name,'item':'状态'})
                                    for(let item in element){
                                        if(filter.indexOf(item)==-1){
                                            let value=this.tracker.trackerFields.find(v=>v.systemProperty==item)?.name||item
                                            if(typeof element[item]!="object"){
                                                rightData.push({'right':element[item],'item':value})
                                            }else{
                                                rightData.push({'right':element[item]?.name,'item':value})
                                            }
                                        }
                                    }
                                }
                            });
                        }else{
                            console.log("res is null")
                        }
                        if(leftData.length!=0&&leftData.length>=rightData.length){
                            this.tableData = leftData.map(v=>{
                                return {
                                    ...v,
                                    ...rightData.find(item=>item.item==v.item)
                                }
                            })
                        }else{
                            this.tableData = rightData.map(v=>{
                                return {
                                    ...v,
                                    ...leftData.find(item=>item.item==v.item)
                                }
                            })
                        }
                        this.tableData=this.tableData.filter(v=>v.left||v.right)
                        
                        this.tableData.unshift({item:'',left:'左侧',right:'右侧'})
                        this.gridOptions1.data = this.tableData
                        console.log("data",this.tableData)
                    }).finally(()=>{
                        this.loading=false
                    })
                }
            }
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
        projectId() {
            return this.$route.params.projectId
        },
        leftRevision() {
            return parseInt(this.currentCompare.left?.revision)>parseInt(this.currentCompare.right?.revision)
        },
    },
    mounted() {

    },
    methods: {
        onCancel() {
            this.$emit('cancel')
        },
        cellStyle(row,column){
            if(row.left==row.right||row.left=='左侧'){
                return;
            }
            if(!row[column=='left'?'right':'left']){
                return '#189FFF'
            }else if(this.leftRevision&&column=='left'){
                return '#f5222f'
            }else if(!this.leftRevision&&column=='right'){
                return '#f5222f'
            }else{
                return '#908b8b'
            }
            
        },
    },
}
</script>

<style lang="less" scoped>
.reverse-table /deep/.vxe-body--row .vxe-body--column:first-child {
    background-color: #f8f8f9;
}

</style>