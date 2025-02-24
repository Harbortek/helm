<template>
    <a-modal v-model="visiable" :title="$t('config.codeRepository.associate.codeRepository')" @ok="onOK" @cancel="onCancel" centered :width="600" dialogClass="tool-dialog"
        :okText="$t('next')">
        <a-list item-layout="horizontal" :data-source="repositoryTemplates" class="tool-list">
            <a-list-item slot="renderItem" slot-scope="item,index">
                <div @click="onSelect(index)" style="position: relative;"
                    :class="selectIndex === index ? 'tool-item tool-selected' : 'tool-item'">
                    <a-list-item-meta :description="item.description">
                        <span slot="title" class="tool-title">{{ item.title }}</span>
                        <a-avatar slot="avatar" shape="square" :size="64" class="tool-icon">
                            <h-icon slot="icon" :type="item.icon" />
                        </a-avatar>
                    </a-list-item-meta>
                    <a-icon type="check-circle" theme="filled"
                        style="position: absolute;top: 5px;left: 45px;color: #0064ff;" v-show="selectIndex === index" />
                </div>
            </a-list-item>
        </a-list>
    </a-modal>
</template>

<script>
export default {
    name: 'CreateRepositoryDialog',
    props: {
        isShowDialog: {
            required: true
        },
        editMode: {
            required: true
        },

    },
    data() {
        return {
            repositoryTemplates: [{
                id: 'gitlab',
                icon: 'gitlab',
                title: this.$t('config.codeRepository.type.gitlab'),
                description: this.$t('config.codeRepository.modal.gitlab'),
            }, {
                id: 'svn',
                icon: 'svn',
                title: 'SVN',
                description: this.$t('config.codeRepository.modal.svn'),
            }, {
                id: 'bitbucket',
                icon: 'bitbucket',
                title: this.$t('config.codeRepository.type.bitbucket'),
                description: this.$t('config.codeRepository.modal.bitbucket'),
            }],
            selectIndex: 0,
        }
    },
    computed: {
        visiable: {
            get() {
                return this.isShowDialog
            },
            set(newValue) {
                return newValue
            }
        },

    },
    watch: {
        isShowDialog: {
            handler: function (newVal, oldVal) {
                if (newVal) {
                    this.loadData();
                }

            }
        }
    },
    methods: {
        loadData() {

        },
        onSelect(index) {
            this.selectIndex = index
            console.log(index)
        },
        onOK() {
            const type = this.repositoryTemplates[this.selectIndex].id
            this.$emit('ok', type)
        },
        onCancel() {
            this.$emit('cancel')
        }

    },
}
</script>

<style lang="less">
.ant-modal.tool-dialog {
    .ant-modal-content {
        .ant-modal-body {
            padding-left: 0;
            padding-right: 0;
        }
    }

}

.tool-list {
    .ant-list-item {
        padding: 20px;

        &:hover {
            background-color: #f0f6ff;
        }
    }
}

.tool-item {
    &:hover {
        background-color: #f0f6ff;
    }

    .tool-icon {
        background-color: transparent;
        border: solid 1px #bebfc2;
    }
}

.tool-item.tool-selected {
    .tool-icon {
        background-color: transparent;
        border: solid 1px #0064ff;
    }

    .tool-title {
        color: #0064ff;
    }
}
</style>