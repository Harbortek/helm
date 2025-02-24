<template>
    <content-page>
        <div style="height: 100%;">
            <a-tabs style="height:45px;" :active-key="currentTab" @change="onChangeTab">
                <a-tab-pane :key="view.id" :tab="view.name" v-for="view in views">
                </a-tab-pane>

                <template #tabBarExtraContent>
                    <a-space>
                        <vxe-button icon="vxe-icon-setting" type="text" @click="onViewConfig">视图管理</vxe-button>
                    </a-space>
                </template>
            </a-tabs>
            <tracker-items-table style="height: calc( 100% - 45px )" :projectId="projectId" :pageId="pageId" :trackerId="trackerId" :views="views" :viewId="currentTab" @refresh="loadData"></tracker-items-table>
        </div>
        <view-list-dialog :is-show-dialog="isShowViewConfig" :object-id="trackerId||pageId" :project-id="projectId" @close="onViewCancel" />

    </content-page>
</template>
<script>
import Vue from "vue"
import { findViewsByObjectId, findOneView } from "@/services/tracker/ViewService"
import TrackerItemsTable from '@/components/table/TrackerItemsTable.vue';
import ViewListDialog from './ViewListDialog.vue';
import ContentPage from '@/components/page/content/ContentPage.vue';



export default {
    name: "TrackerItemsMainPage",
    components: {TrackerItemsTable,ViewListDialog,ContentPage},
    props: {
        projectId: {
            required: true,
        },
        trackerId: {
            required: false,
        },
        pageId: {
            required: false,
        },
        
    },
    data() {
        return {
            loading: false,
            keyword: '',
            views: [],
            currentTab: '',
            isShowViewConfig: false,
        };
    },
    computed: {
       
    },
    watch: {
        trackerId: {
            handler: function (newVal, oldVal) {
                this.loadData(); 
            }
        },
        pageId: {
            handler: function (newVal, oldVal) {
                this.loadData(); 
            }
        },
    },
    mounted() {
        this.userId = this.$store.getters['account/user'].id;
        this.loadData();
    },
    methods: {
        loadData: function (currentView) {
            this.loading=true;
            findViewsByObjectId(this.trackerId||this.pageId,this.userId,true,false).then(resp => {
                this.views = resp
                if (this.views.length > 0) {
                    if(currentView){
                        this.currentTab = currentView.id
                    }else{
                        this.currentTab = this.views[0].id
                    }
                }
            }).finally(()=>{
                this.loading=false;
            })
        },
        onViewConfig: function () {
            this.isShowViewConfig = true
        },
        onChangeTab: function (activeKey) {
            this.currentTab = activeKey
        },
        onViewCancel: function(isChange){
            this.isShowViewConfig = false
            if(isChange){
                console.log("isChange",isChange)
                this.loadData();
            }
        },
    },
    beforeDestroy() {
        if (this.sortable) {
            this.sortable.destroy();
        }
    },
};
</script>
<style lang="less" scoped>
// :global(.content-page .ant-tabs-bar) {
//     margin-bottom: 1px;
// }
</style>
