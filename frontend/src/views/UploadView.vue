<script setup>
import { computed, ref } from 'vue'
import { extractTextFromPdf } from '../lib/pdfText'
import { generateQuiz } from '../api/quiz'

const emit = defineEmits(['generated'])

const file = ref(null)
const count = ref(10)
const busy = ref(false)
const status = ref('')
const error = ref('')
const preview = ref('')

const canSubmit = computed(() => Boolean(file.value) && !busy.value)

function onFileChange(event) {
  const picked = event.target.files?.[0] || null
  file.value = picked
  preview.value = ''
  error.value = ''
  status.value = picked ? `已选择：${picked.name}` : ''
}

async function start() {
  if (!file.value) return
  busy.value = true
  error.value = ''
  status.value = '正在提取 PDF 文本…'

  try {
    const text = await extractTextFromPdf(file.value)
    preview.value = text.slice(0, 500) + (text.length > 500 ? '…' : '')
    status.value = `已提取 ${text.length} 字，正在调用 DeepSeek 出题…`
    const list = await generateQuiz(text, count.value)
    emit('generated', { fileName: file.value.name, list })
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
    <h1>上传 PDF</h1>
    <p class="desc">浏览器本地抽字，后端仅负责调用 DeepSeek。请使用可选中文字的 PDF。</p>

    <label class="field">
      <span>PDF 文件</span>
      <input accept="application/pdf,.pdf" type="file" :disabled="busy" @change="onFileChange" />
    </label>

    <label class="field">
      <span>题目数量（1–20）</span>
      <input v-model.number="count" min="1" max="20" type="number" :disabled="busy" />
    </label>

    <button class="primary" type="button" :disabled="!canSubmit" @click="start">
      {{ busy ? '处理中…' : '生成选择题' }}
    </button>

    <p v-if="status" class="status">{{ status }}</p>
    <p v-if="error" class="error">{{ error }}</p>

    <div v-if="preview" class="preview">
      <h2>文本预览</h2>
      <pre>{{ preview }}</pre>
    </div>
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

h1 {
  margin: 0;
  font-size: 1.35rem;
}

.desc {
  margin: 0.5rem 0 1.25rem;
  color: var(--muted);
  line-height: 1.5;
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

.status {
  margin: 1rem 0 0;
  color: var(--warn);
}

.error {
  margin: 1rem 0 0;
  color: var(--bad);
}

.preview {
  margin-top: 1.25rem;
  padding-top: 1rem;
  border-top: 1px solid var(--line);
}

.preview h2 {
  margin: 0 0 0.5rem;
  font-size: 1rem;
}

.preview pre {
  margin: 0;
  white-space: pre-wrap;
  word-break: break-word;
  background: #fafaf9;
  border-radius: 10px;
  padding: 0.85rem;
  color: var(--muted);
  font-size: 0.88rem;
  line-height: 1.5;
  max-height: 180px;
  overflow: auto;
}
</style>
