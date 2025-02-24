<template>
    <div style="height: 100%;">
        <div class="ui-task-detail-side" type="flex">
            <div class="ui-task-detail-side__inner" :span="24">
                <div :style="{ paddingTop: title ? '' : '0' }"
                    class="task-detail-module task-log task-detail-module-border">
                    <div class="task-detail-module-title">
                        <div class="task-detail-module-title-text">
                            <div class="task-log-title">{{ title }}<div class="task-log-btns">
                                    <div class="task-log-sort-btn">
                                        <div class="menuIcon  task-log-sort-btn"></div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="task-log-wrapper">
                        <a-tabs v-model="taskLogTab" default-active-key="ALL" size="small" @change="loadData">
                            <a-tab-pane key="ALL" tab="全部">
                            </a-tab-pane>
                            <a-tab-pane key="COMMENTS" tab="评论">
                            </a-tab-pane>
                            <a-tab-pane key="ATTACHMENT" tab="文件">
                            </a-tab-pane>
                            <a-tab-pane key="RELATED_ITEMS" tab="关联">
                            </a-tab-pane>
                            <a-tab-pane key="CHANGE_LOG" tab="变更记录">
                            </a-tab-pane>
                        </a-tabs>
                        <div class="task-log-message">
                            <div class="task-log-message" ref="taskLogMessage">
                                <div class="table">
                                    <div class="table-body">
                                        <div class="message" v-for="item in data" :key="item.id">
                                            <div class="message-body" v-if="item.type === 'CHANGELOG'">
                                                <div class="message-icon">
                                                    <a-icon type="form"
                                                        v-if="item.messageType === 'TRACKER_ITEM_FIELD_CHANGE'" />
                                                    <a-icon type="retweet"
                                                        v-if="item.messageType === 'TRACKER_ITEM_STATUS_CHANGE'" />
                                                    <a-icon component="lifeCycle"
                                                        v-if="item.messageType === 'TRACKER_ITEM_LIFE_CYCLE'" />
                                                    <a-icon type="bell"
                                                        v-if="item.messageType === 'TRACKER_ITEM_NOTIFICATION'" />
                                                    <a-icon type="link"
                                                        v-if="item.messageType === 'TRACKER_ITEM_ASSOCIATION'" />
                                                </div>
                                                <div class="message-info">
                                                    <span class="message-user">{{ item.createBy.name }}:</span>

                                                    <span v-if="item.messageType === 'TRACKER_ITEM_FIELD_CHANGE'"
                                                        class="message-text">{{ item.message }}</span>
                                                    <span v-else class="message-text">{{ item.message }}</span>
                                                    <div class="aggregate-log-attribute"
                                                        v-if="item.messageType === 'TRACKER_ITEM_FIELD_CHANGE'">
                                                        <div class="aggregate-log-attribute-name" title="标题">
                                                            <div class="" style="-webkit-line-clamp: 3;">{{
                                                                item?.target?.name||'' }}</div>
                                                        </div>
                                                        <div class="aggregate-log-attribute-content">
                                                            <span class="aggregate-log-attribute-single-line"></span>
                                                            <span class="log-diff">
                                                                <span class="diff-remove">{{ item.oldValue }}</span>
                                                                <a-icon type="right" />
                                                                <span class="diff-add">{{ item.newValue }}</span>
                                                            </span>
                                                        </div>
                                                    </div>
                                                    <div class="message-footer">
                                                        <div class="message-time ui-font-tips">
                                                            <a-tooltip>
                                                                <template slot="title">
                                                                    {{ longDate(item.createDate) }}
                                                                </template>
                                                                <span>{{ shortDate(item.createDate) }}</span>
                                                            </a-tooltip>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="message-body" v-if="item.type === 'ATTACHMENT'">
                                                <div class="message-icon">
                                                    <a-icon component="paperclip" />
                                                </div>
                                                <div class="message-info">
                                                    <span class="message-user">{{ item.createBy.name }} 上传了附件</span>
                                                    <div class="attachment-preview-card">
                                                        <div class="resource-icon"><img :src="getFileIcon(item.filePath)" />
                                                        </div>
                                                        <div class="resource-info">
                                                            <div class="resource-info-name">{{ item.name }}</div>
                                                            <div class="resource-info-otherinfo">
                                                                <span class="resource-info-size">{{
                                                                    getFileSize(item.fileSize) }}</span>
                                                            </div>
                                                        </div>
                                                        <div class="attachment-download-icon">
                                                            <a-button icon="download" @click="onDownload(item)" />
                                                        </div>
                                                    </div>
                                                    <div class="message-footer">
                                                        <div class="message-time ui-font-tips">
                                                            <a-tooltip>
                                                                <template slot="title">
                                                                    {{ longDate(item.createDate) }}
                                                                </template>
                                                                <span>{{ shortDate(item.createDate) }}</span>
                                                            </a-tooltip>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="comment-message-container" v-if="item.type === 'COMMENT'">
                                                <div class="comment-block-container">
                                                    <div class="avatar-container">
                                                            <h-avatar :name="item.createBy.name" :icon="item.createBy.icon" :isShowName="false"></h-avatar>
                                                    </div>
                                                    <div class="content-container">
                                                        <div class="user-container"><span class="user-name">{{
                                                            item.createBy.name }}:</span></div>
                                                        <div class="viewer-container">
                                                            <span class="message-text" v-html="item.message"
                                                                v-if="!item.markDeleted"></span>
                                                            <span class="message-text" v-if="item.markDeleted">评论已被删除</span>
                                                        </div>

                                                        <div class="bottom-bar-container">
                                                            <div class="message-time ui-font-tips">
                                                                <a-tooltip>
                                                                    <template slot="title">
                                                                        {{ longDate(item.lastModifiedDate) }}
                                                                    </template>
                                                                    <span>{{ (item.markDeleted ? '删除于' : '') +
                                                                        shortDate(item.lastModifiedDate)
                                                                    }}</span>
                                                                </a-tooltip>
                                                            </div>
                                                            <div class="bottom-bar-button" v-if="!item.markDeleted">
                                                                <a-button type="link" size="small"
                                                                    @click="onReplyToComment(item)">回复</a-button> |
                                                                <a-button type="link" size="small"
                                                                    @click="onEditComment(item)">编辑</a-button> |
                                                                <a-button type="link" size="small"
                                                                    @click="onDeleteComment(item)">删除</a-button>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                                <div class="sub-discussions-container" v-if="item.replies.length > 0">
                                                    <div class="comment-block-container" v-for="reply in item.replies"
                                                        :key="reply.id">
                                                        <div class="avatar-container">
                                                            <h-avatar :name="item.createBy.name" :icon="item.createBy.icon" :size="16" :isShowName="false"></h-avatar>
                                                        </div>
                                                        <div class="content-container">
                                                            <div class="user-container"><span class="user-name">{{
                                                                reply.createBy.name }}:</span></div>
                                                            <div class="viewer-container">
                                                                <span class="message-text" v-if="!reply.markDeleted"
                                                                    v-html="reply.message"></span>
                                                                <span class="message-text"
                                                                    v-if="reply.markDeleted">评论已被删除</span>
                                                            </div>

                                                            <div class="bottom-bar-container">
                                                                <div class="message-time ui-font-tips">
                                                                    <a-tooltip>
                                                                        <template slot="title">
                                                                            {{ longDate(reply.lastModifiedDate) }}
                                                                        </template>
                                                                        <span>{{ (reply.markDeleted ? '删除于' : '') +
                                                                            shortDate(reply.lastModifiedDate)
                                                                        }}</span>
                                                                    </a-tooltip>
                                                                </div>
                                                                <div class="bottom-bar-button" v-if="!reply.markDeleted">
                                                                    <a-button type="link" size="small"
                                                                        @click="onReplyToReply(item, reply)">回复</a-button> |
                                                                    <a-button type="link" size="small"
                                                                        @click="onEditReply(item, reply)">编辑</a-button> |
                                                                    <a-button type="link" size="small"
                                                                        @click="onDeleteReply(item, reply)">删除</a-button>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>

                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="ui-task-detail__bottom" :span="24">
                <div>
                    <div class="message-input">
                        <div class="message-input-editor-container">
                            <div class="message-input-editor" v-show="commentEditMode">
                                <div class="message-input-editor-title-bar">
                                    <div>评论</div>
                                    <div class="message-input-editor-title-bar-fullscreen">
                                        <a-icon type="fullscreen" />
                                    </div>
                                </div>
                                <simple-editor ref="editor" v-if="commentEditMode" height="100" v-model="comment.message" :showToolbar="true" />
                                <div class="message-input-editor-btn-container">
                                    <a-space>
                                        <a-button @click="onCancelComment">取消</a-button>
                                        <a-button type="primary" @click="onSendComment">确定</a-button>
                                    </a-space>
                                </div>
                            </div>
                            <div class="message-input-container" v-show="!commentEditMode">
                                <div class="message-input-btn-container-left" @click="commentEditMode = true">
                                    <span class="message-input-placeholder">评论</span>
                                </div>
                                <div class="message-input-btn-container-right">
                                    <a-upload name="file" :multiple="false" :showUploadList="false" :directory="false"
                                        :customRequest="onUpload">
                                        <a-icon component="paperclip" />
                                    </a-upload>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div v-if="itemId" class="task-detail-watchers">
                    <div class="task-detail-watchers-title">关注者</div>
                    <span
                        class="task-detail-watcher-picker-toggler ones-user-select-menu task-detail-watcher-picker-toggler">                       
                        <tracker-item-user-select v-if="projectId" :projectId="projectId" 
                            :field="{id:'1',systemProperty:'watchers',title:'关注者'}" mode="multiple"
                            :trackerItem="trackerItem">
                            <template #default>
                                <div class="task-detail-watchers-count">{{ watchersCount }}</div>
                            </template>
                        </tracker-item-user-select>
                        <div class="task-detail-watchers-list">{{ watcherNames }}</div>
                    </span>
                    <div class="task-detail-watchers-watch">
                        <a-tooltip>
                            <template slot="title">
                                取消关注
                            </template>
                            <a-icon @click="onClickCancelWatch" component="watched" v-show="hasWatched()"></a-icon>
                        </a-tooltip>
                        <a-tooltip>
                            <template slot="title">
                                关注
                            </template>
                            <a-icon @click="onClickWatch" component="unwatched" v-show="!hasWatched()"></a-icon>
                        </a-tooltip>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import HAvatar from '@/components/avatar/h-avatar.vue';
import { mapGetters, mapState, mapMutations } from "vuex";
import { shortDate, longDate } from "@/utils/DateUtils";
import { uploadFile, downloadFile } from '@/services/global/FileService'
import VXETable from "vxe-table";
import { fileImgMap, unknownImg, calculateFileSize } from "@/utils/fileUtil"
import SimpleEditor from '../../../components/editor/SimpleEditor.vue';
import { findTrackerLogs, createComment, saveComment, deleteComment, replyComment, uploadAttachment, 
    deleteAttachment,addWatch,cancelWatch } from "@/services/tracker/TrackerItemService"
import TrackerItemUserSelect from '@/components/select/TrackerItemUserSelect.vue';
import { hasPermission } from '@/utils/permission'


export default {
    name: 'TrackerComment',
    components: { SimpleEditor, HAvatar, TrackerItemUserSelect},
    props: {
        projectId: {
            required: false,
        },  
        pageId: {
            required: false,
        },
        itemId: {
            required: false,
        },
        watchers: {
            require: false,  
        },
        title: {
            required: false,
        },
        trackerItem: {
            required: false,
        }
    },
    data() {
        return {
            data: [],
            objectId:'',
            taskLogTab: 'ALL',
            comment: { message: '' },
            commentEditMode: false,
            editMode: 'CreateComment',
        }
    },
    watch: {
        pageId: {
            immediate: true,
            handler: function (newVal, oldVal) {
                if (newVal) {
                    this.objectId=newVal
                    this.loadData();
                }
            }
        },
        itemId: {
            immediate: true,
            handler: function (newVal, oldVal) {
                if (newVal) {
                    this.objectId=newVal
                    this.loadData();
                }
            }
        }
    },
    computed: {
        watchersCount() {
            if (this.watchers && Array.isArray(this.watchers)) {
                return this.watchers.length
            }
            return 0
        },
        watcherNames() {
            let names = []
            if (this.watchers && Array.isArray(this.watchers)) {
                this.watchers.forEach(w => { names.push(w.name) })
            }
            return names.join(',')
        },
        ...mapGetters("account", ["user"]),
    },
    mounted() {
    },
    methods: {
        loadData() {
            this.editMode === 'CreateComment'
            this.commentEditMode = false
            this.comment = { message: '' }
            let includeComment = this.taskLogTab === 'ALL' || this.taskLogTab === 'COMMENTS'
            let includeChangeLog = this.taskLogTab === 'ALL' || this.taskLogTab === 'CHANGE_LOG'
            let includeAttachment = this.taskLogTab === 'ALL' || this.taskLogTab === 'ATTACHMENT'
            let includeAssociation = this.taskLogTab === 'ALL' || this.taskLogTab === 'RELATED_ITEMS'
            if (this.objectId) {
                findTrackerLogs(this.objectId, includeComment, includeChangeLog, includeAttachment, includeAssociation).then(resp => {
                    this.data = resp;
                })
            }

        },
        onClickWatch(){
            if(this.itemId){
                addWatch(this.objectId,this.user).then(res=>{
                    this.$emit("refresh")
                    VXETable.modal.message({ content: '工作项关注成功', status: 'success' })
                })
            }
            
        },
        onClickCancelWatch(){
             if(this.itemId){
                cancelWatch(this.objectId,this.user).then(res=>{
                    this.$emit("refresh")
                    VXETable.modal.message({ content: '已取消关注', status: 'success' })
                })
             }
        },
        hasWatched() {
            if (this.watchers && Array.isArray(this.watchers)) {
                const uid = this.user.id
                return this.watchers.find(w => w.id === uid)
            }
            return false
        },
        shortDate(d) {
            return shortDate(d);
        },
        longDate(d) {
            return longDate(d)
        },
        onCancelComment() {
            this.commentEditMode = false
            this.comment = { message: '' }
        },
        onSendComment() {
            if (this.editMode === 'CreateComment') {
                createComment(this.objectId, this.comment).then(resp => {
                    this.loadData()
                })
            } else if (this.editMode === 'ReplyToComment') {
                replyComment(this.objectId, this.comment).then(resp => {
                    this.loadData()
                })
            } else if (this.editMode === 'EditComment') {
                saveComment(this.objectId, this.comment).then(resp => {
                    this.loadData()
                })
            } else if (this.editMode === 'ReplyToReply') {
                replyComment(this.objectId, this.comment).then(resp => {
                    this.loadData()
                })
            } else if (this.editMode === 'EditReply') {
                saveComment(this.objectId, this.comment).then(resp => {
                    this.loadData()
                })
            }
        },
        onReplyToComment(item) {
            this.commentEditMode = false
            this.comment = { parentId: item.id, message: '', replyTo: item.createBy }
            this.editMode = 'ReplyToComment'
            this.$nextTick(() => { //重新加载simple-editor
                this.commentEditMode = true
            })
        },
        onEditComment(item) {
            this.commentEditMode = false
            this.comment = item
            this.editMode = 'EditComment'
            this.$nextTick(() => {
                this.commentEditMode = true
            })
        },
        onDeleteComment(item) {
            VXETable.modal.confirm({
                title: '删除评论',
                content: '「评论」将被删除，该动作不可恢复'
            }).then(type => {
                if (type === 'confirm') {
                    deleteComment(this.objectId, item).then(resp => {
                        this.loadData()
                    })
                }
            })

        },
        onReplyToReply(item, reply) {
            this.commentEditMode = false
            this.comment = { parentId: item.id, message: '', replyTo: item.createBy }
            this.editMode = 'ReplyToReply'
            this.$nextTick(() => {
                this.commentEditMode = true
            })
        },
        onEditReply(item, reply) {
            this.commentEditMode = false
            this.comment = reply
            this.editMode = 'EditReply'
            this.$nextTick(() => {
                this.commentEditMode = true
            })
        },
        onDeleteReply(item, reply) {
            VXETable.modal.confirm({
                title: '删除评论',
                content: '「评论」将被删除，该动作不可恢复'
            }).then(type => {
                if (type === 'confirm') {
                    deleteComment(this.objectId, reply).then(resp => {
                        this.loadData()
                    })
                }
            })

        },
        onUpload(e) {
            uploadFile(e.file).then((resp) => {
                e.onSuccess(resp, e.file)

                let attachment = { name: resp.origin_name, filePath: resp.url, fileSize: resp.fileSize }
                uploadAttachment(this.objectId, attachment).then(resp2 => {
                    this.loadData();
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
        // scrollToBottom() {
        //     // this.$refs.taskLogMessage.scrollToBottom()
        //     const box = this.$refs.taskLogMessage
        //     console.log("box",box)
        //     this.$nextTick(() => {
        //         box.scrollTo({top:box.scrollHeight, behavior: 'smooth'})
        //     })
        // }
    }
}
</script>

<style lang="less">
.ui-task-detail-side {
    display: flex;
    flex-flow: column;
    height: 100%;

    .ui-task-detail-side__inner {
        flex: 1;
        min-height: 100px;
        overflow-y: auto;
        overflow: hidden;

        .task-detail-module {
            margin: 0 20px 0;
            padding-top: 25px;
            height: 100%;

            .task-detail-module-title {
                font-size: 15px;
                color: #303030;
                font-weight: 500;
                display: flex;
                align-items: center;

                .task-detail-module-title-text {
                    flex: 1 1 auto;

                    .task-log-title {
                        display: flex;
                        align-items: center;
                        justify-content: space-between;
                    }

                    .task-log-title .task-log-btns {
                        display: flex;
                    }
                }
            }

            .task-log-wrapper {
                display: flex;
                flex-direction: column;
                padding-bottom: 20px;
                margin-top: 5px;
                height: calc(100% - 10px);

                .task-log-tab {
                    border-bottom: 1px solid #e8e8e8;
                    margin-bottom: 10px;
                    padding-left: 0 !important;
                    padding-right: 0 !important;
                }

                .ant-tabs .ant-tabs-small-bar .ant-tabs-nav-container {
                    font-size: 12px;
                }

                .ant-tabs-tab {
                    margin-right: 0px;
                }

                .ant-tabs .ant-tabs-small-bar .ant-tabs-tab {
                    padding: 8px 12px;
                }

                .task-log-message {
                    overflow: auto;
                    height: 100%;
                    .table {
                        border: none;
                        padding-top: 10px;
                        padding-bottom: 10px;

                        .table-body {
                            width: 100%;
                            box-sizing: border-box;

                            .message {
                                .message-body {
                                    display: flex;

                                    .message-icon {
                                        flex: 0 0 auto;
                                        margin: 10px 0;
                                        width: 30px;
                                    }

                                    .message-info {
                                        min-width: 0;
                                        flex: 1;
                                        margin: 10px 0;
                                        font-size: 12px;
                                        color: #303030;
                                    }

                                    .message-user {
                                        font-size: 12px;
                                        color: #303030;
                                        font-weight: 500;
                                    }

                                    .message-text {
                                        line-height: 1.5;
                                        color: #909090;
                                    }

                                    .message-footer {
                                        position: relative;
                                        margin-top: 5px;
                                    }

                                    .task-log .log-field-diff-message {
                                        display: inline;

                                        .log-field-single-diff {
                                            display: inline-flex;
                                            flex-direction: row;
                                            align-items: center;
                                        }
                                    }

                                    .aggregate-log-task-template-attributes {
                                        font-size: 12px;
                                        margin-top: 5px;
                                    }

                                    .aggregate-log-attribute {
                                        display: flex;
                                        border-left: 3px solid #909090;
                                        flex-wrap: nowrap;

                                        .aggregate-log-attribute-name {
                                            flex: 0 0 auto;
                                            width: 75px;
                                            padding-left: 10px;
                                        }

                                        .aggregate-log-attribute-content {
                                            flex: 1 1 auto;
                                            min-width: 0;

                                            .aggregate-log-attribute-single-line {
                                                display: block;
                                                padding-bottom: 5px;
                                            }

                                            .log-diff {
                                                padding-left: 5px;
                                                display: inline;
                                            }

                                            .log-diff .diff-remove {
                                                color: #ea0d0d;
                                                text-decoration: line-through;
                                                text-decoration-color: #ea0d0d;
                                                background-color: rgba(234, 13, 13, .1);
                                            }

                                            .log-diff .diff-add {
                                                color: #24b47e;
                                                background-color: rgba(36, 180, 126, .1);
                                                margin-right: 10px;
                                            }
                                        }
                                    }

                                    .aggregate-log-task-template-attribute-time {
                                        display: inline-block;
                                        vertical-align: middle;
                                    }

                                    .attachment-preview-card {
                                        display: flex;
                                        align-items: center;
                                        position: relative;
                                        background: #f8f8f8;
                                        border-radius: 3px;
                                        padding: 8px 10px;
                                        margin-top: 5px;
                                        cursor: pointer;
                                        transition: background .2s;

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

                                        .attachment-download-icon {
                                            position: absolute;
                                            top: 11px;
                                            right: 0.7vw;
                                            width: 32px;
                                            height: 30px;
                                            border-radius: 3px;
                                            box-shadow: 0 0 2px 0 rgba(31, 31, 31, .05), 0 1px 2px 0 rgba(31, 31, 31, .15);
                                            background-color: var(--white);
                                            display: flex;
                                            justify-content: center;
                                            align-items: center;
                                            background: #fff;
                                            color: #606060;
                                            cursor: pointer;
                                            visibility: hidden;
                                        }

                                        &:hover .attachment-download-icon {
                                            visibility: visible;
                                        }
                                    }
                                }

                                .comment-message-container {
                                    margin-bottom: 20px;

                                    .comment-block-container {
                                        display: flex;
                                        margin-bottom: 10px;

                                        .avatar-container {
                                            height: 20px;
                                            line-height: 20px;

                                            .user-icon {
                                                width: 16px;
                                                height: 16px;
                                                line-height: 16px;
                                                font-size: 18px;
                                            }
                                        }

                                        .content-container {
                                            width: 0;
                                            flex: 1;
                                            margin-left: 15px;

                                            .user-container {
                                                font-size: 12px;
                                                line-height: 20px;

                                                .user-name {
                                                    font-weight: 500;
                                                    margin-right: 10px;
                                                }
                                            }
                                        }

                                        .bottom-bar-container {
                                            margin-top: 0px;
                                            width: 100%;
                                            display: flex;
                                            align-items: center;
                                            flex-wrap: wrap;
                                            line-height: 18px;

                                            .message-time {
                                                font-size: 12px;
                                                line-height: 18px;
                                                white-space: nowrap;
                                                margin-right: 30px;
                                            }

                                            .bottom-bar-button {
                                                display: none;
                                                height: 18px;
                                                line-height: 18px;
                                                align-items: center;

                                                .ant-btn-sm {
                                                    height: 18px;
                                                    padding: 0 7px;
                                                    font-size: 12px;
                                                    border-radius: 4px;
                                                    line-height: 18px;
                                                    color: #909090;
                                                }
                                            }

                                            &:hover .bottom-bar-button {
                                                display: block;
                                            }
                                        }
                                    }

                                    .sub-discussions-container {
                                        margin-left: 31px;
                                    }
                                }

                            }
                        }
                    }
                }

            }
        }
    }

    .ui-task-detail__bottom {
        flex: 0 0 auto;
        position: relative;
        overflow: hidden;
        z-index: 1;
        box-shadow: 0 -2px 4px 0 rgba(0, 0, 0, .05), 0 -1px 0 0 #efefef;
        background: #fff;
        border-bottom-right-radius: 3px;

        .message-input {
            position: relative;

            .message-input-editor-container {
                .message-input-editor {
                    .message-input-editor-title-bar {
                        height: 35px;
                        padding: 0 20px;
                        display: flex;
                        justify-content: space-between;
                        align-items: center;
                        background: #f8f8f8;
                        border-bottom: 1px solid #e8e8e8;

                        .message-input-editor-title-bar-fullscreen {
                            cursor: pointer;
                            color: #303030;
                        }
                    }

                    .message-input-editor-btn-container {
                        height: 48px;
                        line-height: 48px;
                        display: flex;
                        align-items: center;
                        justify-content: flex-end;
                        padding-right: 10px;
                    }
                }

                .message-input-container {
                    height: 48px;
                    display: flex;
                    align-items: center;
                    justify-content: space-between;
                    padding-right: 10px;

                    .message-input-btn-container-left {
                        flex: 1 1 auto;
                        line-height: 48px;

                        .message-input-placeholder {
                            width: 100%;
                            display: inline-block;
                            padding-left: 20px;
                            color: #909090;
                            cursor: text;
                            z-index: 1;
                            flex: 1 1 auto;
                        }
                    }

                    .message-input-btn-container-right {
                        display: flex;
                        align-items: center;
                    }
                }
            }
        }

        .task-detail-watchers {
            display: flex;
            align-items: center;
            height: 48px;
            border-top: 1px solid #e8e8e8;
            padding: 0 20px;
            line-height: 1.6;
            position: relative;

            .task-detail-watchers-title {
                flex: 0 0 auto;
                font-size: 14px;
                color: #303030;
                font-weight: 500;
            }

            .task-detail-watcher-picker-toggler {
                display: flex;
                flex: 1 1 auto;
                align-items: center;
                overflow: hidden;
                cursor: pointer;
                transition: color .2s;
                color: #303030;

                .task-detail-watchers-count {
                    cursor: pointer;
                    flex: 0 0 auto;
                    margin-left: 5px;
                    border-radius: 3px;
                    box-sizing: border-box;
                    display: inline-block;
                    height: 18px;
                    line-height: 16px;
                    width: 18px;
                    background: #e8e8e8;
                    text-align: center;
                    -webkit-user-select: none;
                    user-select: none;
                }

                .task-detail-watchers-list {
                    flex: 1 1 auto;
                    overflow: hidden;
                    text-overflow: ellipsis;
                    white-space: nowrap;
                    margin-left: 5px;
                    max-width: 220px;
                }

            }

            .task-detail-watchers-watch {
                flex: 0 0 auto;
                display: flex;
                align-items: center;
                margin-left: 20px;
                line-height: 1;
                font-size: 14px;
                cursor: pointer;
                transition: color .2s;
                color: #303030;
            }
        }

    }
}
</style>