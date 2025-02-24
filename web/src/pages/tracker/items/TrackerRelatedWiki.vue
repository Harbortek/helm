<template>
    <div style="height:100%" :class="!isToolBar ? 'task-detail-module' : ''">
        <!-- <vxe-button type="text" style="float:right;z-index: 1;" 
            @click="onSelectTrackerWiki" icon="vxe-icon-paste">关联Wiki文档</vxe-button> -->
        <a-form-model-item label="关联文档" prop="relatedWorkItems" :span="24">
            <vxe-table ref="optionsTable" :show-header="false" :data="tableData" :row-config="{ isHover: true }" stripe>
                <vxe-column type="seq" width="60" />
                <vxe-column field="name" title="">

                </vxe-column>

                <vxe-column field="" title="操作" header-align="center" width="100">
                    <template #default="{ row }">
                        <!-- <vxe-button type="text" icon="vxe-icon-delete"
                            @click="onDeleteWiki(row)"></vxe-button> -->
                        <a-tooltip :overlayStyle="{ fontSize: '10px' }">
                            <template slot="title">
                                新窗口打开
                            </template>
                            <vxe-button type="text" icon="vxe-icon-zoom-in" @click="onEditWiki(row)"></vxe-button>
                        </a-tooltip>
                    </template>
                </vxe-column>
            </vxe-table>
        </a-form-model-item>
        <tracker-wiki-select-modal :projectId="projectId" :initalData="tableData" ref="trackerWikiSlectModal"
            @trackerWikiSelector="trackerWikiSelector"></tracker-wiki-select-modal>
    </div>
</template>

<script>
import { relatedWikis } from "@/services/tracker/TrackerItemService"
import VXETable from "vxe-table";
import {
    findByProjectId,findByPageIds
} from "@/services/tracker/ProjectPageService";
import TrackerWikiSelectModal from '@/components/dialog/TrackerWikiSelectModal'

export default {
    name: 'TrackerRelatedWiki',
    components: { TrackerWikiSelectModal },
    props: {
        trackerItem: {
            required: true
        },
        projectId: String,
        isToolBar: Boolean
    },
    data() {
        return {
            tableData: [],
            loading: false,
        }
    },
    watch: {
        trackerItem: {
            immediate: true,
            handler: function (newVal, oldVal) {
                if (newVal) {
                    this.loadData();
                }
            }
        }
    },
    mounted() {

    },
    methods: {
        loadData() {
            if (this.trackerItem?.relatedWikis?.length > 0) {
                findByPageIds(this.trackerItem.relatedWikis).then(res => {
                    this.tableData = res;
                    this.loading = false;
                })
            }
        },
        onSelectTrackerWiki() {
            this.$refs.trackerWikiSlectModal.view(null);
        },
        onEditWiki(row) {

            const routeData = this.$router.resolve({
                path: `/tracker/project/${this.projectId}/wiki/${row.id}`
            });
            window.open(routeData.href, '_blank');
        },
        onDeleteWiki(row) {
            for (let index in this.tableData) {
                if (row.id == this.tableData[index].id) {
                    this.$delete(this.tableData, index)
                    break;
                }
            }
            this.trackerWikiSelector(this.tableData)
        },
        trackerWikiSelector(select) {

            let ids = select.map(item => item.id);
            let oldIds = this.tableData.map(item => item.id);
            this.trackerItem.relatedWikis.push(...ids.filter(id => oldIds.indexOf(id) == -1));
            this.tableData = Object.assign([], select);
            if (this.trackerItem?.id) {
                relatedWikis(this.trackerItem?.id, ids).then(() => {
                    this.$emit("loadComment")
                })
            }
        },
    },
}
</script>

<style lang="less" scoped>
.task-detail-module {
    margin: 0 20px 0;
}
</style>