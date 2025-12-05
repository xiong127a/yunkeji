<script setup>
import { RouterView } from 'vue-router'
import AppHeader from './components/Header.vue'
import AppFooter from './components/Footer.vue'
import ContactFloatButton from './components/ContactFloatButton.vue'
</script>

<template>
  <div id="app">
    <AppHeader />
    <RouterView />
    <AppFooter />
    <ContactFloatButton />
  </div>
</template>

<style>
#app {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 0;
  display: flex;
  flex-direction: column;
}

#app > :nth-child(2) {
  flex: 1;
}

* {
  box-sizing: border-box;
}

.el-card {
  border-radius: 12px !important;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08) !important;
  border: none !important;
  transition: all 0.3s ease;
}

.el-card:hover {
  box-shadow: 0 6px 24px rgba(0, 0, 0, 0.12) !important;
  transform: translateY(-2px);
}

.el-button {
  border-radius: 8px !important;
  font-weight: 500 !important;
  transition: all 0.3s ease !important;
}

.el-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1) !important;
}

.el-input__inner {
  border-radius: 8px !important;
}

h1, h2, h3, h4, h5, h6 {
  color: #2c3e50;
  font-weight: 600;
}

p {
  color: #7f8c8d;
  line-height: 1.6;
}
</style>