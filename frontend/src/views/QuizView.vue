<script setup>
import { computed, ref } from 'vue'

const props = defineProps({
  questions: { type: Array, required: true },
  sourceName: { type: String, default: '' },
})

defineEmits(['again'])

const index = ref(0)
const selected = ref('')
const revealed = ref(false)
const answers = ref([])

const total = computed(() => props.questions.length)
const current = computed(() => props.questions[index.value])
const finished = computed(() => answers.value.length === total.value && index.value >= total.value - 1 && revealed.value)
const score = computed(() => answers.value.filter((a) => a.correct).length)
const rate = computed(() => (total.value ? Math.round((score.value / total.value) * 100) : 0))

function choose(key) {
  if (revealed.value) return
  selected.value = key
}

function confirm() {
  if (!selected.value || revealed.value) return
  const correct = selected.value === current.value.answer
  answers.value.push({
    index: index.value,
    selected: selected.value,
    correct,
  })
  revealed.value = true
}

function next() {
  if (index.value >= total.value - 1) return
  index.value += 1
  selected.value = ''
  revealed.value = false
}

function optionClass(key) {
  if (!revealed.value) {
    return selected.value === key ? 'picked' : ''
  }
  if (key === current.value.answer) return 'correct'
  if (key === selected.value && key !== current.value.answer) return 'wrong'
  return ''
}
</script>

<template>
  <section class="panel">
    <div class="meta">
      <p>{{ sourceName || '未命名 PDF' }}</p>
      <p>第 {{ Math.min(index + 1, total) }} / {{ total }} 题</p>
    </div>

    <template v-if="!finished || index < total - 1 || !revealed">
      <h1>{{ current.question }}</h1>

      <div class="options">
        <button
          v-for="key in ['A', 'B', 'C', 'D']"
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
        <button class="primary" type="button" :disabled="!selected || revealed" @click="confirm">
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
      </div>

      <div v-if="revealed" class="explain">
        <p>
          结果：
          <strong :class="selected === current.answer ? 'ok' : 'bad'">
            {{ selected === current.answer ? '正确' : '错误' }}
          </strong>
          （正确答案 {{ current.answer }}）
        </p>
        <p>{{ current.explanation || '暂无解析' }}</p>
      </div>
    </template>

    <div v-if="revealed && index === total - 1" class="summary">
      <h2>答题结束</h2>
      <p>得分：{{ score }} / {{ total }}（正确率 {{ rate }}%）</p>
      <button class="primary" type="button" @click="$emit('again')">再来一份</button>
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

.meta {
  display: flex;
  justify-content: space-between;
  gap: 1rem;
  color: var(--muted);
  font-size: 0.92rem;
  margin-bottom: 0.75rem;
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
.secondary {
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

.secondary {
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
