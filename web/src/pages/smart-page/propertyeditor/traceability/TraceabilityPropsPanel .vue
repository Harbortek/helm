<template>
    <div class="prop-container">
        <div class="data-props-panel">
            <div>
                <div class="prop-name"><span>主工作项</span></div>
                <div class="prop-value">
                    <tracker-select ref="trackerSelect" :projectId="projectId" v-model="formData.mainTrackerId"
                        style="width: 100%;" size="small" @change="onMainTrackerChange" />
                </div>
            </div>
            <div>
                <div class="prop-name"><span>链接工作项</span></div>
                <div class="prop-value">
                    <tracker-select :projectId="projectId" v-model="formData.linkTrackerId" style="width: 100%;"
                        size="small" @change="onLinkTrackerChange" />
                </div>
            </div>
            <div>
                <div class="prop-name"><span>关联类型(链接工作项->主工作项)</span></div>
                <div class="prop-value">
                    <link-type-select :projectId="projectId" v-model="formData.linkTypeId" style="width: 100%;"
                        size="small" @change="emitDataChange" />
                </div>
            </div>

            <div>
                <div class="prop-name"><parameter-binding :pageId="pageId" :component="component" title="显示方式"
                        bindingProperty="showType" @binding="onBinding" />
                </div>

                <div class="prop-value">
                    <a-select v-model="formData.showType" style="width: 100%;" size="small"
                        :disabled="hasBinding('showType')" @change="emitDataChange">
                        <a-select-option value="SHOW_ALL">显示所有</a-select-option>
                        <a-select-option value="SHOW_LINKS">仅显示关联项</a-select-option>
                        <a-select-option value="SHOW_UNLINK">仅显示未关联项</a-select-option>
                        <a-select-option value="SHOW_PROBLEMS">仅显示问题项</a-select-option>
                    </a-select>
                </div>
            </div>

            <div>
                <div class="prop-name"><span>第二链接工作项</span></div>
                <div class="prop-value">
                    <tracker-select :projectId="projectId" v-model="formData.secondLinkTrackerId" style="width: 100%;"
                        size="small" @change="onSecondLinkTrackerChange" />
                </div>
            </div>
            <div>
                <div class="prop-name"><span>第二关联类型(第二链接工作项->链接工作项)</span></div>
                <div class="prop-value">
                    <link-type-select :projectId="projectId" v-model="formData.secondLinkTypeId" style="width: 100%;"
                        size="small" @change="emitDataChange" />
                </div>
            </div>

            <div>
                <div class="prop-name"><span>主工作项标题</span></div>
                <div class="prop-value">
                    <a-input size="small" v-model="formData.mainTrackerTitle" style="width: 100%;"
                        @change="emitDataChange" />
                </div>
            </div>

            <div>
                <div class="prop-name"><span>链接工作项标题</span></div>
                <div class="prop-value">
                    <a-input size="small" v-model="formData.linkTrackerTitle" style="width: 100%;"
                        @change="emitDataChange" />
                </div>
            </div>
            <div>
                <div class="prop-name"><span>第二链接工作项标题</span></div>
                <div class="prop-value">
                    <a-input size="small" v-model="formData.secondLinkTrackerTitle" style="width: 100%;"
                        @change="emitDataChange" />
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import { deepCopy } from '@/pages/smart-page/util/canvasUtils'
import TrackerSelect from '@/components/select/TrackerSelect.vue'
import LinkTypeSelect from '@/components/select/LinkTypeSelect.vue'
export default {
    name: 'TraceabilityPropsPanel',
    components: { TrackerSelect, LinkTypeSelect },
    props: {
        pageId: {
            type: String,
        },
        component: {
            type: Object,
            required: false
        }
    },
    data() {
        return {
            formData: {
                mainTrackerId: '',
                linkTrackerId: '',
                secondLinkTrackerId: '',
                linkTypeId: '',
                secondLinkTypeId: '',
                showType: 'SHOW_ALL',
                mainTrackerTitle: '',
                linkTrackerTitle: '',
                secondLinkTrackerTitle: '',
            }
        }
    },
    computed: {
        projectId() {
            return this.$route.params.projectId;
        }
    },
    watch: {
        component: {
            handler: function (newVal, oldVale) {
                this.prepareFormData(newVal)
            },
            immediate: true,
        }
    },
    mounted() {

    },
    methods: {
        prepareFormData(newVal) {
            this.formData = deepCopy(newVal)
            this.formData.mainTrackerId = newVal.mainTrackerId || ''
            this.formData.linkTrackerId = newVal.linkTrackerId || ''
            this.formData.secondLinkTrackerId = newVal.secondLinkTrackerId || ''
            this.formData.linkTypeId = newVal.linkTypeId || ''
            this.formData.secondLinkTypeId = newVal.secondLinkTypeId || ''
            this.formData.showType = newVal.showType || 'SHOW_ALL'
            this.formData.mainTrackerTitle = newVal.mainTrackerTitle || ''
            this.formData.linkTrackerTitle = newVal.linkTrackerTitle || ''
            this.formData.secondLinkTrackerTitle = newVal.secondLinkTrackerTitle || ''
            this.formData.bindingParameters = newVal.bindingParameters || []
        },
        emitDataChange() {
            let comp = deepCopy(this.formData)
            this.$emit('change', comp)
        },
        onMainTrackerChange(v) {
            this.formData.mainTrackerTitle = this.$refs.trackerSelect.getTrackerName(v)
            this.emitDataChange()
        },
        onLinkTrackerChange(v) {
            this.formData.linkTrackerTitle = this.$refs.trackerSelect.getTrackerName(v)
            this.emitDataChange()
        },
        onSecondLinkTrackerChange(v) {
            this.formData.secondLinkTrackerTitle = this.$refs.trackerSelect.getTrackerName(v)
            this.emitDataChange()
        },
        onBinding(property, parameter) {
            this.formData.bindingParameters = this.formData.bindingParameters.filter(item => item.propertyName !== property)
            this.formData.bindingParameters.push({
                propertyName: property,
                parameterName: parameter
            })
            this.emitDataChange()
        },
        hasBinding(property) {
            const reuslt = this.formData.bindingParameters.find(item => item.propertyName === property) || []
            return reuslt.length > 0
        }
    },
}
</script>

<style lang="less" scoped>
.data-props-panel {
    width: 100%;
    height: 100%;
    padding-left: 10px;
    padding-right: 10px;

    .prop-name {
        width: 100%;
        height: 22px;
        margin-top: 15px;
        font-size: 12px;
        line-height: 12px;
        text-align: left;
        position: relative;
        width: 100%;
        display: inline-block;
    }

    .prop-value {
        width: 100%;
        min-height: 26px;
    }
}
</style>