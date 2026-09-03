<script setup>
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { extractTextFromPdf } from '../lib/pdfText'
import { createQuizSet } from '../api/quizSets'

const router = useRouter()

const title = ref('')
const file = ref(null)
const singleCount = ref(5)
const multipleCount = ref(3)
const judgeCount = ref(2)
const busy = ref(false)
const status = ref('')
const error = ref('')

const totalCount = computed(
  () => Number(singleCount.value || 0) + Number(multipleCount.value || 0) + Number(judgeCount.value || 0),
)

const canSubmit = computed(
  () => Boolean(title.value.trim()) && Boolean(file.value) && !busy.value && totalCount.value > 0,
)

function onFileChange(event) {
  file.value = event.target.files?.[0] || null
  error.value = ''
}

function clampCount(value) {
  const n = Number(value)
  if (Number.isNaN(n) || n < 0) return 0
  return Math.min(20, Math.floor(n))
}

async function submit() {
  if (!canSubmit.value) return
  singleCount.value = clampCount(singleCount.value)
  multipleCount.value = clampCount(multipleCount.value)
  judgeCount.value = clampCount(judgeCount.value)
  if (totalCount.value <= 0) {
    error.value = '请至少为一种题型填写大于 0 的数量'
    return
  }

  busy.value = true
  error.value = ''
  status.value = '正在提取 PDF 文本…'

  try {
    const text = await extractTextFromPdf(file.value)
    status.value = `已提取 ${text.length} 字，正在按题型出题并保存（合计 ${totalCount.value} 题）…`
    const detail = await createQuizSet({
      title: title.value.trim(),
      text,
      singleCount: singleCount.value,
      multipleCount: multipleCount.value,
      judgeCount: judgeCount.value,
    })
    router.replace(`/quiz/${detail.id}`)
  } catch (err) {
    error.value = err?.message || String(err)
    status.value = ''
  } finally {
    busy.value = false
  }
}
</script>

<template>
  <section class="panel">
    <header class="head">
      <div>
        <h1>添加问答</h1>
        <p class="desc">填写名称、各题型数量并导入 PDF。每种最多 20 道，至少选一种。</p>
      </div>
      <button class="ghost" type="button" :disabled="busy" @click="router.push('/')">返回题库</button>
    </header>

    <label class="field">
      <span>问卷名称</span>
      <input v-model="title" maxlength="200" type="text" placeholder="例如：第三章复习" :disabled="busy" />
    </label>

    <label class="field">
      <span>PDF 文件</span>
      <input accept="application/pdf,.pdf" type="file" :disabled="busy" @change="onFileChange" />
    </label>

    <div class="counts">
      <label class="field">
        <span>单选题（0–20）</span>
        <input v-model.number="singleCount" min="0" max="20" type="number" :disabled="busy" />
      </label>
      <label class="field">
        <span>多选题（0–20）</span>
        <input v-model.number="multipleCount" min="0" max="20" type="number" :disabled="busy" />
      </label>
      <label class="field">
        <span>判断题（0–20）</span>
        <input v-model.number="judgeCount" min="0" max="20" type="number" :disabled="busy" />
      </label>
    </div>
    <p class="hint">合计 {{ totalCount }} 题（最多 60）</p>

    <button class="primary" type="button" :disabled="!canSubmit" @click="submit">
      {{ busy ? '处理中…' : '生成并开始' }}
    </button>

    <p v-if="status" class="status">{{ status }}</p>
    <p v-if="error" class="error">{{ error }}</p>
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

.desc {
  margin: 0.45rem 0 0;
  color: var(--muted);
}

.counts {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 0.75rem;
}

@media (max-width: 720px) {
  .counts {
    grid-template-columns: 1fr;
  }
}

.field {
  display: grid;
  gap: 0.4rem;
  margin-bottom: 1rem;
}

.field span {
  font-size: 0.92rem;
  color: var(--muted);
}

.field input {
  border: 1px solid var(--line);
  border-radius: 10px;
  padding: 0.65rem 0.75rem;
  background: #fff;
}

.hint {
  margin: -0.35rem 0 1rem;
  color: var(--muted);
  font-size: 0.9rem;
}

.primary {
  border: 0;
  border-radius: 10px;
  padding: 0.75rem 1.1rem;
  background: var(--accent);
  color: #fff;
  font-weight: 600;
}

.primary:disabled {
  opacity: 0.55;
  cursor: not-allowed;
}

.ghost {
  border: 1px solid var(--line);
  background: #fff;
  border-radius: 10px;
  padding: 0.55rem 0.9rem;
}

.status {
  margin: 1rem 0 0;
  color: var(--warn);
}

.error {
  margin: 1rem 0 0;
  color: var(--bad);
}
</style>
