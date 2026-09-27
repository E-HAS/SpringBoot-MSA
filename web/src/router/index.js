import { createRouter, createWebHistory } from 'vue-router'
import MainLayout from '../layouts/mainLayout.vue'
import Dashboard from '../views/dashboard.vue'

export default createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      component: MainLayout,
      children: [
        { path: '', name: 'Dashboard', component: Dashboard }
      ]
    }
  ]
})