<template>
    <div style="width: 100%; height: 100%;">
        <page-editor :pageId="smartPageId" @close="onClose"></page-editor>
    </div>
</template>

<script>
import ContentPage from '@/components/page/content/ContentPage.vue';
import PageEditor from '@/pages/smart-page/editor/PageEditor.vue';
import { findOneProjectPage } from '@/services/tracker/ProjectPageService';

export default {
    name: 'ProjectReportEditor',
    components: {
        ContentPage, PageEditor
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
    mounted() {
        this.loadData()
    },
    methods: {
        loadData() {
            console.log(this.pageId)
            findOneProjectPage(this.pageId).then(resp => {
                this.smartPageId = resp.smartPageId
                console.log(this.smartPageId)
            })
        },
        onClose() {
            this.$router.push({
                name: 'smartPage',
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