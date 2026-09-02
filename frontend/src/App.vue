<script setup>
import { ref } from 'vue'
import UploadView from './views/UploadView.vue'
import QuizView from './views/QuizView.vue'

const phase = ref('upload')
const questions = ref([])
const sourceName = ref('')

function onGenerated({ fileName, list }) {
  sourceName.value = fileName
  questions.value = list
  phase.value = 'quiz'
}

function reset() {
  questions.value = []
  sourceName.value = ''
  phase.value = 'upload'
}
</script>

<template>
  <div class="shell">
    <header class="top">
      <div>
        <p class="brand">pdf2q</p>
        <p class="tag">PDF → 选择题（本地版）</p>
      </div>
      <button v-if="phase === 'quiz'" class="ghost" type="button" @click="reset">重新上传</button>
    </header>

    <main>
      <UploadView v-if="phase === 'upload'" @generated="onGenerated" />
      <QuizView v-else :questions="questions" :source-name="sourceName" @again="reset" />
    </main>
  </div>
</template>

<style scoped>
.shell {
  width: min(880px, calc(100% - 2rem));
  margin: 0 auto;
  padding: 2rem 0 3rem;
}

.top {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.brand {
  margin: 0;
  font-size: 1.75rem;
  font-weight: 700;
  letter-spacing: -0.03em;
}

.tag {
  margin: 0.25rem 0 0;
  color: var(--muted);
}

.ghost {
  border: 1px solid var(--line);
  background: var(--card);
  color: var(--ink);
  border-radius: 10px;
  padding: 0.55rem 0.9rem;
}
</style>
