<template>
    <div style="width: 100%; height: 100%;">
        <page-viewer :pageId="smartPageId" @edit="onEdit"></page-viewer>
    </div>
</template>

<script>
import ContentPage from '@/components/page/content/ContentPage.vue';
import PageViewer from '@/pages/smart-page/viewer/PageViewer.vue';
import { findOneProjectPage } from '@/services/tracker/ProjectPageService';
import { hasPermission } from '@/utils/permission'
import VXETable from "vxe-table";


export default {
    name: 'ReportViewer',
    components: {
        ContentPage, PageViewer
    },
    data() {
        return {
            smartPageId: undefined,
        }
    },
    computed: {
        pageId() {
            return this.$route.params.pageId;
        },
        projectId() {
            return this.$route.params.projectId;
        }
    },
    watch: {
        pageId() {
            this.loadData()
        }
    },
    mounted() {
        this.loadData()
    },
    methods: {
        loadData() {
            findOneProjectPage(this.pageId).then(resp => {
                this.smartPageId = resp.smartPageId
                console.log('smartPageId', this.smartPageId)
            })
        },
        onEdit() {
            if (!hasPermission("PAGE_WRITE",this.pageId)) {
                VXETable.modal.message({ status: 'warning', content: '权限不足' })
                return;
            }
            this.$router.push({
                name: 'smartPageEditor',
                params: {
                    projectId: this.projectId,
                    pageId: this.pageId
                }
            })
        }
    },
}
</script>

<style lang="less" scoped></style>