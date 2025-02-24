<template>
    <div style="width: 100%;height: 100%;">
        <a-tabs :default-active-key="tabs[0]" :activeKey="currentTab" @change="onTabChange">
            <a-tab-pane :key="tab" :tab="$t('chart.tab_title_' + tab)" v-for="tab in tabs" :forceRender="true">
                <style-props-panel v-if="tab === 'STYLE'" :pageId="pageId" :component="component"
                    @change="componentStyleChange" />
            </a-tab-pane>
        </a-tabs>
    </div>
</template>

<script>
import StylePropsPanel from '@/pages/smart-page/propertyeditor/chart/StylePropsPanel.vue';
export default {
    name: 'StylePropertyEditor',
    components: { StylePropsPanel },
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
            currentTab: 'STYLE',
            tabs: ['STYLE'],
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