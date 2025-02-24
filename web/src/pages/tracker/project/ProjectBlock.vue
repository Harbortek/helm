<template>
    <a-card :loading="loading" hoverable bordered style="width: 260px;margin-left: 15px;margin-bottom: 15px;">
        <a-card-meta :title="project.name">
            <span slot="description" style="color: #87888a;font-size:12px;">
                最近工作项更新于{{ lastModifiedDate }}
            </span>
                <h-avatar slot="avatar" :name="project.owner.name" :icon="project.owner.icon" :isShowName="false"></h-avatar>
        </a-card-meta>
        <a-row style="margin-top: 5px;">
            <div :id="chartId" style="width:210px;height: 70px;"></div>
        </a-row>
        <a-row type="flex">
            <a-col flex="auto">
                <span style="color: #87888a;font-size:12px;">未完成工作项趋势</span>
            </a-col>
        </a-row>
    </a-card>
</template>

<script>
import HAvatar from '@/components/avatar/h-avatar.vue';
import { cardInfo } from "@/services/tracker/TrackerItemService";
import { TinyColumn, TinyLine, TinyArea } from "@antv/g2plot";
import { fromNow } from "@/utils/DateUtils"
export default {
    name: 'ProjectBlock',
    components:{HAvatar},
    props: {
        project: {
            type: Object,
            required: true
        },
        loading:false,
    },
    data() {
        return {
            lastModified: undefined,
            dailyTrend: []
        }
    },
    computed: {
        chartId() {
            return `chart-${this.project.id}`;
        },
        lastModifiedDate() {
            return fromNow(this.lastModified)
        }
    },
    mounted() {
        this.loadData();
    },
    methods: {
        loadData() {
            cardInfo(this.project.id).then(resp => {
                this.lastModified = resp.lastModified
                this.dailyTrend = resp.trendList;
                this.drawChart()
            })
        },
        drawChart() {
            let data = []
            this.dailyTrend.forEach(item => { data.push(item?.amount) })
            const linePlot = new TinyArea(this.chartId, {
                width: 210,
                height: 70,
                autoFit: false,
                smooth: true,
                data: data,
                // pattern: { type: 'line' },
                areaStyle: {
                    fill: '#409eff',
                    lineWidth: 0,
                    opacity: 0.5,
                },

            });
            linePlot.render();
           
        },
    }
}
</script>

<style></style>