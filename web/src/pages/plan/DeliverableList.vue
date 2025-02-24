<template>
    <config-page title="交付物"
        description="交付物是衡量一个里程碑完成质量的重要指标，而里程碑的完成则代表整个项目计划的推进是否健康，所以在每一个里程碑完成后，项目经理都将对里程碑设定相应的交付物用来交付甲方或内部验收。">
        <div>
            <vxe-toolbar style="flex: 0 0;">
                <template #buttons>
                    已提交 {{ committed }}项，共{{ total }}项
                </template>
            </vxe-toolbar>
            <vxe-table size="small" ref="xTable" show-overflow border row-key :show-header="true" :auto-resize="true"
                :data="tableData" :row-config="{ keyField: 'id', isCurrent: true }" style="flex: 1 1 auto;">
                <vxe-column type="seq" width="50px" />

                <vxe-column field="sourceName" title="来源名称" width="100px">
                    <template #default="{ row }">
                        <a @click="gotoMilestone(row.milestoneId)">{{ row?.sourceName }}</a>
                    </template>
                </vxe-column>
                <vxe-column field="sourceType" title="来源类型" width="100px">
                    <template #default="{ row }">
                        {{ $t('plan.type.' + row.sourceType) }}
                    </template>
                </vxe-column>
                <vxe-column field="name" title="交付物名称" />
                <vxe-column field="type" title="类型" width="80px">
                    <template #default="{ row }">
                        {{ $t('deliverable.type.' + row.type) }}
                    </template>
                </vxe-column>
                <vxe-column field="url" title="交付情况" width="200px">
                    <template #default="{ row }">
                        <template v-if="row.committed">
                            <span v-if="row?.type === 'URL'"><a target="_blank" @click="openPage(row.url)">
                                    {{ row.url }}</a> </span>
                            <span v-if="row?.type === 'FILE'">
                                <img :src="getFileIcon(row.attachment?.filePath)" width="16" height="16" />
                                <a @click="onDownload(row.attachment)"> {{ row.attachment.name }}</a>
                            </span>
                        </template>
                        <template v-else>
                            -
                        </template>
                    </template>
                </vxe-column>
                <vxe-column field="fileSize" title="文件大小" width="100px">
                    <template #default="{ row }">
                        <template v-if="row.committed">
                            <span v-if="row?.type === 'FILE'"> {{ getFileSize(row.attachment?.fileSize)
                            }}
                            </span>
                        </template>
                        <template v-else>
                            -
                        </template>
                    </template>
                </vxe-column>
                <vxe-column field="lastModifyDate" title="提交时间" width="200px">
                    <template #default="{ row }">
                        <template v-if="row.committed">
                            {{ row.lastModifiedDate }}
                        </template>
                        <template v-else>
                            -
                        </template>
                    </template>
                </vxe-column>
                <vxe-column field="createBy.name" title="交付者" width="100px" :edit-render="{}">
                    <template #default="{ row }">
                        <template v-if="row.committed">
                            <h-avatar :name="row.lastModifiedBy?.name" :icon="row.lastModifiedBy?.icon"></h-avatar>
                        </template>
                        <template v-else>
                            -
                        </template>
                    </template>
                </vxe-column>

                <!-- <vxe-column title="操作" header-align="center" width="100">
                    <template #default="{ row }">
                        <vxe-button type="text" v-if="row?.type === 'URL'" size="medium"
                            @click="onSubmitDeliverable(row)">编辑链接</vxe-button>

                        <a-upload name="file" v-if="row?.type === 'FILE'" :multiple="false" :showUploadList="false"
                            :directory="false" :customRequest="(e) => onUpload(row, e)">
                            <vxe-button type="text">提交交付物</vxe-button>
                        </a-upload>
                    </template>
                </vxe-column> -->
            </vxe-table>
        </div>
    </config-page>
</template>

<script>
import HAvatar from '@/components/avatar/h-avatar.vue';
import { findMilestones, findDeliverables } from '@/services/plan/PlanService'
import ConfigPage from '@/components/config-page/ConfigPage.vue';
import { uploadFile, downloadFile } from '@/services/global/FileService'
import { fileImgMap, unknownImg, calculateFileSize } from "@/utils/fileUtil"
import { hasPermission } from '@/utils/permission'
import { findPageByComponentType } from "@/services/tracker/ProjectPageService";
export default {
    name: 'DeliverabeList',
    components: { ConfigPage, HAvatar },
    data() {
        return {
            tableData: [],
        }
    },
    computed: {
        projectId() {
            return this.$route.params.projectId
        },
        pageId() {
            return this.$route.params.pageId
        },
        committed() {
            return this.tableData.filter(row => row.committed).length;
        },
        total() {
            return this.tableData.length
        }
    },  
    mounted() {
        this.loadData()
    },
    methods: {
        loadData() {
            findMilestones(this.projectId).then(resp => {
                const data = resp
                this.tableData = []
                data.forEach(element => {
                    const objs = element.deliverables || []
                    objs.forEach(obj => {
                        if(obj){
                            const item = obj
                            item.sourceName = element.name
                            item.sourceType = 'MILE_STONE'
                            this.tableData.push(item)
                        }
                    })
                });
            })
        },
        onUpload(row, e) {
            uploadFile(e.file).then((resp) => {
                e.onSuccess(resp, e.file)

                let attachment = { name: resp.origin_name, filePath: resp.url, fileSize: resp.fileSize }
                console.log(attachment)
                uploadAttachment(row.id, attachment).then(resp2 => {
                    this.loadData()
                })
            })
        },
        onDownload(attachment) {
            downloadFile({
                name: attachment.name,
                url: attachment.filePath
            })
        },
        getFileIcon(filePath) {
            let index = filePath.lastIndexOf(".");
            //获取后缀
            let ext = filePath.substr(index + 1);

            let iconPath = fileImgMap.get(ext)
            if (!iconPath) {
                return unknownImg
            }
            return iconPath
        },
        getFileSize(fileSize) {
            return calculateFileSize(fileSize)
        },
        openPage(url) {
            if(!hasPermission("PAGE_WRITE",this.pageId)){
                return;
            }  
            window.open(url, '_blank')
        },
        gotoMilestone(milestoneId) {
            // if(!hasPermission(this.pageId, 'WRITE')){
            //     return;
            // }
            // this.$router.push({ name: 'milestoneDetail', params: { itemId: milestoneId } })
            this.getMilestonePageId().then(milestonePageId=>{
                this.$router.push({ name: 'milestoneDetail', params: { itemId: milestoneId,pageId: milestonePageId } })
            })
        },
        async getMilestonePageId(){
            let milestonePageId;
            await findPageByComponentType(this.projectId,"milestones").then(res=>{
                milestonePageId=res.id;
            })
            return milestonePageId;
        },
    },
}
</script>

<style lang="less" scoped></style>