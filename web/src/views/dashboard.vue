<template>

  <div>
    <div class="d-flex justify-content-between align-items-end mb-4">
        <div>
          <h2 class="fw-bold mb-1">종합 성과 대시보드</h2>
          <p class="text-muted-dark mb-0">2026년 9월 전체 시스템 모니터링 현황</p>
        </div>
    </div>
    <!-- 지표 카드 -->
    <div class="row g-3 mb-4">
      <div class="col-12 col-sm-6 col-xl-3" v-for="metric in store.metrics" :key="metric.title">
        <div class="card-dark p-3">
          <div class="text-secondary fw-semibold">{{ metric.title }}</div>
          <div class="fs-2 fw-bold my-2">{{ metric.value }}</div>
          <div :class="metric.trend > 0 ? 'text-success' : 'text-danger'">
            {{ metric.trend }}% vs 지난주
          </div>
        </div>
      </div>
    </div>

    <!-- 3D ECharts 영역 -->
    <div class="card-dark p-3 mb-4">
      <h5 class="mb-3">다차원 성과 분석 (3D Surface)</h5>
      <div ref="chartDom" style="width: 100%; height: 350px;"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useDashboardStore } from '../stores/dashboardStore'
import * as echarts from 'echarts'
import 'echarts-gl'

const store = useDashboardStore()
const chartDom = ref(null)

onMounted(() => {
  const myChart3D = echarts.init(chartDom.value)
  const surfaceData = []
  for (let i = 0; i <= 10; i++) {
    for (let j = 0; j <= 10; j++) {
      let z = Math.sin(i / 2) * Math.cos(j / 2) * 50 + 50
      surfaceData.push([i, j, z])
    }
  }

  myChart3D.setOption({
    visualMap: { show: false, min: 0, max: 100, inRange: { color: ['#311b92', '#0288d1', '#42b883'] } },
    xAxis3D: { type: 'value', name: '지역' },
    yAxis3D: { type: 'value', name: '시간' },
    zAxis3D: { type: 'value', name: '매출' },
    grid3D: { environment: '#1e1e1e', viewControl: { alpha: 35, beta: 45 } },
    series: [{ type: 'surface', data: surfaceData, shading: 'color' }]
  })
  
  window.addEventListener('resize', () => myChart3D.resize())
})
</script>