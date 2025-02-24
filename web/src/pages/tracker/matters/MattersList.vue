<template>
    <content-page>
        <div style="height: 100%;">
            <a-tabs style="height:45px;" :active-key="currentTab" @change="onChangeTab">
                <a-tab-pane :key="view.id" :tab="view.name" v-for="view in views">
                </a-tab-pane>

                <template #tabBarExtraContent>
                    <a-space>
                        <vxe-button v-action="'PAGE_WRITE|'+pageId" icon="vxe-icon-setting" type="text" @click="onViewConfig">视图管理</vxe-button>
                    </a-space>
                </template>
            </a-tabs>
            <tracker-items-table style="height: 100%;padding-bottom: 45px;" isMatters="true" :pageId="pageId" :projectId="projectId" :views="views" :viewId="currentTab" @refresh="loadData"></tracker-items-table>
        </div>
        <view-list-dialog :is-show-dialog="isShowViewConfig" :object-id="pageId" :project-id="projectId" @close="onViewCancel" />

    </content-page>
</template>
<script>
import Vue from "vue"
import { findViewsByObjectId, findOneView } from "@/services/tracker/ViewService"
import TrackerItemsTable from '@/components/table/TrackerItemsTable.vue';
import ViewListDialog from '@/pages/tracker/items/ViewListDialog.vue';
import ContentPage from '@/components/page/content/ContentPage.vue';


export default {
    name: "MattersList",
    components: {TrackerItemsTable,ViewListDialog,ContentPage},
    props: {
        
    },
    data() {
        return {
            loading: false,
            projectId:'',
            pageId: '',
            keyword: '',
            views: [],
            currentTab: '',
            isShowViewConfig: false,
        };
    },
    computed: {
       
    },
    watch: {
        pageId: {
            handler: function (newVal, oldVal) {
                this.loadData(); 
            }
        },
    },
    created(){
        this.userId = this.$store.getters['account/user'].id;
        this.projectId=this.$route.params.projectId;
        this.pageId=this.$route.params.pageId;
    },
    mounted() {
    },
    methods: {
        loadData: function (currentView) {
            this.loading=true;
            findViewsByObjectId(this.pageId,this.userId,true,true).then(resp => {
                this.views = resp
                console.log("this.views",this.views)
                if (this.views.length > 0) {
                    if(currentView){
                        this.currentTab = currentView.id
                    }else{
                        if(this.views.length >1){
                            this.currentTab = this.views[1].id
                        }else{
                            this.currentTab = this.views[0].id
                        }
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
:deep(.content-page .ant-tabs-bar) {
    margin-bottom: 1px;
}
</style>
