<template>
  <!-- 
    App.vue는 최상위 컴포넌트입니다.
    이곳에 <router-view />를 선언하면, src/router/index.js에서 설정한 
    MainLayout.vue가 이 위치에 렌더링되고, 
    MainLayout 내부의 또 다른 <router-view /> 영역에 
    Dashboard나 Traffic 같은 자식 페이지가 렌더링됩니다.
  -->
  <router-view />
</template>

<script setup>
// App.vue는 전역 라우터 진입점 역할만 하므로 특별한 스크립트 로직이 필요 없습니다.
// 글로벌하게 처리해야 할 로직(예: 초기 사용자 인증 확인, 앱 설정 로드 등)이 있다면 
// 이곳의 onMounted 등에서 처리할 수 있습니다.
</script>

<style>
/* 
  앱 전역 스타일은 src/assets/main.css 파일로 분리하여 
  main.js에서 import 했으므로, 
  App.vue의 <style> 태그는 비워두어도 됩니다. 
*/
</style>