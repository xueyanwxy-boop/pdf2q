<script setup>
</script>

<template>
  <div class="shell">
    <router-view />
  </div>
</template>

<style scoped>
.shell {
  width: min(920px, calc(100% - 2rem));
  margin: 0 auto;
  padding: 2rem 0 3rem;
}
</style>
