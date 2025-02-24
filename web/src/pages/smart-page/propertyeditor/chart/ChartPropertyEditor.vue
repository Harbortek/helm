<template>
    <div style="width: 100%;height: 100%;">
        <a-tabs :default-active-key="tabs[0]" :activeKey="currentTab" @change="onTabChange">
            <a-tab-pane :key="tab" :tab="$t('chart.tab_title_' + tab)" v-for="tab in tabs" :forceRender="true">
                <data-props-panel ref="dataPanel" v-if="tab === 'DATA'" :pageId="pageId" :component="component"
                    @change="componentDataChange" />
                <style-props-panel v-if="tab === 'STYLE'" :pageId="pageId" :component="component"
                    @change="componentStyleChange" />
                <advanced-props-panel v-if="tab === 'ADVANCE'" :pageId="pageId" :component="component" />
            </a-tab-pane>
        </a-tabs>
    </div>
</template>

<script>
import DataPropsPanel from './DataPropsPanel.vue';
import StylePropsPanel from './StylePropsPanel.vue';
import AdvancedPropsPanel from './AdvancedPropsPanel.vue';
export default {
    name: 'ChartPropertyEditor',
    components: { DataPropsPanel, StylePropsPanel, AdvancedPropsPanel },
    props: {
        pageId: {
            required: true
        },
        component: {
            required: false,
        }
    },
    data() {
        return {
            currentTab: 'DATA',
            tabs: ['DATA', 'STYLE', 'ADVANCE'],
        }
    },
    mounted() {

    },
    methods: {
        onTabChange(activeKey) {
            this.currentTab = activeKey
        },
        componentDataChange(comp) {
            this.$emit('change', comp)
        },
        componentStyleChange(comp) {
            this.$emit('change', comp)
        }
    },
}
</script>

<style></style>