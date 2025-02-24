export default {
    eachPeriod: {   
        eachField: {
            empty: '每 {{field.id}}',
            value: '{{value.text}}',
            range: '{{start.text}}-{{end.text}}',
            everyX: 'every {{every.value}}'
        },
        monthField: {
            prefix: 'in',
            value: '{{value.alt}}',
            range: '{{start.alt}}-{{end.alt}}',
        },
        dayField: {
            prefix: 'on'
        },
        dayOfWeekField: {
            prefix: 'on',
            empty: '星期的每一天',
            value: '{{value.alt}}',
            range: '{{start.alt}}-{{end.alt}}',
        },
        hourField: {
            prefix: 'at'
        },
        minuteField: {
            prefix: ':'
        }
    },
    hourPeriod: {
        minuteField: {
            prefix: 'at',
            suffix: 'minute(s)',
            empty: 'every'
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