<template>
    <div class="jobs-main" style="background-color: transparent;margin: 0 10px;">
        <a-page-header style="border: 1px solid rgb(235, 237, 240)" :title="'流水线:' + pipeline?.fullName"
            :sub-title="'#' + buildInfo?.number" @back="goBack">
            <a-row type="flex" align="middle">
                <a-col flex="30px">
                    <a-icon type="check-circle" theme="filled" style="font-size:24px;color:green;"
                        v-if="buildInfo?.result === 'SUCCESS'" />
                    <a-icon type="close-circle" theme="filled" style="font-size:24px;color:red;"
                        v-if="buildInfo?.result === 'FAILURE'" />
                </a-col>
                <a-col flex="200px" style="margin-left: 5px;">
                    <div>
                        <span v-if="buildInfo?.result === 'SUCCESS'">运行成功</span>
                        <span v-if="buildInfo?.result === 'FAILURE'">运行失败</span>
                    </div>
                    <div>{{ formatLongDate(buildInfo?.timestamp) }}</div>
                </a-col>
                <a-col flex="200px">
                    <div>
                        <span>运行人</span>
                    </div>
                    <div>{{ buildInfo?.buildUser }}</div>
                </a-col>
                <a-col flex="200px">
                    <div>
                        <span>持续时间</span>
                    </div>
                    <div>{{ formatElipse(buildInfo?.duration) }}</div>
                </a-col>
            </a-row>

        </a-page-header>
        <div class="section-title" style="margin-top: 1em;">
            <p>执行过程</p>
        </div>
        <div class="scroll-controller">
            <div class="graph-root-wrapper indiana-scroll-container">
                <a-spin :spinning="loading" style="width: 100%;height: 100%;margin-top: 80px;" />

                <div class="graph-root" id="stage-container" v-show="!loading">
                    <div class="full-log-entry"><a target="_blank" rel="noopener noreferrer" @click="onShowFullLog"
                            class="ci-link-3ittjKOl3g " style="font-size: 13px;">查看完整日志<h-icon type="open-link" /></a>
                    </div>
                    <div class="stage-group">
                        <div class="stage first-stage">
                            <div class="stage-box stage-box-first start" id="start">
                                <div class="stage-index">
                                    <a-icon type="check-circle" theme="filled" style="font-size:24px;color:green;" />
                                </div>
                                <div class="stage-name">开始</div>
                            </div>
                        </div>
                    </div>
                    <div class="stage-group" v-for="stage in stages" :key="stage.id">
                        <div :id="'stage-group-' + stage.id" class="stage last">
                            <div :id="'stage-' + stage.id" class="stage-box stage-box-first" @click="onShowStageLog(stage)">
                                <div class="stage-index">
                                    <a-icon type="check-circle" theme="filled" style="font-size:24px;color:green;"
                                        v-if="stage?.status === 'SUCCESS'" />
                                    <a-icon type="close-circle" theme="filled" style="font-size:24px;color:red;"
                                        v-if="stage?.status === 'FAILED'" />
                                </div>
                                <div class="stage-name"><a-tooltip :title="stage.name">{{ stage.name }}</a-tooltip></div>
                                <div class="stage-time"><span>{{ formatElipse(stage.durationMillis) }}</span></div>
                            </div>
                            <div :id="'step-' + step.id" class="step-box" v-for="step in stage.steps" :key="step.id"
                                @click="onShowStepLog(step)">
                                <div style="margin-right: 10px; display: flex; align-items: center;">
                                    <a-icon type="check" style="font-size:16px;color:green;"
                                        v-if="step?.status === 'SUCCESS'" />
                                    <a-icon type="close" style="font-size:16px;color:red;"
                                        v-if="step?.status === 'FAILED'" />
                                </div>
                                <div class="step-label"><a-tooltip :title="step.parameterDescription || step.name"> {{
                                    step.parameterDescription || step.name }}</a-tooltip></div>
                                <div class="step-time"><span>{{ formatElipse(step.durationMillis) }}</span></div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="section-title">
            <p>执行日志</p>
        </div>
        <pre class="log" style="flex: 1 1 auto;overflow: auto;">
            <div class="line" v-for="(line, index) in logLines" :key="index">
                <a class="line-number">{{ index + 1 }}</a>
                <span class="line-content" v-html="line"></span>
            </div>
        </pre>

    </div>
</template>

<script>
import Cookies from "js-cookie";
import { findExecutionStages, findExecutionLog } from '@/services/pipeline/PipelineService'
import { formatLongDate, formatElipse } from '@/utils/DateUtils'
import { AnsiUp } from 'ansi_up'
export default {
    name: 'PipelineJobs',
    components: {},
    data() {
        return {
            loading: false,
            pipeline: {},
            buildInfo: {},
            stages: [],
            logLines: [],
        }
    },
    computed: {

    },
    mounted() {
    },
    methods: {
        formatLongDate(d) {
            return formatLongDate(d)
        },
        formatElipse(d) {
            return formatElipse(d)
        },

        loadData(pipeline, buildInfo) {
            this.pipeline = pipeline
            this.buildInfo = buildInfo
            const repositoryId = pipeline.repositoryId
            this.loading = true
            this.stages = []
            findExecutionStages(repositoryId, pipeline, buildInfo).then(resp => {
                this.stages = resp;
                this.loading = false
            })
        },
        goBack() {
            this.$emit('close')
        },
        onShowStageLog(stage) {
            if (this.pipeline.branchJob) {
                return
            }
            findExecutionLog(this.pipeline.repositoryId, stage, null).then(resp => {
                let lines = resp.split('\n') || []
                let ansiUp = new AnsiUp()
                for (let i = 0; i < lines.length; i++) {
                    lines[i] = ansiUp.ansi_to_html(lines[i]) + '<br>'
                }
                this.logLines = lines
            })
        },
        onShowStepLog(step) {
            findExecutionLog(this.pipeline.repositoryId, null, step).then(resp => {
                let lines = resp.split('\n') || []
                let ansiUp = new AnsiUp()
                for (let i = 0; i < lines.length; i++) {
                    lines[i] = ansiUp.ansi_to_html(lines[i]) + '<br>'
                }
                this.logLines = lines
            })
        },
        onShowFullLog() {
            const accessToken = Cookies.get("Authorization");
            const repositoryId = this.pipeline.repositoryId
            const buildNo = this.buildInfo.number
            const pipelineName = this.pipeline.fullName
            const url = process.env.VUE_APP_API_BASE_URL + `/pipeline/job/fullLog/?access_token=${accessToken}&repositoryId=${repositoryId}&buildNo=${buildNo}&pipelineName=${pipelineName}`;
            console.log(url)
            window.open(url, '_blank')
        }
    },
}
</script>

<style lang="less" scoped>
.jobs-main {
    background-color: transparent;
    margin: 0px 10px;
    height: calc(100vh - 100px);
    display: flex;
    flex-direction: column;
}

.graph-root-wrapper {
    background-color: #f5f7fa;
    border: 1px solid #e6e9ed;
    border-radius: 2px;
    padding: 50px 0;
    -webkit-transition: margin-right .3s ease-in-out;
    -o-transition: margin-right .3s ease-in-out;
    transition: margin-right .3s ease-in-out;
    will-change: margin-right;
    cursor: move;
    margin-bottom: 24px;

    .graph-root {
        display: flex;
        color: #202d40;
        -ms-flex-align: start;
        align-items: flex-start;

        .stage-group {
            display: -ms-flexbox;
            display: flex;
            -ms-flex-direction: column;
            flex-direction: column;
            margin-right: 12px;

            &:last-child .stage:after {
                right: 50%;
                border-right: none;
            }

            .stage {
                position: relative;

                &:after {
                    content: "";
                    position: absolute;
                    top: 0;
                    right: 0;
                    bottom: 0;
                    left: 0;
                    -webkit-transform: translateY(30px);
                    -ms-transform: translateY(30px);
                    transform: translateY(30px);
                    border: 1px solid #b8c0cb;
                    border-top-color: transparent;
                }

                .stage-box {
                    border-radius: 2px;
                    margin: 10px 45px;
                    width: 220px;
                    height: 40px;
                    -ms-flex-pack: center;
                    justify-content: center;
                    -ms-flex-align: center;
                    align-items: center;
                    display: -ms-flexbox;
                    display: flex;
                    background-color: #fff;
                    border: 1px solid #dadfe6;
                    position: relative;
                    z-index: 9;
                    font-weight: 700;
                    -webkit-box-shadow: 0 2px 4px 0 rgba(0, 0, 0, .08);
                    box-shadow: 0 2px 4px 0 rgba(0, 0, 0, .08);
                    display: -ms-flexbox;
                    display: flex;
                    -ms-flex-pack: justify;
                    justify-content: space-between;
                    -ms-flex-align: center;
                    align-items: center;
                    cursor: pointer;

                    &:before {
                        content: "";
                        width: 0;
                        height: 0;
                        border-top: 5px solid transparent;
                        border-bottom: 5px solid transparent;
                        position: absolute;
                        left: 0;
                        top: 50%;
                        -webkit-transform: translate(-100%, -50%);
                        -ms-transform: translate(-100%, -50%);
                        transform: translate(-100%, -50%);
                        border-left: 5px solid #b8c0cb;
                    }

                    &.focus {
                        border-color: #3385ff
                    }

                    .stage-index {
                        height: 100%;
                        width: 40px;
                        background: #fff;
                        color: #3ac27d;
                        display: -ms-flexbox;
                        display: flex;
                        border-radius: 2px 0 0 2px;
                        -ms-flex-pack: center;
                        justify-content: center;
                        -ms-flex-align: center;
                        align-items: center;
                    }

                    .stage-name {
                        -ms-flex: 1 1;
                        flex: 1 1;
                        padding: 10px;
                        overflow: hidden;
                        white-space: nowrap;
                        -o-text-overflow: ellipsis;
                        text-overflow: ellipsis;
                    }

                    .stage-time {
                        color: #5f6c81;
                        padding: 10px;
                        overflow: hidden;
                        white-space: nowrap;
                        -o-text-overflow: ellipsis;
                        text-overflow: ellipsis;
                        font-weight: 400;
                    }
                }

                .step-box {
                    cursor: pointer;
                    display: -ms-flexbox;
                    display: flex;
                    -ms-flex-pack: justify;
                    justify-content: space-between;
                    padding: 10px;
                    -ms-flex-align: center;
                    align-items: center;
                    border-radius: 2px;
                    margin: 10px 45px;
                    width: 220px;
                    height: 40px;
                    -ms-flex-pack: center;
                    -webkit-box-pack: center;
                    justify-content: center;
                    -ms-flex-align: center;
                    -webkit-box-align: center;
                    align-items: center;
                    display: -ms-flexbox;
                    display: -webkit-box;
                    display: flex;
                    background-color: #fff;
                    border: 1px solid #dadfe6;
                    position: relative;
                    z-index: 9;

                    &:before {
                        content: "";
                        width: 1px;
                        height: 12px;
                        background: #dadfe6;
                        position: absolute;
                        bottom: 100%;
                        left: 15px;
                    }

                    &:after {
                        content: "";
                        width: 6px;
                        height: 6px;
                        position: absolute;
                        top: -3px;
                        left: 12px;
                        border: 1px solid #dadfe6;
                        border-radius: 50%;
                        background-color: #fff;
                    }

                    .step-label {
                        text-align: left;
                        -ms-flex: 1 1;
                        flex: 1 1;
                        overflow: hidden;
                        white-space: nowrap;
                        -o-text-overflow: ellipsis;
                        text-overflow: ellipsis;
                    }

                    .step-time {
                        color: #5f6c81;
                    }

                    &:hover {
                        border-color: #3385ff
                    }

                    &:hover .stage-icon {
                        opacity: 1
                    }

                    &.focus {
                        border-color: #3385ff
                    }
                }

                .stage-box.focus,
                .stage-box:hover {
                    border-color: #3385ff
                }

                .stage-box.start:before {
                    content: none
                }

                &.first-stage:after {
                    border-color: transparent;
                }

                &.last:after {
                    border-style: none;
                }
            }




            .stage.first-stage .stage-box {
                cursor: default;
            }

            .stage.last:after {
                border-style: none;
            }

            .stage-box.start {
                width: 100px;
            }

            .stage-box.stage-box-first:after {
                content: "";
                height: 1px;
                width: 120px;
                background: #b8c0cb;
                position: absolute;
                left: 100%;
                top: 50%;
            }

        }

        .stage-group:first-child .stage-box-first:before {
            content: none
        }

        .stage-group:last-child .stage-box-first:after {
            content: none
        }

        .stage-group:last-child .stage:after {
            right: 50%;
            border-right: none
        }
    }
}

.scroll-container {
    position: relative;
}

.indiana-scroll-container {
    overflow: auto;
}

.full-log-entry {
    position: absolute;
    right: 20px;
    -webkit-transform: translate(-30px, -30px);
    -ms-transform: translate(-30px, -30px);
    transform: translate(-30px, -30px);
    z-index: 10;
}

.section-title {
    font-size: 16px;
    font-weight: 700;
}

.log {
    background-color: #202d40;
    color: #f5f5f5;
    padding: 0.5rem 0;
    font-size: 12px;
    font-family: SFMono-Regular, Consolas, Liberation Mono, Menlo, Courier, monospace !important;
    margin: 0;
    white-space: pre-wrap;

    .line {
        display: -ms-flexbox;
        display: flex;
        -ms-flex-direction: row;
        flex-direction: row;
        line-height: 18px;

        .line-number {
            display: inline-block;
            min-width: 40px;
            color: #777;
            text-align: right;
            -webkit-user-select: none;
            -moz-user-select: none;
            -ms-user-select: none;
            user-select: none;
            height: 18px;
        }

        .line-content {
            padding-left: 15px;
            padding-right: 15px;
            max-width: 100%;
            word-break: break-word;
            text-wrap: nowrap;
            height: 18px;
        }
    }


}
</style>