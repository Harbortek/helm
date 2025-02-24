<template>
    <div class="page-props-panel">
        <div class="title-bar">
            <span class="title-bar-title">页面样式</span>
        </div>
        <a-row class="view-panel">
            <div style="overflow:auto;border-right: 1px solid #e6e6e6;height: 100%;width: 100%;padding-right: 0px"
                class="attr-style theme-border-class">
                <a-row class="de-collapse-style">
                    <!-- <span class="padding-lr">页面样式</span> -->
                    <a-collapse v-model="attrActiveNames" class="style-collapse" expandIconPosition="right">
                        <a-collapse-panel name="layout" header="布局">
                            <layout-selector :pageStyle="pageStyle" @change="layoutChange"></layout-selector>
                        </a-collapse-panel>

                        <a-collapse-panel name="background" header="背景">
                            <background-selector :pageStyle="pageStyle" @change="backgroundChange"></background-selector>
                        </a-collapse-panel>

                        <a-collapse-panel name="componentStyle" header="组件样式">
                            <component-style-selector :pageStyle="pageStyle"
                                @change="componentStyleChange"></component-style-selector>
                        </a-collapse-panel>
                    </a-collapse>
                </a-row>
            </div>
        </a-row>
    </div>
</template>

<script>
import BackgroundSelector from './BackgroundSelector.vue'
import LayoutSelector from './LayoutSelector.vue'
import ComponentStyleSelector from './ComponentStyleSelector.vue'

export default {
    name: "PagePropsPanel",
    components: {
        LayoutSelector, BackgroundSelector, ComponentStyleSelector
    },
    props: {
        pageStyle: {
            required: true,
        }
    },
    data() {
        return {
            attrActiveNames: [],
        }
    },
    mounted() {
    },
    methods: {
        pageStyleChange() {
            this.$emit('change', this.pageStyle)
        },
        layoutChange(layout) {
            this.pageStyle.layout = layout
            this.pageStyleChange()
        },
        backgroundChange(background) {
            this.pageStyle.background = background
            this.pageStyleChange()
        },
        componentStyleChange(componentStyle) {
            this.pageStyle.chartInfo.chartCommonStyle = componentStyle
            this.pageStyleChange()
        }
    },
}
</script>

<style lang="less">
.page-props-panel {
    width: 100%;
    height: 100%;
    background-color: #fff;

    .title-bar {
        height: 30px;
        display: flex;
        flex-direction: row;
        align-items: center;
        border-bottom: 1px solid #e0dbdb;

        .title-bar-title {
            flex: 1 1 auto;
            padding: 0 16px;
        }

        .title-bar-button {
            flex: 0 0;
            margin-right: 10px;
        }
    }

}

.de-collapse-style {
    ::v-deep.a-collapse-item__header {
        height: 34px !important;
        line-height: 34px !important;
        padding: 0 0 0 6px !important;
        font-size: 12px !important;
        font-weight: 400 !important;
    }
}

.padding-lr {
    padding: 0 6px;
}

.col {
    width: 40%;
    flex: 1;
    padding: 10px;
    border: solid 1px #eee;
    border-radius: 5px;
    float: left;
}

.col+.col {
    margin-left: 10px;
}

.view-panel {
    display: flex;
    // height: 100%;
    background-color: #f7f8fa;
    font-size: 12px;

    .ant-collapse-content>.ant-collapse-content-box {
        padding: 0px;
        flex-direction: row;
        display: flex;
    }

    .ant-collapse>.ant-collapse-item>.ant-collapse-header {
        font-size: 12px;
        font-weight: 500;
        color: #303030;
        padding-top: 4px !important;
        padding-bottom: 4px !important;
        margin-top: 0px;
        padding-left: 16px;
        padding-right: 20px;
        margin-right: 0px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        background-color: #fff;
    }

    .ant-collapse-icon-position-right>.ant-collapse-item>.ant-collapse-header {
        font-size: 12px;
    }

    span {
        font-size: 12px;
    }

    .ant-form {
        width: 100%;
    }

    .ant-form-item {
        margin-bottom: 0;
    }

    .ant-form label {
        font-size: 12px;
    }

    // .ant-form-item-label {
    //     display: inline-block;
    //     overflow: hidden;
    //     line-height: 28px;
    //     white-space: nowrap;
    //     text-align: right;
    //     vertical-align: middle;
    // }

    // .ant-form-item-control {
    //     position: relative;
    //     line-height: 28px;
    //     zoom: 1;
    // }

    .ant-select {
        font-size: 12px;
    }

    .ant-select-selection--single {
        position: relative;
        height: 28px;
        font-size: 12px;
        cursor: pointer;
    }

    .ant-select-selection__rendered {
        line-height: 28px;
        font-size: 12px;
    }

    .ant-select-arrow {
        color: #06B0FE;
        font-size: 12px;
        font-weight: 600;
    }

    .ant-select-dropdown {
        font-size: 12px;
    }

    .ant-input {
        font-size: 12px;
    }


    .cpicker {
        margin-top: 5px;
    }

    .ant-input-number {
        font-size: 12px;
        height: 22px;
        line-height: 22px;

        .ant-input-number-input {
            height: 100%;
        }
    }

}

.props-dropdown {
    .ant-select-dropdown-menu {
        font-size: 12px;
    }

    .ant-select-dropdown-menu-item {
        font-size: 12px !important;
    }
}

.blackTheme .view-panel {
    background-color: var(--MainBG);
}





.tab-header ::v-deep .a-tabs__header {
    border-top: solid 1px #eee;
    border-right: solid 1px #eee;
}

.tab-header ::v-deep .a-tabs__item {
    font-size: 12px;
    padding: 0 20px !important;
}

.blackTheme .tab-header ::v-deep .a-tabs__item {
    background-color: var(--MainBG);
}

.tab-header ::v-deep .a-tabs__nav-scroll {
    padding-left: 0 !important;
}

.tab-header ::v-deep .a-tabs__header {
    margin: 0 !important;
}

.tab-header ::v-deep .a-tabs__content {
    height: calc(100% - 40px);
}

.chart-icon {
    width: 20px;
    height: 20px;
}

.a-radio {
    margin: 5px;
}

.a-radio ::v-deep .a-radio__label {
    padding-left: 0;
}

.attr-style {
    height: calc(100vh - 56px - 60px - 40px - 40px);
}

.blackTheme .attr-style {
    color: var(--TextPrimary);
}

.attr-selector {
    width: 100%;
    height: 100%;
    margin: 6px 0;
    padding: 0 4px;
    display: flex;
    align-items: center;
    background-color: white
}

.blackTheme .attr-selector {

    background-color: var(--MainBG)
}

.dialog-css ::v-deep .a-modal__title {
    font-size: 14px;
}

.dialog-css ::v-deep .a-modal__header {
    padding: 20px 20px 0;
}

.dialog-css ::v-deep .a-modal__body {
    padding: 10px 20px 20px;
}

.blackTheme .theme-border-class {
    color: var(--TextPrimary) !important;
    background-color: var(--ContentBG);
}

.blackTheme .padding-lr {
    border-color: var(--TableBorderColor) !important;
}

.blackTheme .theme-item-class {
    background-color: var(--MainBG) !important;
    border-color: var(--TableBorderColor) !important;
}

.icon-class {
    color: #6c6c6c;
}

.blackTheme .icon-class {
    color: #cccccc;
}

.radio-span ::v-deep .a-radio__label {
    margin-left: 4px;
}

.view-title-name {
    display: -moz-inline-box;
    display: inline-block;
    width: 130px;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
    margin-left: 45px;
}

::v-deep .item-axis {
    width: 128px !important;
}

::v-deep .a-slider__input {
    width: 80px !important;
}

::v-deep .a-input-number--mini {
    width: 100px !important;
}

::v-deep .a-slider__runway.show-input {
    width: 80px !important;
}

.no-senior {
    width: 100%;
    text-align: center;
    font-size: 12px;
    padding-top: 40px;
    overflow: auto;
    border-right: 1px solid #e6e6e6;
    height: 100%;
}

.form-item-slider ::v-deep .a-form-model-item__label {
    font-size: 12px;
    line-height: 38px;
}

.form-item ::v-deep .a-form-model-item__label {
    font-size: 12px;
}

.no-properties {
    width: 100%;
    text-align: center;
    font-size: 12px;
    padding-top: 40px;
    overflow: auto;
    height: 100%;
}
</style>