import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useDashboardStore = defineStore('dashboard', () => {
  const isSidebarOpen = ref(true)
  const toggleSidebar = () => { isSidebarOpen.value = !isSidebarOpen.value }

  // KPI 지표 데이터
  const metrics = ref([
    { title: '총 방문자', value: '124,500', trend: 12.5, icon: 'bi-people' },
    { title: '신규 가입', value: '1,250', trend: -2.4, icon: 'bi-person-plus' },
    { title: '총 매출액', value: '₩84.2M', trend: 8.2, icon: 'bi-currency-dollar' },
    { title: '전환율', value: '3.4%', trend: 1.1, icon: 'bi-lightning-charge' }
  ])

  // 테이블 데이터
  const tableData = ref([
    { id: '1042', source: 'Google 검색', status: '완료', amount: '₩125,000' },
    { id: '1041', source: '직접 유입', status: '대기중', amount: '₩45,000' },
    { id: '1040', source: 'Facebook 광고', status: '완료', amount: '₩210,000' }
  ])

  return { isSidebarOpen, toggleSidebar, metrics, tableData }
})