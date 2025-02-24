export default {
    eachPeriod: {   
        eachField: {
            empty: '每 {{field.id}}',
            value: '{{value.text}}',
            range: '{{start.text}}-{{end.text}}',
            everyX: '所有 {{every.value}}'
        },
        monthField: {
            prefix: '的',
            value: '{{value.alt}}',
            range: '{{start.alt}}-{{end.alt}}',
        },
        dayField: {
            prefix: '的'
        },
        dayOfWeekField: {
            prefix: '的',
            empty: '一周的每一天',
            value: '{{value.alt}}',
            range: '{{start.alt}}-{{end.alt}}',
        },
        hourField: {
            prefix: '的'
        },
        minuteField: {
            prefix: ':'
        }
    },
    hourPeriod: {
        minuteField: {
            prefix: '的',
            suffix: '分钟',
            empty: '每'
        }
    },
    monthPeriod: {
        dayOfWeekField: {
            prefix: '和'
        }
    },
    yearPeriod: {
        dayOfWeekField: {
            prefix: '和'
        }
    },
    periodPrefix: '每',
    periodSuffix: ''
}