<template>
    <div style="width: 100%; height: 100%;">
        <page-viewer :pageId="pageId"  @edit="onEdit"></page-viewer>
    </div>
</template>

<script>
import ContentPage from '@/components/page/content/ContentPage.vue';
import PageViewer from '@/pages/smart-page/viewer/PageViewer.vue';
import { findOneReport } from '@/services/product-line/ReportService';
import { findOneReportGroup } from '@/services/product-line/ReportGroupService';
export default {
    name: 'ReportViewer',
    components: {
        ContentPage, PageViewer
    },
    data() {
        return {
            pageId: undefined,
            report: {},
            reportGroup: {},
        }
    },
    computed: {
        reportId() {
            return this.$route.params.reportId;
        },
        productLineId() {
            return this.$route.params.productLineId;
        },
    },
    mounted() {
        this.loadData()
    },
    methods: {
        loadData() {
            findOneReport(this.productLineId, this.reportId).then(resp => {
                this.report = resp
                this.pageId = resp.smartPageId
                findOneReportGroup(this.productLineId, this.report.groupId).then(resp2 => {
                    this.reportGroup = resp2
                })
            })
        },
        onEdit() {
            this.$router.push({
                name: 'productLineReportEditor',
                params: {
                    productLineId: this.productLineId,
                    reportId: this.reportId
                }
            })
        }
    },
}
</script>

<style lang="less" scoped>
.page-header {
    position: absolute;
    float: left;
    flex: 0 0 auto;
    line-height: 48px;
    margin-top: 10px;
    margin-left: 20px;

    .ant-breadcrumb {
        color: rgba(93, 99, 105);
        font-size: 18px;
        line-height: 26px;
    }

    .ant-breadcrumb a {
        color: rgba(93, 99, 105);
        font-size: 18px;
        line-height: 26px;
    }

}
</style>