<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getQuizSet, saveProgress } from '../api/quizSets'

const props = defineProps({
  id: { type: [String, Number], required: true },
})

const route = useRoute()
const router = useRouter()
const loading = ref(true)
const error = ref('')
const title = ref('')
const questions = ref([])
const index = ref(0)
/** 单选/判断：'A'；多选：['A','C'] */
const selected = ref('')
const selectedMulti = ref([])
const revealed = ref(false)
const answers = ref([])
const saving = ref(false)

/** 是否从答题记录页点题号进入 */
const fromRecord = computed(() => route.query.from === 'record')

const total = computed(() => questions.value.length)
const current = computed(() => questions.value[index.value])
const currentType = computed(() => current.value?.type || 'single')
const optionKeys = computed(() => {
  if (!current.value?.options) return []
  return Object.keys(current.value.options)
})
const typeLabel = computed(() => {
  if (currentType.value === 'multiple') return '多选题'
  if (currentType.value === 'judge') return '判断题'
  return '单选题'
})
const finished = computed(
  () => total.value > 0 && answers.value.length >= total.value && revealed.value && index.value >= total.value - 1,
)
const score = computed(() => answers.value.filter((a) => a.correct).length)
const rate = computed(() => (total.value ? Math.round((score.value / total.value) * 100) : 0))

function normalizeAnswer(value) {
  if (Array.isArray(value)) {
    return [...value].map((v) => String(v).toUpperCase()).sort().join(',')
  }
  return String(value || '')
    .toUpperCase()
    .split(/[,|、;/]+/)
    .map((s) => s.trim())
    .filter(Boolean)
    .sort()
    .join(',')
}

function serializeSelection() {
  if (currentType.value === 'multiple') {
    return normalizeAnswer(selectedMulti.value)
  }
  return String(selected.value || '').toUpperCase()
}

function restoreSelection(raw) {
  if (currentType.value === 'multiple') {
    selectedMulti.value = String(raw || '')
      .split(',')
      .map((s) => s.trim())
      .filter(Boolean)
    selected.value = ''
  } else {
    selected.value = String(raw || '')
    selectedMulti.value = []
  }
}

function clearSelection() {
  selected.value = ''
  selectedMulti.value = []
}

function hasSelection() {
  if (currentType.value === 'multiple') return selectedMulti.value.length > 0
  return Boolean(selected.value)
}

async function persist() {
  saving.value = true
  try {
    await saveProgress(props.id, {
      currentIndex: index.value,
      answers: answers.value,
    })
  } catch (err) {
    error.value = err?.message || String(err)
  } finally {
    saving.value = false
  }
}

async function load() {
  loading.value = true
  error.value = ''
  try {
    const detail = await getQuizSet(props.id)
    title.value = detail.title
    questions.value = detail.questions || []
    const progress = detail.progress || { currentIndex: 0, answers: [] }
    answers.value = Array.isArray(progress.answers) ? [...progress.answers] : []

    const maxIndex = Math.max(questions.value.length - 1, 0)
    const qParam = Number(route.query.q)
    if (Number.isFinite(qParam) && qParam >= 1) {
      index.value = Math.min(Math.max(qParam - 1, 0), maxIndex)
    } else {
      index.value = Math.min(progress.currentIndex || 0, maxIndex)
    }

    const answeredCurrent = answers.value.find((a) => a.index === index.value)
    if (answeredCurrent) {
      restoreSelection(answeredCurrent.selected)
      revealed.value = true
    } else {
      clearSelection()
      revealed.value = false
    }
  } catch (err) {
    error.value = err?.message || String(err)
  } finally {
    loading.value = false
  }
}

watch(() => [props.id, route.query.q], load)
onMounted(load)

function choose(key) {
  if (revealed.value) return
  if (currentType.value === 'multiple') {
    const set = new Set(selectedMulti.value)
    if (set.has(key)) set.delete(key)
    else set.add(key)
    selectedMulti.value = [...set].sort()
    return
  }
  selected.value = key
}

async function confirm() {
  if (!hasSelection() || revealed.value) return
  const selectedValue = serializeSelection()
  const correct = normalizeAnswer(selectedValue) === normalizeAnswer(current.value.answer)
  const nextAnswers = answers.value.filter((a) => a.index !== index.value)
  nextAnswers.push({ index: index.value, selected: selectedValue, correct })
  nextAnswers.sort((a, b) => a.index - b.index)
  answers.value = nextAnswers
  revealed.value = true
  await persist()
}

async function next() {
  if (index.value >= total.value - 1) return
  index.value += 1
  const answered = answers.value.find((a) => a.index === index.value)
  if (answered) {
    restoreSelection(answered.selected)
    revealed.value = true
  } else {
    clearSelection()
    revealed.value = false
  }
  await persist()
}

async function exitQuiz() {
  await persist()
  router.push('/')
}

async function backToRecord() {
  await persist()
  router.push(`/quiz/${props.id}/record`)
}

function isPicked(key) {
  if (currentType.value === 'multiple') return selectedMulti.value.includes(key)
  return selected.value === key
}

function isCorrectKey(key) {
  return normalizeAnswer(current.value.answer)
    .split(',')
    .filter(Boolean)
    .includes(key)
}

function optionClass(key) {
  if (!revealed.value) {
    return isPicked(key) ? 'picked' : ''
  }
  if (isCorrectKey(key)) return 'correct'
  if (isPicked(key) && !isCorrectKey(key)) return 'wrong'
  return ''
}

function formatAnswer(answer) {
  if (currentType.value === 'judge') {
    const key = String(answer || '').toUpperCase()
    return current.value?.options?.[key] || answer
  }
  return answer
}
</script>

<template>
  <section class="panel">
    <header class="head">
      <div>
        <p class="muted">{{ title || '答题' }}</p>
        <p v-if="!loading && total" class="muted small">
          第 {{ Math.min(index + 1, total) }} / {{ total }} 题 · {{ typeLabel }}
          <span v-if="saving"> · 保存中…</span>
        </p>
      </div>
      <button class="ghost" type="button" :disabled="loading" @click="exitQuiz">退出答题</button>
    </header>

    <p v-if="loading" class="muted">加载中…</p>
    <p v-if="error" class="error">{{ error }}</p>

    <template v-if="!loading && current">
      <p v-if="currentType === 'multiple'" class="tip">多选题：可选多项，须与标准答案完全一致才得分。</p>
      <h1>{{ current.question }}</h1>

      <div class="options">
        <button
          v-for="key in optionKeys"
          :key="key"
          type="button"
          class="option"
          :class="optionClass(key)"
          @click="choose(key)"
        >
          <strong>{{ key }}.</strong>
          <span>{{ current.options[key] }}</span>
        </button>
      </div>

      <div class="actions">
        <button class="primary" type="button" :disabled="!hasSelection() || revealed" @click="confirm">
          提交答案
        </button>
        <button
          v-if="revealed && index < total - 1"
          class="secondary"
          type="button"
          @click="next"
        >
          下一题
        </button>
        <button
          v-if="fromRecord"
          class="secondary"
          type="button"
          @click="backToRecord"
        >
          返回答题记录
        </button>
      </div>

      <div v-if="revealed" class="explain">
        <p>
          结果：
          <strong :class="normalizeAnswer(serializeSelection()) === normalizeAnswer(current.answer) ? 'ok' : 'bad'">
            {{ normalizeAnswer(serializeSelection()) === normalizeAnswer(current.answer) ? '正确' : '错误' }}
          </strong>
          （正确答案 {{ formatAnswer(current.answer) }}）
        </p>
        <p>{{ current.explanation || '暂无解析' }}</p>
      </div>

      <div v-if="finished" class="summary">
        <h2>答题结束</h2>
        <p>得分：{{ score }} / {{ total }}（正确率 {{ rate }}%）</p>
        <button class="primary" type="button" @click="exitQuiz">返回题库</button>
      </div>
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
  margin-bottom: 0.85rem;
}

.muted {
  margin: 0;
  color: var(--muted);
}

.small {
  margin-top: 0.25rem;
  font-size: 0.92rem;
}

.tip {
  margin: 0 0 0.75rem;
  color: var(--warn);
  font-size: 0.9rem;
}

.error {
  color: var(--bad);
}

h1 {
  margin: 0 0 1rem;
  font-size: 1.2rem;
  line-height: 1.45;
}

.options {
  display: grid;
  gap: 0.65rem;
}

.option {
  display: flex;
  gap: 0.55rem;
  text-align: left;
  border: 1px solid var(--line);
  background: #fff;
  border-radius: 12px;
  padding: 0.85rem 0.95rem;
  line-height: 1.4;
}

.option.picked {
  border-color: var(--accent);
  background: var(--accent-soft);
}

.option.correct {
  border-color: var(--ok);
  background: #dcfce7;
}

.option.wrong {
  border-color: var(--bad);
  background: #fee2e2;
}

.actions {
  display: flex;
  gap: 0.75rem;
  margin-top: 1.1rem;
}

.primary,
.secondary,
.ghost {
  border-radius: 10px;
  padding: 0.7rem 1rem;
  font-weight: 600;
}

.primary {
  border: 0;
  background: var(--accent);
  color: #fff;
}

.primary:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.secondary,
.ghost {
  border: 1px solid var(--line);
  background: #fff;
}

.explain {
  margin-top: 1rem;
  padding: 0.9rem 1rem;
  border-radius: 12px;
  background: #fafaf9;
  border: 1px solid var(--line);
  line-height: 1.5;
}

.ok {
  color: var(--ok);
}

.bad {
  color: var(--bad);
}

.summary {
  margin-top: 1.25rem;
  padding-top: 1rem;
  border-top: 1px solid var(--line);
}

.summary h2 {
  margin: 0 0 0.4rem;
}
</style>
