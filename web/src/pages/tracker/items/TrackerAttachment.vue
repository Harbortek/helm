<template>
    <div :class="!isToolBar ? 'task-detail-module' : ''">
        <vxe-toolbar size="medium">
            <template #buttons>
                <span class="task-detail-module-title">文件</span>
            </template>
            <template #tools>
                <a-upload name="file" :multiple="false" :showUploadList="false" :directory="false"
                    :customRequest="onUpload">
                    <a-button :disabled="trackerItem?.notPagePerm" icon="upload">上传文件</a-button>
                </a-upload>
            </template>
        </vxe-toolbar>
        <vxe-table :data="tableData" :loading="loading" :row-config="{ isHover: true }" :show-header="false">
            <vxe-column type="seq" width="60" />
            <vxe-column field="name" title="文件名">
                <template #default="{ row }">
                    <div class="resource-item-container">
                        <div class="resource-item">
                            <div class="resource-icon">
                                <img :src="getFileIcon(row.filePath)" />
                            </div>
                            <div class="resource-info">
                                <div class="resource-info-name">{{ row.name }}</div>
                                <div class="resource-info-otherinfo">
                                    <span class="resource-info-size">{{ getFileSize(row.fileSize) }}</span>
                                    <span>来自</span>
                                    <span class="resource-info-owner">{{ row.createBy?.name }}</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </template>
            </vxe-column>

            <vxe-column v-if="!isToolBar" width="200px" title="上传时间" header-align="center">
                <template #default="{ row }">
                    {{ row.createDate }}
                </template>
            </vxe-column>

            <vxe-column width="200px" title="操作" header-align="center">
                <template #default="{ row }">
                    <vxe-button type="text" icon="vxe-icon-download" @click="onDownload(row)">下载</vxe-button>
                    <vxe-button type="text" icon="vxe-icon-delete" :disabled="trackerItem?.notPagePerm"
                        @click="onDelete(row)">删除</vxe-button>
                </template>
            </vxe-column>

        </vxe-table>
    </div>
</template>

<script>
import { uploadFile, downloadFile } from '@/services/global/FileService'
import { uploadAttachment, deleteAttachment, findAttachmentsByObjectId } from "@/services/tracker/TrackerItemService"
import { fileImgMap, unknownImg, calculateFileSize } from "@/utils/fileUtil"
import VXETable from "vxe-table";
export default {
    name: 'TrackerAttachment',
    components: {},
    props: {
        trackerItem: {
            required: true
        },
        isToolBar: {
            required: false
        }
    },
    data() {
        return {
            tableData: [],
            loading: false,
        }
    },
    watch: {
        "trackerItem.id": {
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
            if (this.trackerItem?.id) {
                // this.tableData = this.trackerItem.attachments
                findAttachmentsByObjectId(this.trackerItem.id).then(resp => {
                    this.tableData = resp
                })
            }
        },
        onUpload(e) {
            uploadFile(e.file).then((resp) => {
                this.uploadedFile = resp.url
                e.onSuccess(resp, e.file)

                let attachment = { name: resp.origin_name, filePath: resp.url, fileSize: resp.fileSize }
                if (this.trackerItem?.id) {
                    uploadAttachment(this.trackerItem.id, attachment).then(resp2 => {
                        const attachment = resp2
                        this.tableData.push(attachment)
                        this.$emit("loadComment")
                    })
                } else {
                    this.tableData.push(attachment)
                    Object.assign(attachment, { type: "ATTACHMENT" });
                    this.trackerItem.attachments.push(attachment)
                }
            })
        },
        onDownload(attachment) {
            downloadFile({
                name: attachment.name,
                url: attachment.filePath
            })
        },
        onDelete(attachment) {
            VXETable.modal.confirm({
                title: '删除附件',
                content: '「' + attachment.name + '」将被删除，该动作不可恢复'
            }).then(type => {
                if (type === 'confirm') {
                    if (this.trackerItem?.id) {
                        deleteAttachment(this.trackerItem.id, attachment).then(resp => {
                            this.tableData = this.tableData.filter(row => row.id != attachment.id)
                            this.$emit("loadComment")
                        })
                    } else {
                        this.$delete(this.trackerItem.attachments, this.trackerItem.attachments.indexOf(attachment))
                        this.tableData = this.tableData.filter(row => row != attachment)
                    }
                }
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
        }
    },
}
</script>

<style lang="less" scoped>
.task-detail-module {
    margin: 0 20px 0;
}

.resource-item-container:not(.resource-item-container-readonly) {
    cursor: pointer;
}

.resource-item-container {
    .resource-item {
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        display: flex;
        align-items: center;
        height: 100%;
        margin-left: 20px;
        margin-right: 20px;
        box-sizing: border-box;

        .resource-icon {
            line-height: 0;
            margin-right: 10px;
            width: 32px;
            flex: 0 0 auto;

            img {
                width: 32px;
            }
        }

        .resource-info {
            display: flex;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
            flex-direction: column;
            flex: 1 1 auto;

            .resource-info-name {
                overflow: hidden;
                text-overflow: ellipsis;
                white-space: nowrap;
                line-height: 1.5;
            }

            .resource-info-otherinfo {
                overflow: hidden;
                text-overflow: ellipsis;
                white-space: nowrap;
                font-size: 12px;
                color: #909090;

                .resource-info-size {
                    margin-right: 10px;
                }

                .resource-info-owner {
                    margin-left: 10px;
                }
            }
        }
    }
}

.task-detail-module-title {
    font-size: 15px;
    color: #303030;
    font-weight: 500;
    display: flex;
    align-items: center;
    justify-content: space-between;
}
</style>