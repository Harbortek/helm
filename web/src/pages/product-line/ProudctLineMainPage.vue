<template>
    <div style="margin-top: 0px;" class="project-main">
        <teleport to="#topMenuComponenet">
            <a-row type="flex" class="project-top-menu">
                <a-col flex="150px">
                    <a-dropdown :trigger="['click']" v-model="showDropdown">
                        <a style="white-space:nowrap" class="ant-dropdown-link" @click="e => e.preventDefault()">
                            产品线：{{ currentProductLine.name }} <a-icon type="down" />
                        </a>
                        <div slot="overlay">
                            <div @click.stop>
                                <a-menu class="project-list-dropdown" :selectable="true"
                                    style="padding-left: 10px;padding-right: 10px;padding-top: 10px;"
                                    @click="gotoProductLine">
                                    <a-menu-item v-for="p in  productLines" :key="p.id">
                                        {{ p.name }}
                                    </a-menu-item>
                                </a-menu>
                            </div>
                        </div>
                    </a-dropdown>
                </a-col>
                <a-col flex="auto" style="height: 48px;">
                    <span class="top-menu">
                        <router-link :to="item.path" :key="index" v-for="(item, index) in menuItems">
                            <div class="top-menu-item">
                                <a-icon v-if="item.icon" :type="item.icon" />
                                {{ $t(item.title) }}
                            </div>
                        </router-link>
                    </span>
                </a-col>
            </a-row>
        </teleport>
        <a-row v-show="thirdMenus && thirdMenus.length > 0" type="flex" class="third-menu-bar" justify="center">
            <a-col flex="auto" style="height: 40px;">
                <div class="third-menu-container">
                    <router-link :to="item.path" :key="index" v-for="(item, index) in thirdMenus">
                        <div class="third-menu-item">
                            <a-icon v-if="item.icon" :type="item.icon" />
                            {{ $t(item.title) }}
                        </div>
                    </router-link>
                </div>
            </a-col>
        </a-row>
        <a-row style="height: 100vh;">
            <router-view style="height:100%" />
        </a-row>
    </div>
</template>

<script>
import Teleport from 'vue2-teleport';
import { findProductLines } from '@/services/product-line/ProductLineService';

export default {
    name: "ProductLineMainPage",
    components: { Teleport },
    data() {
        return {
            currentProductLine: { name: '需求管理项目' },
            productLines: [],
            keyword: '',
            menuItems: [],
            showDropdown: false,
        };
    },
    computed: {
        productLineId() {
            return this.$route.params.productLineId
        },
        thirdMenus() {
            console.log(this.$route.matched)
            let all = [];
            if (this.$route.matched.length > 0) {
                let funcName = this.$route.matched[3].name;
                if (funcName == 'trackerGroup') {
                    funcName = 'trackerGroup-' + this.$route.params.groupId;
                }
                for (let i = 0; i < this.menuItems.length; i++) {
                    if (this.menuItems[i].name === funcName) {
                        return this.menuItems[i].children || []
                    }
                }
            }
            return all;
        },
    },
    mounted() {
        this.buildNavgation()
        this.loadData()
    },
    beforeDestroy() {
        document.getElementById("topMenuComponenet").innerHTML = '';
    },
    methods: {
        loadData() {
            findProductLines().then(resp => {
                this.productLines = resp
                this.productLines.forEach(item => {
                    if (item.id == this.productLineId) {
                        this.currentProductLine = item
                    }
                })
            })
        },
        buildNavgation() {
            this.menuItems = []
            let index = 0
            this.menuItems.push({ id: index++, name: 'products', title: '产品清单', path: this.url('/products') })

            this.menuItems.push({
                id: index++, name: 'reports', title: '报表', path: this.url('/reports'), icon: 'bar-chart'
            })

            this.menuItems.push({
                id: index++, name: 'productConfig', title: '设置', path: this.url('/config'), icon: 'setting'
            })
        },
        url(path) {
            return '/productline/' + this.productLineId + path
        },
        gotoProductLine({ key }) {
            console.log('gotoProductLine', key)
            let currentPath = this.$route.path
            if (currentPath.indexOf('/productline/') >= 0) {
                currentPath = currentPath.replace(this.productLineId, key)
            }
            console.log('currentPath', currentPath)
            this.showDropdown = false
            this.$router.push({ path: currentPath })
        }
    },
};
</script>

<style lang="less">
.project-top-menu {

    padding-left: 10px;

    .ant-menu-horizontal>.ant-menu-item:hover,
    .ant-menu-horizontal>.ant-menu-item-active {
        color: #338fe5;
        border-top: 2px solid #338fe5 !important;
        border-bottom: 0px !important;
    }

    .ant-menu-horizontal>.ant-menu-item-selected {
        color: #338fe5;
        border-top: 2px solid #338fe5 !important;
        border-bottom: 0px !important;
    }

    .ant-menu-horizontal>.ant-menu-item-open {
        color: #338fe5;
        border-top: 2px solid #338fe5 !important;
        border-bottom: 0px !important;
    }

}

.top-menu {
    color: #606060;

    .router-link-active {
        .top-menu-item {
            border-top: 3px solid #338fe5;
            background-color: #f8f8f8;
            transition: all 0.2s;
            color: #338fe5;
            font-size: 500;
        }
    }

    &:first-child {
        margin-left: 10px;
    }

    .top-menu-item {
        display: inline-block;
        color: #606060;
        max-width: 230px;
        height: 48px;
        border-top: 3px solid transparent;
        border-bottom: 3px solid transparent;
        font-weight: 500;
        padding: 0 15px;

        background: transparent;

        &:hover {
            color: #338fe5;
            font-weight: 500;
            // border-top: 3px solid #338fe5;
        }

        &::before {
            position: absolute;
            content: "";
            height: 20px;
            left: 0;
            width: 0px;
            background: #c7c7c7;
            top: 50%;
            margin-top: -10px;
        }
    }
}

.project-list-dropdown {
    .ant-dropdown-link {
        color: #606060;
    }

    .ant-menu-item-group {
        .ant-menu-item-group-title {
            font-size: 12px;
            color: #606060
        }

        .ant-menu-item-group-list {
            .ant-menu-item {
                font-size: 14px;

            }

            .ant-menu-vertical .ant-menu-item {
                margin-top: 0px;
                margin-bottom: 0px;
                height: 26px !important;
                line-height: 26px !important;
            }
        }
    }
}

.ant-menu-horizontal>.ant-menu-item {
    position: relative;
    top: 1px;
    display: inline-block;
    vertical-align: bottom;
    border-top: 2px solid transparent;
    border-bottom: 0px !important;
}



.third-menu-bar {
    z-index: 2;
    justify-content: center;
    background: #f8f8f8;
    box-shadow: 0 2px 4px 0 #d3d3d3;
    height: 40px;
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: stretch;
    position: relative;
    flex: 0 0 40px;

    .third-menu-container {
        font-size: 15px;
        height: 40px;
        white-space: nowrap;
        display: flex;
        flex-wrap: wrap;
        overflow: hidden;
        height: 40px;
        align-items: center;
        justify-content: center;
        vertical-align: middle;

        .router-link-active {
            .third-menu-item {
                color: #338fe5;
                background-color: rgba(51, 143, 229, .12);
            }
        }

        .third-menu-item {
            display: inline-block;
            border: none;
            color: #606060;
            background: transparent;
            max-width: 230px;
            height: 32px;
            // border-top: 3px solid transparent;
            // border-bottom: 3px solid transparent;
            font-weight: 400;
            padding: 5px 15px;
            margin-top: 1px;
            // margin-bottom: 8px;

            background: transparent;

            &:hover {
                color: #338fe5;
                font-weight: 400;
            }

            &::before {
                position: absolute;
                content: "";
                height: 20px;
                left: 0;
                width: 0px;
                background: #c7c7c7;
                top: 50%;

            }
        }
    }


}
</style>
