<template>
    <div class="dashboard-landing">
        <div :style="{ background: `#000d4a url(${bgUrl}) center top` }">
            <div class="loading" ref="loadingEl">
                <div class="loadbox"><img :src="loadingGifUrl"> 页面加载中...</div>
            </div>
            <div class="back"></div>
            <div class="head">
                <div class="weather"><span id="showTime">{{ showTime }}</span></div>
                <h1>广西大学东盟语料收集与管理平台可视化展示</h1>
            </div>
            <div class="mainbox">
                <ul class="clearfix">
                    <li>
                        <div class="boxall" style="height:400px;">
                            <div class="alltitle">标题样式</div>
                            <div class="navboxall" id="echart5"></div>
                        </div>
                        <div class="boxall" style="height:260px;">
                            <div class="alltitle">标题样式</div>
                            <div class="navboxall">
                                <div class="wraptit">
                                    <span>单号</span><span>金额</span><span>品名</span><span>时间</span>
                                </div>
                                <div class="wrap">
                                    <ul>
                                        <li>
                                            <p><span>100021415</span><span>199</span><span>手机</span><span>18小时</span>
                                            </p>
                                        </li>
                                        <li>
                                            <p><span>100021415</span><span>199</span><span>手机</span><span>18小时</span>
                                            </p>
                                        </li>
                                        <li>
                                            <p><span>100021415</span><span>199</span><span>手机</span><span>18小时</span>
                                            </p>
                                        </li>
                                        <li>
                                            <p><span>100021415</span><span>199</span><span>手机</span><span>18小时</span>
                                            </p>
                                        </li>
                                        <li>
                                            <p><span>100021415</span><span>199</span><span>手机</span><span>18小时</span>
                                            </p>
                                        </li>
                                        <li>
                                            <p><span>100021415</span><span>199</span><span>手机</span><span>18小时</span>
                                            </p>
                                        </li>
                                        <li>
                                            <p><span>100021415</span><span>199</span><span>手机</span><span>18小时</span>
                                            </p>
                                        </li>
                                        <li>
                                            <p><span>100021415</span><span>199</span><span>手机</span><span>18小时</span>
                                            </p>
                                        </li>
                                        <li>
                                            <p><span>100021415</span><span>199</span><span>手机</span><span>18小时</span>
                                            </p>
                                        </li>
                                    </ul>
                                </div>

                            </div>
                        </div>
                        <div class="boxall" style="height:260px;">
                            <div class="alltitle">标题样式</div>

                            <div class="navboxall" id="echart1"></div>
                        </div>
                    </li>
                    <li>

                        <div class="boxall" style="height:230px">
                            <div class="clearfix navboxall" style="height: 100%">
                                <div class="pulll_left num">
                                    <div class="numbt">语料库总览</div>
                                    <div class="numtxt">19382721</div>
                                </div>
                                <div class="pulll_right zhibiao">
                                    <div class="zb1"><span>标题样式</span>
                                        <div id="zb1"></div>
                                    </div>
                                    <div class="zb2"><span>标题样式</span>
                                        <div id="zb2"></div>
                                    </div>
                                    <div class="zb3"><span>标题样式</span>
                                        <div id="zb3"></div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="boxall" style="height:350px">
                            <div class="alltitle">标题样式</div>
                            <div class="navboxall" id="echart4"></div>

                        </div>
                        <div class="boxall" style="height:340px">
                            <div class="alltitle">标题样式</div>
                            <div class="navboxall" id="echart3"></div>
                        </div>
                    </li>
                    <li>
                        <div class="boxall" style="height:300px">
                            <div class="alltitle">标题样式</div>
                            <div class="navboxall">
                                <table class="table1" width="100%" border="0" cellspacing="0" cellpadding="0">
                                    <tbody>
                                        <tr>
                                            <th scope="col">排名</th>
                                            <th scope="col">公司</th>
                                            <th scope="col">数量</th>
                                            <th scope="col">增长率</th>
                                        </tr>
                                        <tr>
                                            <td><span>1</span></td>
                                            <td>腾讯科技</td>
                                            <td>114万<br></td>
                                            <td>100%<br></td>
                                        </tr>
                                        <tr>
                                            <td><span>2</span></td>
                                            <td>百度公司</td>
                                            <td>923823万</td>
                                            <td>21%</td>
                                        </tr>

                                        <tr>
                                            <td><span>3</span></td>
                                            <td>新浪</td>
                                            <td>1240253万</td>
                                            <td>12%</td>
                                        </tr>
                                        <tr>
                                            <td><span>4</span></td>
                                            <td>网易</td>
                                            <td>1.2亿</td>
                                            <td>39%</td>
                                        </tr>
                                        <tr>
                                            <td><span>5</span></td>
                                            <td>雅虎</td>
                                            <td>13423万</td>
                                            <td>9%</td>
                                        </tr>

                                    </tbody>
                                </table>
                            </div>
                        </div>

                        <div class="boxall" style="height: 300px">
                            <div class="alltitle">标题样式</div>
                            <div class="navboxall">
                                <table class="table1" width="100%" border="0" cellspacing="0" cellpadding="0">
                                    <tbody>
                                        <tr>
                                            <th scope="col">排名</th>
                                            <th scope="col">公司</th>
                                            <th scope="col">数量</th>
                                            <th scope="col">增长率</th>
                                        </tr>
                                        <tr>
                                            <td><span>1</span></td>
                                            <td>腾讯科技</td>
                                            <td>114万<br></td>
                                            <td>100%<br></td>
                                        </tr>
                                        <tr>
                                            <td><span>2</span></td>
                                            <td>百度公司</td>
                                            <td>923823万</td>
                                            <td>21%</td>
                                        </tr>

                                        <tr>
                                            <td><span>3</span></td>
                                            <td>新浪</td>
                                            <td>1240253万</td>
                                            <td>12%</td>
                                        </tr>
                                        <tr>
                                            <td><span>4</span></td>
                                            <td>网易</td>
                                            <td>1.2亿</td>
                                            <td>39%</td>
                                        </tr>
                                        <tr>
                                            <td><span>5</span></td>
                                            <td>雅虎</td>
                                            <td>13423万</td>
                                            <td>9%</td>
                                        </tr>

                                    </tbody>
                                </table>
                            </div>

                        </div>

                        <div class="boxall" style="height:320px">
                            <div class="alltitle">标题样式</div>
                            <div class="navboxall" id="echart2"></div>
                        </div>


                    </li>
                </ul>

            </div>
        </div>
    </div>
</template>

<script setup>
import { onMounted, onBeforeUnmount, ref } from 'vue'
import * as echarts from 'echarts'
import '@/assets/comon0.css'

const showTime = ref('')
const loadingEl = ref(null)

const bgUrl = new URL('@/images/bg.jpg', import.meta.url).href
const loadingGifUrl = new URL('@/images/loading.gif', import.meta.url).href

let intervalId = null
let resizeHandlers = []

function updateClock() {
    const dt = new Date()
    const y = dt.getFullYear()
    const mt = dt.getMonth() + 1
    const day = dt.getDate()
    const h = dt.getHours()
    const m = dt.getMinutes()
    const s = dt.getSeconds()
    showTime.value = `${y}年${mt}月${day}日${h}时${m}分${s}秒`
}

function initResizeFor(chart) {
    const handler = () => chart.resize()
    window.addEventListener('resize', handler)
    resizeHandlers.push({ chart, handler })
}

function echarts_1() {
    const el = document.getElementById('echart1')
    if (!el) return
    const myChart = echarts.init(el)
    const option = {
        tooltip: { trigger: 'item', formatter: '{b} : {c} ({d}%)' },
        legend: {
            right: 0,
            top: 30,
            height: 160,
            itemWidth: 10,
            itemHeight: 10,
            itemGap: 10,
            textStyle: { color: 'rgba(255,255,255,.6)', fontSize: 12 },
            orient: 'vertical',
            data: ['图例1', '图例2', '图例3', '图例4', '图例5']
        },
        calculable: true,
        series: [
            {
                name: ' ',
                color: ['#62c98d', '#2f89cf', '#4cb9cf', '#53b666', '#62c98d', '#205acf', '#c9c862', '#c98b62', '#c962b9', '#7562c9', '#c96262', '#c25775', '#00b7be'],
                type: 'pie',
                radius: [30, 70],
                center: ['35%', '50%'],
                roseType: 'radius',
                label: { normal: { show: true }, emphasis: { show: true } },
                lableLine: { normal: { show: true }, emphasis: { show: true } },
                data: [
                    { value: 10, name: '图例1' },
                    { value: 5, name: '图例2' },
                    { value: 15, name: '图例3' },
                    { value: 25, name: '图例4' },
                    { value: 20, name: '图例5' },
                ]
            }
        ]
    }
    myChart.setOption(option)
    initResizeFor(myChart)
}

function echarts_2() {
    const el = document.getElementById('echart2')
    if (!el) return
    const myChart = echarts.init(el)
    const option = {
        tooltip: { trigger: 'item', formatter: '{b} : {c} ({d}%)' },
        legend: {
            top: '15%',
            data: ['图例1', '图例2', '图例3', '图例4', '图例5'],
            icon: 'circle',
            textStyle: { color: 'rgba(255,255,255,.6)' }
        },
        calculable: true,
        series: [{
            name: '',
            color: ['#62c98d', '#2f89cf', '#4cb9cf', '#53b666', '#62c98d', '#205acf', '#c9c862', '#c98b62', '#c962b9', '#c96262'],
            type: 'pie',
            startAngle: 0,
            radius: [51, 100],
            center: ['50%', '45%'],
            roseType: 'area',
            avoidLabelOverlap: false,
            label: { normal: { show: true }, emphasis: { show: true } },
            labelLine: { normal: { show: true, length2: 1 }, emphasis: { show: true } },
            data: [
                { value: 1, name: '图例1' },
                { value: 4, name: '图例2' },
                { value: 5, name: '图例3' },
                { value: 6, name: '图例4' },
                { value: 9, name: '图例5' },
                { value: 0, name: '', label: { show: false }, labelLine: { show: false } },
                { value: 0, name: '', label: { show: false }, labelLine: { show: false } },
                { value: 0, name: '', label: { show: false }, labelLine: { show: false } },
                { value: 0, name: '', label: { show: false }, labelLine: { show: false } },
                { value: 0, name: '', label: { show: false }, labelLine: { show: false } },
            ]
        }]
    }
    myChart.setOption(option)
    initResizeFor(myChart)
}

function echarts_3() {
    const el = document.getElementById('echart3')
    if (!el) return
    const myChart = echarts.init(el)
    const option = {
        tooltip: { trigger: 'axis', axisPointer: { lineStyle: { color: '#57617B' } } },
        legend: {
            data: ['销售额', '利润'],
            top: '0',
            textStyle: { color: '#fff' },
            itemGap: 20,
        },
        grid: { left: '0', right: '20', top: '10', bottom: '20', containLabel: true },
        xAxis: [{
            type: 'category',
            boundaryGap: false,
            axisLabel: { show: true, textStyle: { color: 'rgba(255,255,255,.6)' } },
            axisLine: { lineStyle: { color: 'rgba(255,255,255,.1)' } },
            data: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
        }, {}],
        yAxis: [{
            axisLabel: { show: true, textStyle: { color: 'rgba(255,255,255,.6)' } },
            axisLine: { lineStyle: { color: 'rgba(255,255,255,.1)' } },
            splitLine: { lineStyle: { color: 'rgba(255,255,255,.1)' } }
        }],
        series: [
            {
                name: '销售额', type: 'line', smooth: true, symbol: 'circle', symbolSize: 5, showSymbol: false,
                lineStyle: { normal: { width: 2 } },
                areaStyle: { normal: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: 'rgba(24, 163, 64, 0.3)' }, { offset: 0.8, color: 'rgba(24, 163, 64, 0)' }], false), shadowColor: 'rgba(0,0,0,0.1)', shadowBlur: 10 } },
                itemStyle: { normal: { color: '#cdba00', borderColor: 'rgba(137,189,2,0.27)', borderWidth: 12 } },
                data: [220, 182, 191, 134, 150, 120, 110, 125, 145, 122, 165, 122]
            },
            {
                name: '利润', type: 'line', smooth: true, symbol: 'circle', symbolSize: 5, showSymbol: false,
                lineStyle: { normal: { width: 2 } },
                areaStyle: { normal: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: 'rgba(39, 122,206, 0.3)' }, { offset: 0.8, color: 'rgba(39, 122,206, 0)' }], false), shadowColor: 'rgba(0,0,0,0.1)', shadowBlur: 10 } },
                itemStyle: { normal: { color: '#277ace', borderColor: 'rgba(0,136,212,0.2)', borderWidth: 12 } },
                data: [120, 110, 125, 145, 122, 165, 122, 220, 182, 191, 134, 150]
            }
        ]
    }
    myChart.setOption(option)
    initResizeFor(myChart)
}

function echarts_4() {
    const el = document.getElementById('echart4')
    if (!el) return
    const myChart = echarts.init(el)
    const option = {
        tooltip: { trigger: 'axis', axisPointer: { lineStyle: { color: '#57617B' } } },
        legend: {
            data: [{ name: '图例1' }, { name: '图例2' }, { name: '完成率' }],
            top: '0%',
            textStyle: { color: 'rgba(255,255,255,0.9)' }
        },
        xAxis: [{
            type: 'category',
            data: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月'],
            axisLine: { lineStyle: { color: 'rgba(255,255,255,.1)' } },
            axisLabel: { textStyle: { color: 'rgba(255,255,255,.6)', fontSize: '14' } },
        }],
        yAxis: [
            {
                type: 'value', name: '金额', min: 0, max: 50, interval: 10,
                axisLabel: { show: true },
                axisLine: { lineStyle: { color: 'rgba(255,255,255,.4)' } },
            },
            {
                type: 'value', name: '完成率', show: true,
                axisLabel: { show: true },
                axisLine: { lineStyle: { color: 'rgba(255,255,255,.4)' } },
                splitLine: { show: true, lineStyle: { color: '#001e94' } },
            },
        ],
        grid: { top: '10%', right: '30', bottom: '30', left: '30' },
        series: [
            {
                name: '图例1', type: 'bar', data: [4, 6, 36, 6, 8, 6, 4, 6, 30, 6, 8, 12], barWidth: 'auto',
                itemStyle: { normal: { color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1, colorStops: [{ offset: 0, color: '#609db8' }, { offset: 1, color: '#609db8' }], globalCoord: false } } }
            },
            {
                name: '图例2', type: 'bar', data: [4, 2, 34, 6, 8, 6, 4, 2, 32, 6, 8, 18], barWidth: 'auto',
                itemStyle: { normal: { color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1, colorStops: [{ offset: 0, color: '#66b8a7' }, { offset: 1, color: '#66b8a7' }], globalCoord: false } } },
                barGap: '0'
            },
            {
                name: '完成率', type: 'line', yAxisIndex: 1, data: [100, 50, 80, 30, 90, 40, 70, 33, 100, 40, 80, 20],
                lineStyle: { normal: { width: 2 } }, itemStyle: { normal: { color: '#cdba00' } }, smooth: true
            }
        ]
    }
    myChart.setOption(option)
    initResizeFor(myChart)
}

function echarts_5() {
    const el = document.getElementById('echart5')
    if (!el) return
    const myChart = echarts.init(el)
    const option = {
        tooltip: { show: false },
        grid: { top: '0%', left: '65', right: '14%', bottom: '0%' },
        xAxis: { min: 0, max: 100, splitLine: { show: false }, axisTick: { show: false }, axisLine: { show: false }, axisLabel: { show: false } },
        yAxis: {
            data: ['字段名称', '字段名称', '字段名称', '字段名称', '字段名称', '字段名称', '字段名称', '字段名称', '字段名称', '字段名称', '字段名称'],
            axisTick: { show: false }, axisLine: { show: false }, axisLabel: { color: 'rgba(255,255,255,.6)', fontSize: 14 }
        },
        series: [
            { type: 'bar', label: { show: true, zlevel: 10000, position: 'right', padding: 10, color: '#49bcf7', fontSize: 14, formatter: '{c}%' }, itemStyle: { color: '#49bcf7' }, barWidth: '15', data: [49, 80, 67, 99, 12, 19, 39, 84, 28, 47, 57, 100], z: 10 },
            { type: 'bar', barGap: '-100%', itemStyle: { color: '#fff', opacity: 0.1 }, barWidth: '15', data: Array(12).fill(100), z: 5 }
        ]
    }
    myChart.setOption(option)
    initResizeFor(myChart)
}

function zb1() {
    const el = document.getElementById('zb1')
    if (!el) return
    const myChart = echarts.init(el)
    const v1 = 298, v2 = 523, v3 = v1 + v2
    const option = {
        series: [{
            type: 'pie', radius: ['60%', '70%'], color: '#49bcf7', label: { normal: { position: 'center' } }, data: [
                { value: v2, name: '女消费', label: { normal: { formatter: `${v2}`, textStyle: { fontSize: 20, color: '#fff' } } } },
                { value: v1, name: '男消费', label: { normal: { formatter: () => `占比${Math.round(v2 / v3 * 100)}%`, textStyle: { color: '#aaa', fontSize: 12 } } }, itemStyle: { normal: { color: 'rgba(255,255,255,.2)' }, emphasis: { color: '#fff' } } }
            ]
        }]
    }
    myChart.setOption(option)
    initResizeFor(myChart)
}

function zb2() {
    const el = document.getElementById('zb2')
    if (!el) return
    const myChart = echarts.init(el)
    const v1 = 298, v2 = 523, v3 = v1 + v2
    const option = {
        series: [{
            type: 'pie', radius: ['60%', '70%'], color: '#cdba00', label: { normal: { position: 'center' } }, data: [
                { value: v1, name: '男消费', label: { normal: { formatter: `${v1}`, textStyle: { fontSize: 20, color: '#fff' } } } },
                { value: v2, name: '女消费', label: { normal: { formatter: () => `占比${Math.round(v1 / v3 * 100)}%`, textStyle: { color: '#aaa', fontSize: 12 } } }, itemStyle: { normal: { color: 'rgba(255,255,255,.2)' }, emphasis: { color: '#fff' } } }
            ]
        }]
    }
    myChart.setOption(option)
    initResizeFor(myChart)
}

function zb3() {
    const el = document.getElementById('zb3')
    if (!el) return
    const myChart = echarts.init(el)
    const v1 = 298, v2 = 523, v3 = v1 + v2
    const option = {
        series: [{
            type: 'pie', radius: ['60%', '70%'], color: '#62c98d', label: { normal: { position: 'center' } }, data: [
                { value: v2, name: '女消费', label: { normal: { formatter: `${v2}`, textStyle: { fontSize: 20, color: '#fff' } } } },
                { value: v1, name: '男消费', label: { normal: { formatter: () => `占比${Math.round(v2 / v3 * 100)}%`, textStyle: { color: '#aaa', fontSize: 12 } } }, itemStyle: { normal: { color: 'rgba(255,255,255,.2)' }, emphasis: { color: '#fff' } } }
            ]
        }]
    }
    myChart.setOption(option)
    initResizeFor(myChart)
}

onMounted(async () => {
    if (loadingEl.value) loadingEl.value.style.display = 'block'

    // initialize charts
    echarts_5()
    echarts_1()
    echarts_2()
    echarts_3()
    echarts_4()
    zb1()
    zb2()
    zb3()

    // start clock
    updateClock()
    intervalId = window.setInterval(updateClock, 1000)

    requestAnimationFrame(() => {
        if (loadingEl.value) loadingEl.value.style.display = 'none'
    })
})

onBeforeUnmount(() => {
    if (intervalId) clearInterval(intervalId)
    // remove resize handlers
    resizeHandlers.forEach(({ handler }) => window.removeEventListener('resize', handler))
})
</script>

<style lang="scss" scoped>
.dashboard-landing {
    background: #000d4a url(@/images/bg.jpg) center top;
}
</style>
