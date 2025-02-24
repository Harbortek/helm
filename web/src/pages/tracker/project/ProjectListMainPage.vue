<template>
    <content-page>
        <div style="height:100%">
            <a-tabs :activeKey="activeTab" @change="onTabChange" style="height: 45px;">
                <a-tab-pane v-for="item in views" :key="item.id" :tab="item.name">
                </a-tab-pane>
                <!-- <a-tab-pane key="ALL" tab="所有项目">
                    <project-list status="ALL"/>
                </a-tab-pane>
                <a-tab-pane key="NOT_STARTED" tab="未开始">
                    <project-list status="NOT_STARTED" />
                </a-tab-pane>
                <a-tab-pane key="ONGOING" tab="进行中">
                    <project-list status="ONGOING"/>
                </a-tab-pane>
                <a-tab-pane key="ENDED" tab="已完成">
                    <project-list status="ENDED"/>
                </a-tab-pane>
                <a-tab-pane key="CLOSED" tab="已关闭">
                    <project-list status="CLOSED"/>
                </a-tab-pane> -->
                <template #tabBarExtraContent>
                    <a-space>
                        <vxe-button icon="vxe-icon-setting" type="text" @click="onViewConfig" v-action="'PROJECT_LIST'">视图管理</vxe-button>
                    </a-space>
                </template>
            </a-tabs>
            <!-- :status="transformStatus(views.find(item=>item.id==activeTab)?.name)" -->
            <project-list style="height:calc(100% - 45px)" :views="views" :viewId="activeTab"/>
        </div>
        <view-list-dialog :is-show-dialog="isShowViewConfig" :object-id="objectId" @close="colse" />
    </content-page>
</template>
<script>

import ContentPage from '../../../components/page/content/ContentPage.vue';
import ProjectList from './ProjectList.vue'
import ViewListDialog from '../items/ViewListDialog.vue';
import { findViewsByObjectId, findOneView } from "@/services/tracker/ViewService"


export default {
    name: "ProjectListMainPage",
    components: { ContentPage,ProjectList,ViewListDialog },
    data() {
        return {
            activeTab: 'ALL',
            isShowViewConfig: false,
            userId:'',
            objectId: "1",
            views: [],
            currentView: {
                viewConfig: {
                    orderBys: []
                }
            },
        };
    },
    mounted() {
        this.userId = this.$store.getters['account/user'].id;
        this.loadData();
    },
    methods: {
        transformStatus(name){
            if(name=="未开始") return "NOT_STARTED";
            else if(name=="进行中") return "ONGOING"
            else if(name=="已完成") return "ENDED"
            else if(name=="已关闭") return "CLOSED"
            else return ''
        },
        onTabChange(activeKey) {
            this.activeTab = activeKey
        },
        onViewConfig: function () {
            this.isShowViewConfig = true
        },
        loadData: function () {
            findViewsByObjectId(this.objectId,this.userId,true).then(resp => {
                this.views = resp
                if (this.views.length > 0) {
                    this.activeTab = this.views[0].id
                    // findOneView(this.activeTab).then(resp => {
                    //     this.currentView = resp
                    // })
                }
            })
        },
        colse(isChange){
            this.isShowViewConfig = false
            if(isChange){
                this.loadData();
            }
        },
    },
};
</script>
<style lang="less" scoped>
:deep(.content-page .ant-tabs-bar) {
    margin-bottom: 1px;
}

:deep(.content-page .ant-tabs-content) {
    height: 100%;
}

.card-layout {
    padding: 10px;
    display: flex;
    align-item: flex-start;
    flex-wrap: wrap;

    .card-project-title {
        display: flex;
        align-items: center;
        /*垂直居中*/
        font-size: 18px;
        height: 60px;
        line-height: 18px;
        vertical-align: middle;
    }

    .card-project-desciption {
        color: #808080;
    }

    .empty-data {
        width:100%;
        height:300px;
        box-sizing: border-box;
        margin: 0;
        padding: 0;
        color: rgba(0, 0, 0, .65);
        font-size: 14px;
        font-variant: tabular-nums;
        line-height: 1.5;
        list-style: none;
        font-feature-settings: "tnum";
        position: relative;
    }
}
</style>
