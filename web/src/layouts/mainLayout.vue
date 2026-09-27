<template>
  <div>
    <!-- 상단 네비게이션 -->
    <nav class="navbar-custom fixed-top d-flex justify-content-between align-items-center px-4">
      <div class="d-flex align-items-center">
        <button class="btn border-0 text-white me-3" @click="store.toggleSidebar">
          <i class="bi bi-list fs-4"></i>
        </button>
        <a class="navbar-brand" href="#">
            <img src="@/assets/logo/top_logo_white.png" alt="logo">
            <span class="fs-5 fw-bold text-white">Analytics Pro</span>
        </a>
      </div>
    </nav>

    <div class="main-container">
      <!-- 사이드 메뉴 -->
      <aside class="sidebar" :class="{ 'show': store.isSidebarOpen }">
        <div class="sidebar-group-title">대시보드</div>
        <router-link to="/" class="sidebar-link active"><i class="bi bi-speedometer2"></i> 종합 요약</router-link>
        <router-link to="/traffic" class="sidebar-link"><i class="bi bi-graph-up"></i> 트래픽 분석</router-link>
      </aside>

      <!-- 자식 컴포넌트(대시보드) 렌더링 영역 -->
      <main class="content-area" :class="{ 'sidebar-open': store.isSidebarOpen }">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { useDashboardStore } from '../stores/dashboardStore'
const store = useDashboardStore()
</script>