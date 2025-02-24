<template>
    <div @click.stop>
        <div class="tracker-select-option" v-for="p in stateTransitions" :key="p.id">
            <div class="option-item" @click="onExecuteStep(p)">
                <div class="option-item-content">
                    <span class="option-item-text">执行步骤： {{ p.name }}</span>
                </div>
                <div class="option-item-icon ">
                </div>
            </div>
        </div>
        <div class="tracker-select-option">
            <div class="option-item" @click="onClickFlowSheetShow">
                <div class="option-item-content">
                    <i class="vxe-icon-flow-branch"
                        style="margin-top: 3px;margin-right: 5px;"></i>
                    <span class="option-item-text">查看状态流程图</span>
                </div>
                <div class="option-item-icon ">
                </div>
            </div>
        </div>
        <div class="tracker-select-option">
            <div class="option-item" @click="onStateTransitionPage">
                <div class="option-item-content">
                    <a-icon type="setting" style="margin-top: 3px;margin-right: 5px;" />
                    <span class="option-item-text"> 配置工作流</span>
                </div>
                <div class="option-item-icon ">
                </div>
            </div>
        </div>
        <a-modal v-if="flowSheetVisible" :visible="flowSheetVisible" centered width="90vw" title="状态流程图" @cancel="onCancel">
            <template slot="footer">
                <a></a>
            </template>
            <a-row :gutter="[24,16]">
                <div id="container" style="height:calc(100vh - 300px);">

                </div>
            </a-row>
        </a-modal>
    </div>
    
</template>

<script>
import {stateChange} from "@/services/tracker/TrackerItemService"
import {
    findTrackerStateTransitions,findTrackerStatus
} from "@/services/tracker/TrackerService";
import { Graph, Vector } from "@antv/x6";
import { DagreLayout } from "@antv/layout";

export default {
    name: 'TrackerItemStatusPopover',
    components:{
        Graph
    },
    props: {
        projectId:{
            required: true
        },
        trackerItem: {
            required: true
        },
        tracker: {
            required: false
        }
    },
    computed:{
        stateTransitions() {
            const currentStatusId = this.trackerItem?.status?.id
            return this.trackerStateTransitions?.filter(s => s.transitionFrom.id === currentStatusId)
        },
    },
    data() {
        return {
            statePopoverVisible: false,
            flowSheetVisible: false,
            nodeInfo: {
                nodes: [],
                edges: [],
            },
            trackerStateTransitions:[],
            trackerStatuses:[],
            trackerId:'',
        }
    },
    mounted() {
        this.trackerId=this.trackerItem?.tracker?.id;
        this.loadData();    
    },
    methods:{
        onCancel(){
            this.flowSheetVisible=false
            this.graph=null;
        },
        loadData() {
            if(this.tracker?.trackerStateTransitions){
                this.trackerStateTransitions=Object.assign([],this.tracker.trackerStateTransitions)
            }else{
                findTrackerStateTransitions(this.trackerId).then(res=>{
                    this.trackerStateTransitions=res
                })
            }

            if(this.tracker?.trackerStatuses){
                this.trackerStatuses=Object.assign([],this.tracker.trackerStatuses)
            }else{
                findTrackerStatus(this.trackerId).then(res=>{
                    this.trackerStatuses=res
                })
            }
        },
        onExecuteStep(stateTransition) {
            stateChange(this.trackerItem.id, stateTransition.id).then(resp => {
                this.$message.success("更新成功")
                this.trackerItem.status=resp;
                this.refresh()
                this.$emit("change")
            })
            this.statePopoverVisible=false
        },
        refresh(){
            this.$emit("refresh");
        },
        onClickFlowSheetShow() {
            this.statePopoverVisible = false
            this.flowSheetVisible = true
            this.buildGraphData();
        },
        onStateTransitionPage() {
            this.$router.push({
                name: 'trackerStateTransitionConfig', params: {
                    trackerId: this.trackerId
                }
            })
        },
        buildGraphData: function () {
            let i = 0
            for (i = 0; i < this.trackerStatuses.length; i++) {
                const status = this.trackerStatuses[i]
                this.nodeInfo.nodes.push({
                    id: status.id,
                    label: status.name
                })
            }
            for (i = 0; i < this.trackerStateTransitions.length; i++) {
                const t = this.trackerStateTransitions[i]
                this.nodeInfo.edges.push({
                    source: t.transitionFrom.id,
                    target: t.transitionTo.id,
                    lineType: 'solid',
                    // label: t.name
                })
            }
            let that = this
            this.$nextTick(function () {
                if (that.graph == null) {
                    that.flowSheetInit()
                }
                that.graph.centerContent(); // 将画布中元素居中展示
            })
        },
        flowSheetInit: function () {
            //创建一个 Graph 对象
            this.graph = new Graph({
                container: document.getElementById("container"),
                width: '100%', //画布容器宽度
                height: '100vh-300px', //画布容器高度
                mousewheel: true, //滚轮缩放
                panning: true //支持平移拖拽
            });
            //定义层次布局Dagre
            const dagreLayout = new DagreLayout({
                type: "dagre", //布局类型
                rankdir: "LR", //布局的方向。T：top（上）；B：bottom（下）；L：left（左）；R：right（右）
                align: "UL", //节点对齐方式。U：upper（上）；D：down（下）；L：left（左）；R：right（右）；undefined (居中)
                ranksep: 50, //层间距（px）。在 rankdir 为 TB 或 BT 时是竖直方向相邻层间距；在 rankdir 为 LR 或 RL 时代表水平方向相邻层间距
                nodesep: 200, //节点间距（px）。在 rankdir 为 TB 或 BT 时是节点的水平间距；在 rankdir 为 LR 或 RL 时代表节点的竖直方向间距
                controlPoints: true, //是否保留布局连线的控制点
            });
            //分别动态添加节点和边的样式
            this.nodeInfo.nodes.map((item) => {
                Object.assign(item, { width: 130, height: 40 });
            });
            this.nodeInfo.edges.map((item) => {
                if (item.lineType === "solid") {
                    Object.assign(item, {
                        //路由将边的路径点 vertices 做进一步转换处理
                        router: {
                            name: "manhattan", //智能正交路由
                            args: {
                                startDirections: ["right"], // 支持从哪些方向开始路由
                                endDirections: ["left"], // 支持从哪些方向结束路由
                            },
                        },
                        //定义实线样式
                        attrs: {
                            line: { stroke: "#0e639c" }, // line 指代的元素代表了边的主体
                        },
                    });
                } else if (item.lineType === "dotted") {
                    Object.assign(item, {
                        router: {
                            name: "manhattan", //智能正交路由，由水平或垂直的正交线段组成，并自动避开路径上的其他节点（障碍)
                            args: {
                                startDirections: ["bottom"], // 支持从哪些方向开始路由
                                endDirections: ["left"], // 支持从哪些方向结束路由
                            },
                        },
                        //定义虚线样式
                        attrs: {
                            line: { strokeDasharray: "5 5", stroke: "#51ff51" }, // line 指代的 元素代表了边的主体
                        },
                    });
                }
            });
            //判断是否需要dagre布局(通过控制dagreLayout字段)
            if (true) {
                //应用dagre布局
                const model = dagreLayout.layout(this.nodeInfo);
                //渲染画布
                this.graph.fromJSON(model);
            } else {
                this.graph.fromJSON(this.nodeInfo);
            }
        },//
    },
}
</script>

<style lang="less" scoped>
.tracker-select-option {
    font-size: 12px;
    cursor: pointer;
    display: block;
    font-size: 14px;
    font-weight: 400;
    line-height: 22px;
    min-height: 34px;
    min-width: 100px;
    padding: 6px 10px;
    position: relative;
    color: #000;

    &:hover {
        background-color: #f8f8f8;
        color: #338fe5;
    }

    .option-item {
        display: flex;

        .option-item-content {
            flex: auto;
            display: inline-flex;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
        }

        .option-item-icon {
            flex: none;
            display: inline-flex;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
            padding-top: 5px;
            margin-left: 10px;
        }


        &>a {
            color: inherit;
        }

        &>a:hover {
            color: inherit;
        }

        .option-item-text {}

        .option-item-subtext {
            margin-left: 5px;
            font-size: 12px;
            color: #606060;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
            max-width: 180px;
            flex: 0 0 auto;
            color: var(--gray-80);
        }
    }
}
</style>
