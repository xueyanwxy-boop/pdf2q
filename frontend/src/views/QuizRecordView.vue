<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { getQuizSet } from '../api/quizSets'

const props = defineProps({
  id: { type: [String, Number], required: true },
})

const router = useRouter()
const loading = ref(true)
const error = ref('')
const title = ref('')
const questionCount = ref(0)
const answers = ref([])

const correctCount = computed(() => answers.value.filter((a) => a.correct).length)
const wrongCount = computed(() => answers.value.filter((a) => !a.correct).length)
const answeredCount = computed(() => answers.value.length)
const unansweredCount = computed(() => Math.max(questionCount.value - answeredCount.value, 0))

/** 每题状态：correct | wrong | blank */
const cells = computed(() => {
  const map = new Map(answers.value.map((a) => [a.index, a.correct]))
  const list = []
  for (let i = 0; i < questionCount.value; i += 1) {
    if (!map.has(i)) {
      list.push({ no: i + 1, status: 'blank' })
    } else {
      list.push({ no: i + 1, status: map.get(i) ? 'correct' : 'wrong' })
    }
  }
  return list
})

async function load() {
  loading.value = true
  error.value = ''
  try {
    const detail = await getQuizSet(props.id)
    title.value = detail.title || ''
    questionCount.value = detail.questionCount || (detail.questions || []).length || 0
    answers.value = Array.isArray(detail.progress?.answers) ? detail.progress.answers : []
  } catch (err) {
    error.value = err?.message || String(err)
  } finally {
    loading.value = false
  }
}

watch(() => props.id, load)
onMounted(load)
</script>

<template>
  <section class="panel">
    <header class="head">
      <div>
        <h1>答题记录</h1>
        <p class="muted">{{ title || '—' }}</p>
      </div>
      <button class="ghost" type="button" @click="router.push('/')">返回题库</button>
    </header>

    <p v-if="loading" class="muted">加载中…</p>
    <p v-if="error" class="error">{{ error }}</p>

    <template v-if="!loading && !error">
      <div v-if="answeredCount === 0" class="empty">
        <p>还没有作答记录，先去答题吧。</p>
        <button class="primary" type="button" @click="router.push(`/quiz/${id}`)">去答题</button>
      </div>

      <template v-else>
        <div class="stats">
          <div class="stat">
            <p class="label">已答</p>
            <p class="value">{{ answeredCount }} / {{ questionCount }}</p>
          </div>
          <div class="stat ok">
            <p class="label">答对</p>
            <p class="value">{{ correctCount }}</p>
          </div>
          <div class="stat bad">
            <p class="label">答错</p>
            <p class="value">{{ wrongCount }}</p>
          </div>
          <div v-if="unansweredCount > 0" class="stat">
            <p class="label">未答</p>
            <p class="value">{{ unansweredCount }}</p>
          </div>
        </div>

        <h2 class="section-title">题目一览</h2>
        <p class="hint">绿色正确 · 红色错误 · 灰色未答 · 点击题号查看详情</p>
        <div class="grid">
          <button
            v-for="cell in cells"
            :key="cell.no"
            type="button"
            class="cell"
            :class="cell.status"
            :title="cell.status === 'correct' ? '正确' : cell.status === 'wrong' ? '错误' : '未答'"
            @click="router.push({ path: `/quiz/${id}`, query: { q: String(cell.no), from: 'record' } })"
          >
            {{ cell.no }}
          </button>
        </div>
      </template>
    </template>
  </section>
</template>

<style scoped>
.panel {
  background: var(--card);
  border: 1px solid var(--line);
  border-radius: 18px;
  padding: 1.5rem;
  box-shadow: 0 10px 30px rgba(28, 25, 23, 0.04);
}

.head {
  display: flex;
  justify-content: space-between;
  gap: 1rem;
  align-items: flex-start;
  margin-bottom: 1.25rem;
}

h1 {
  margin: 0;
  font-size: 1.35rem;
}

.muted {
  margin: 0.35rem 0 0;
  color: var(--muted);
}

.error {
  color: var(--bad);
}

.stats {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 0.75rem;
  margin-bottom: 1.35rem;
}

@media (max-width: 640px) {
  .stats {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

.stat {
  border: 1px solid var(--line);
  border-radius: 14px;
  padding: 0.85rem 1rem;
  background: #fafaf9;
}

.stat .label {
  margin: 0;
  color: var(--muted);
  font-size: 0.88rem;
}

.stat .value {
  margin: 0.35rem 0 0;
  font-size: 1.45rem;
  font-weight: 700;
}

.stat.ok .value {
  color: var(--ok);
}

.stat.bad .value {
  color: var(--bad);
}

.section-title {
  margin: 0 0 0.35rem;
  font-size: 1.05rem;
}

.hint {
  margin: 0 0 0.85rem;
  color: var(--muted);
  font-size: 0.88rem;
}

.grid {
  display: flex;
  flex-wrap: wrap;
  gap: 0.55rem;
}

.cell {
  width: 2.4rem;
  height: 2.4rem;
  border-radius: 10px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 0.95rem;
  border: 1px solid transparent;
  cursor: pointer;
  padding: 0;
}

.cell.correct {
  background: #dcfce7;
  color: var(--ok);
  border-color: #86efac;
}

.cell.wrong {
  background: #fee2e2;
  color: var(--bad);
  border-color: #fca5a5;
}

.cell.blank {
  background: #f5f5f4;
  color: var(--muted);
  border-color: var(--line);
}

.empty {
  display: grid;
  gap: 1rem;
  justify-items: start;
}

.primary {
  border: 0;
  border-radius: 10px;
  padding: 0.7rem 1rem;
  background: var(--accent);
  color: #fff;
  font-weight: 600;
}

.ghost {
  border: 1px solid var(--line);
  background: #fff;
  border-radius: 10px;
  padding: 0.55rem 0.9rem;
}
</style>
