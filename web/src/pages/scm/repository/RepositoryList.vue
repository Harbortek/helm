<template>
    <div>
        <a-layout style="height: 100%;">
            <a-layout-sider style="background-color: white;" :collapsedWidth="0" :collapsible="true" theme="light"
                :trigger="null" :defaultCollapsed="false" :collapsed="collapsed" width="300">
                <div class="repo-left">
                    <div class="repo-title">
                        <div class="repo-title-text">文件</div>
                        <div class="repo-title-tools"><h-icon type="collapse-left" @click="collapsed = true"></h-icon>
                        </div>
                    </div>
                    <div class="repo-branch">
                        <branch-select v-model="currentBranch" :projectId="projectId" @change="loadData" />
                    </div>
                    <div class="repo-tree">
                        <vxe-table :data="treeData" ref="xTree" :loading="loadingTree" row-id="fileId" border="outer"
                            :row-config="{ isHover: true, isCurrent: true, keyField: 'fileId' }"
                            :tree-config="{ transform: true, accordion: true, rowField: 'fileId', parentField: 'parentId', hasChild: 'directory', lazy: true, showIcon: true, loadMethod: loadChildrenMethod }"
                            :cell-style="table_item_class" :show-header="false" height="100%"
                            @current-change="onTreeNodeSelect">
                            <vxe-column field="name" title="名称" tree-node show-overflow="title">
                                <template #default="{ row }">
                                    <template v-if="row.directory">
                                        <h-icon type="folder-open" theme="filled"
                                            v-if="$refs.xTree.isTreeExpandByRow(row)"
                                            style="color: rgb(84, 174, 255);"></h-icon>
                                        <h-icon type="folder" theme="filled" v-else style="color: rgb(84, 174, 255);" />
                                    </template>
                                    <template v-else>
                                        <h-icon type="file" />
                                    </template>

                                    {{ row.name }}
                                </template>
                            </vxe-column>
                        </vxe-table>
                    </div>
                </div>
            </a-layout-sider>
            <a-layout style="overflow-y: hidden;">
                <a-layout-header class="repo-header">
                    <div class="repo-header-title">
                        <div class="file-path">
                            <div>
                                <a @click="showDirectory({ path: '/' })">
                                    <h-icon type="expand-right" @click="collapsed = false" v-if="collapsed"></h-icon>
                                    <span class="file-path-title"
                                        style="color: rgb(9, 105, 218);text-decoration: none;font-weight: 600;">
                                        {{ projectInfo?.name }}
                                    </span>
                                </a>
                            </div>
                            <div v-for="(item, index) in paths" :key="index" style="margin-left: 5px;">
                                /
                                <a @click="showDirectory(item)">
                                    <span class="file-path-title"
                                        style="color: #222222;text-decoration: none;font-weight: 600;">{{
                                            item.name
                                        }}</span>
                                </a>
                            </div>
                            <div class="open-icon">
                                <a target="_blank" rel="noopener noreferrer" @click="onShowGitLab"
                                    style="font-size: 16px;" title="查看原始仓库"><h-icon type="open-link" /></a>
                            </div>
                        </div>
                    </div>

                    <div class="last-commit">
                        <div class="commit-author">{{ lastCommit?.committerName }}</div>
                        <div class="commit-message">{{ lastCommit?.message }}</div>
                        <div class="commit-shortId"><a :href="lastCommit?.webUrl">{{
                            lastCommit?.shortId }}</a></div>
                        <div class="commit-date">{{ fromNow(lastCommit?.committedDate) }}</div>
                    </div>
                </a-layout-header>
                <a-layout-content :style="{ background: '#fff', padding: 0 }">
                    <div class="file-list" v-show="!showFile">
                        <vxe-table :data="files" :loading="loading" :row-config="{ isHover: true }" row-id="fileId">
                            <vxe-column field="name" title="名称">
                                <template #default="{ row }">


                                    <span v-if="row.directory"> <h-icon type="folder" theme="filled"
                                            style="color: rgb(84, 174, 255);margin-right:5px;"
                                            v-if="row.name !== '..'" /><a @click="showDirectory(row)">{{
                                                row.name }}</a></span>
                                    <span v-else> <h-icon type="file" /> <a @click="showFileContent(row)">{{ row.name
                                            }}</a> </span>
                                </template>
                            </vxe-column>
                            <vxe-column field="description" title="最后提交">
                                <template #default="{ row }">
                                    {{ row.lastCommit?.message }}
                                </template>
                            </vxe-column>
                            <vxe-column align="right" field="createDate" title="最后修改">
                                <template #default="{ row }">
                                    {{ fromNow(row.lastCommit?.committedDate) }}
                                </template>
                            </vxe-column>
                        </vxe-table>
                    </div>
                    <div class="file-content" v-show="showFile">
                        <div class="code-container">
                            <div id="code-editor" ref="code-editor" class="code-editor" style=""
                                v-show="currentFile?.type === 'text'"></div>
                        </div>

                        <div id="raw-file-download" class="code-editor" style=""
                            v-show="currentFile?.type === 'binary'">
                            <div class="download-area">
                                <div><h-icon type="download" style="font-size:24px;" /></div>
                                <div style="font-size:24px;"><a @click="onDownloadFile">下载{{
                                    getFileSize(currentFile?.size)
                                        }}</a></div>
                            </div>
                        </div>
                    </div>
                </a-layout-content>
            </a-layout>
        </a-layout>

    </div>
</template>

<script>
import { findProjectInfo, findLastCommit, findFiles, findFileContent, fileDownload } from '@/services/scm/RepositoryService'
import BranchSelect from './BranchSelect.vue'
import XEUtils from 'xe-utils'
import { fromNow } from '@/utils/DateUtils'
import { fileImgMap, unknownImg, calculateFileSize } from "@/utils/fileUtil"
import * as monaco from 'monaco-editor';
import { encode, decode } from '@cnwhy/base64';
import MonacoEditorCopilot from 'monaco-editor-copilot';
import Cookies from 'js-cookie'
export default {
    name: 'RepositoryList',
    components: { BranchSelect },
    data() {
        return {
            monacoEditor: null,
            projectInfo: undefined,
            collapsed: false,
            loading: false,
            loadingTree: false,
            files: [],
            currentPath: '/',
            currentFile: undefined,
            currentBranch: undefined,
            keyword: '',
            treeData: [],
            showFile: false,
            lastCommit: undefined,
            table_item_class: {
                fontSize: '14px',
                height: '30px'
            },
        }
    },
    computed: {
        projectId() {
            return this.$route.params.projectId
        },
        paths() {
            if (this.currentPath === '/') {
                return []
            }
            const paths = this.currentPath.split('/')
            let result = []
            const count = paths.length

            for (let i = 0; i < count; i++) {
                let array = []
                for (let j = 0; j <= i; j++) {
                    array.push(paths[j])
                }
                let fullPath = array.join('/')
                result.push({ name: paths[i], path: fullPath })
            }
            console.log(result)
            return result
        },
        rawContent() {
            if (this.currentFile) {
                if (this.currentFile.encoding === 'BASE64') {
                    console.log(decode(this.currentFile.content).toString())
                    return decode(this.currentFile.content).toString()
                } else if (this.currentFile.encoding === 'TEXT') {
                    return this.currentFile.content
                }
            }
        },
    },
    mounted() {
        findProjectInfo(this.projectId).then(resp => {
            this.projectInfo = resp
        })
        this.monacoEditor = monaco.editor.create(document.getElementById("code-editor"), {
            value: this.rawContent, // 初始文字
            readOnly: true, // 是否只读
            automaticLayout: true, // 自动布局
            theme: 'vs-light', // vs | hc-black | vs-dark
            minimap: {
                enabled: false,// 关闭小地图
            },
            tabSize: 2, // tab缩进长度
            fontSize: 14, // 文字大小,
            scrollBeyondLastLine: false,
        });
        const config = {
            openaiKey: Cookies.get("Authorization").slice(7),
            openaiUrl: process.env.VUE_APP_API_BASE_URL + '/ai/openai',
            assistantMessage: 'use sql mysql',
            openaiParams: {
                model: '',
            },
        };

        const dispose = MonacoEditorCopilot(this.monacoEditor, config);
    },
    beforeDestroy() {
        if (this.monacoEditor) {
            this.monacoEditor.dispose();
        }
    },
    methods: {
        fromNow(v) {
            if (!v) {
                return ''
            }
            return fromNow(v)
        },
        loadData() {
            this.loading = true
            this.loadingTree = true
            if (this.currentBranch) {
                findFiles(this.projectId, this.currentPath, this.currentBranch.name, null).then(resp => {
                    this.files = resp;
                    this.treeData = XEUtils.clone(this.files)
                    this.loading = false
                    this.loadingTree = false
                })
                findLastCommit(this.projectId, this.currentPath, this.currentBranch.name).then(resp => {
                    this.lastCommit = resp
                })
            }
        },
        onTreeNodeSelect({ newValue, oldValue, row }) {
            if (newValue.directory) {
                this.showDirectory(newValue)
            } else {
                this.showFileContent(newValue)
            }

        },
        loadChildrenMethod({ row }) {
            return findFiles(this.projectId, row.path, this.currentBranch.name, row.fileId)
        },
        showDirectory(row) {
            this.expandTreeNodeAndSelect(row)
            this.showFile = false
            this.currentPath = row.path
            this.loading = true
            findFiles(this.projectId, row.path, this.currentBranch.name, row.fileId).then(resp => {
                this.files = []
                if (this.currentPath === '' || this.currentPath == '/') {

                } else {
                    const parentPath =
                        row.path.substring(0, row.path.lastIndexOf('/'))
                    this.files = [{ path: parentPath, name: '..', directory: true }]
                }
                this.files = this.files.concat(resp);
                this.loading = false
            })
            findLastCommit(this.projectId, this.currentPath, this.currentBranch.name).then(resp => {
                this.lastCommit = resp
            })
        },
        showFileContent(row) {
            this.expandTreeNodeAndSelect(row)
            this.currentPath = row.path
            this.loading = true
            findFileContent(this.projectId, row.path, this.currentBranch.name).then(resp => {
                this.currentFile = resp
                this.showFile = true
                if (this.currentFile.type === 'text') {
                    monaco.editor.getModels().forEach(model => model.dispose());
                    const model = monaco.editor.createModel(
                        this.rawContent,
                        undefined, // language
                        monaco.Uri.file(this.currentFile.name) // uri
                    )

                    this.monacoEditor.setModel(model)
                }

                this.loading = false
            })
            findLastCommit(this.projectId, this.currentPath, this.currentBranch.name).then(resp => {
                this.lastCommit = resp
            })
        },
        onDownloadFile() {
            fileDownload(this.projectId, this.currentPath, this.currentBranch.name)
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
        onShowGitLab() {
            const url = this.projectInfo.webUrl + '/-/tree/' + this.currentBranch.name + '/' + this.currentPath + '?ref_type=heads';
            console.log(url)
            window.open(url, '_blank')
        },
        expandTreeNodeAndSelect(row) {
            console.log('will expand', row)
            const treeRow = this.$refs.xTree.getTableData().tableData.filter(item => { return item.path === row.path })[0]
            console.log('expand ', treeRow)
            this.$refs.xTree.setTreeExpand(treeRow, true)
            this.$nextTick(() => {
                this.$refs.xTree.setCurrentRow(treeRow)
            })

        },
    },
}
</script>

<style lang="less" scoped>
.repo-left {
    display: flex;
    flex-direction: column;
    margin-left: 10px;
    margin-right: 10px;

    .repo-title {
        width: 100%;
        margin-top: 26px;
        margin-bottom: 8px;
        display: inline-flex;
        flex-direction: row;

        .repo-title-text {
            font-size: 16px;
            font-weight: 500;
            flex: 1 1 auto;
        }

        .repo-title-tools {
            width: 24px;
            font-size: 16px;
        }
    }

    .repo-branch {
        width: 100%;
        font-size: 16px;
        margin-top: 10px;
        margin-bottom: 8px;
    }

    .repo-search {
        width: 100%;
        margin-top: 10px;
    }

    .repo-tree {
        width: 100%;
        margin-top: 10px;
        height: calc(100vh - 216px);
    }
}

.repo-header {
    height: 100px;
    background: rgb(255, 255, 255);
    padding: 16px 16px 0px;
    display: flex;
    flex-direction: column;

    .repo-header-title {
        display: flex;
        flex-direction: row;
        gap: 5px;
        width: 100%;
        line-height: 40px;

        .branch-selector {
            font-size: 16px;
            width: 100px;
        }

        .file-path {
            flex: 1 1 auto;
            display: inline-flex;
            line-height: 40px;

            .file-path-title {
                font-size: 16px;
                // margin-left: 5px;
            }

            .open-icon {
                margin-left: 5px;
                line-height: 35px;
                margin-top: 5px;
            }
        }
    }




    .last-commit {
        width: 100%;
        line-height: 40px;
        display: inline-flex;
        padding: 0 16px 0 16px;
        flex-direction: row;
        -webkit-box-pack: justify;
        justify-content: space-between;
        -webkit-box-align: center;
        gap: 8px;
        min-width: 273px;
        border: 1px solid rgb(208, 215, 222);
        border-radius: 6px;

        .commit-author {
            flex: 0 0 200px;
            text-decoration: none;
            font-weight: 600;
            white-space: nowrap;
            color: rgb(31, 35, 40);
        }

        .commit-message {
            flex: 1 1 auto;
            font-size: 14px;
            -webkit-box-align: center;
            align-items: center;
        }

        .commit-shortId {
            flex: 0 0 80px;

            a {
                color: #2c3e50;

                &:hover {
                    text-decoration: underline;
                }
            }
        }

        .commit-date {
            flex: 0 0 50px;
            color: rgb(101, 109, 118);
            font-size: 12px;
        }
    }
}



.file-list {
    width: 100%;
    padding: 16px 16px 0px;

    a {
        color: #2c3e50;
    }
}

.file-content {
    width: 100%;
    height: calc(100vh - 160px);
    padding: 16px 16px 0px;

    .code-container {
        overflow: hidden;
        width: 100%;
        height: 99%;
        border: 1px solid rgb(208, 215, 222);
        border-radius: 6px;

        .code-editor {
            width: 100%;
            height: 100%
        }

        .download-area {
            text-align: center;
            padding-top: 6rem;
            padding-bottom: 6rem;

            a {
                color: #2c3e50;

                &:hover {
                    text-decoration: underline;
                }
            }
        }
    }
}
</style>