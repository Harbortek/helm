<template>
    <div style="width: 100%; height: 100%;">
        <page-editor :pageId="pageId" @close="onClose"></page-editor>
    </div>
</template>

<script>
import ContentPage from '@/components/page/content/ContentPage.vue';
import PageEditor from '@/pages/smart-page/editor/PageEditor.vue';
import { findOneReport } from '@/services/product-line/ReportService';

export default {
    name: 'ReportEditor',
    components: {
        ContentPage, PageEditor
    },
    data() {
        return {
            pageId: undefined,
        }
    },
    computed: {
        reportId() {
            return this.$route.params.reportId;
        },
        productLineId() {
            return this.$route.params.productLineId;
        }
    },
    mounted() {
        this.loadData()
    },
    methods: {
        loadData() {
            findOneReport(this.productLineId, this.reportId).then(resp => {
                this.pageId = resp.smartPageId
            })
        },
        onClose() {
            this.$router.push({
                name: 'productLineReportViewer',
                params: {
                    productLineId: this.productLineId,
                    reportId: this.reportId
                }
            })
        }
    },
}
</script>

<style lang="less" scoped></style>