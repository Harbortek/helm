<template>
    <div class="ui-task-primary-fields">
        <span class="task-basic-quick-picker">
            <div class="basic-info-model">
                <div class="basic-info-model-icon">
                    <slot name="icon"></slot>
                </div>

                <div class="basic-info-model-content">
                    <span class="basic-info-model-content-title">
                        <a-tooltip> <template slot="title">
                                {{ title }}
                            </template>{{ title }}</a-tooltip>
                    </span>
                    <div class="basic-info-model-content-subTitle"><span class="">{{ subTitle }}</span></div>
                </div>
            </div>
        </span>
    </div>
</template>
<script>

export default {
    name: 'QuickPicker',
    components: {},
    props: ['title', 'subTitle'],
    data() {
        return {
        }
    },
    computed: {

    }
}
</script>
<style lang="less" scoped>
.ui-task-primary-fields {
    width: auto;
    margin-right: 10px;
    flex: 0 0 auto;
    min-width: 120px;
    max-width: 200px;

    .task-basic-quick-picker {
        flex: 1;

        .basic-info-model {
            flex: 1;
            display: flex;
            padding: 4px;
            cursor: pointer;
            align-items: center;
            transition: background-color .2s;
            border-radius: 30px;
            margin-right: 15px;

            .basic-info-model-icon {
                width: 30px;
                height: 30px;
                display: flex;
                align-items: center;
                justify-content: center;
                background-color: #e8e8e8;
                border-radius: 50%;
                font-size: 16px;
                color: #606060;
            }

            .basic-info-model-content {
                overflow: hidden;
                padding-right: 10px;
                margin-left: 10px;
                display: flex;
                flex-direction: column;

                .basic-info-model-content-title {
                    min-width: 80px;
                    overflow: hidden;
                    text-overflow: ellipsis;
                    white-space: nowrap;
                    font-size: 15px;
                    color: #303030;
                    font-weight: 500;
                    line-height: 24px;
                }

                .basic-info-model-content-subTitle {
                    line-height: 16px;
                    margin-top: 2px;
                    height: 16px;
                    display: inline-block;
                    font-size: 12px;
                    color: #909090;
                    font-weight: 500;
                }
            }
        }
    }
}
</style>
