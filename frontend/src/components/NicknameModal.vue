<script setup>
import { nextTick, onMounted, onUnmounted, ref, watch } from 'vue'
import { updateNickname } from '../api/auth'

const props = defineProps({
  open: { type: Boolean, default: false },
  current: { type: String, default: '' },
})

const emit = defineEmits(['close', 'saved'])

const nickname = ref('')
const busy = ref(false)
const error = ref('')
const inputEl = ref(null)

function close() {
  if (!busy.value) emit('close')
}

function onKeydown(e) {
  if (e.key === 'Escape' && props.open) {
    close()
  }
}

watch(
  () => props.open,
  async (v) => {
    if (v) {
      nickname.value = props.current || ''
      error.value = ''
      busy.value = false
      await nextTick()
      inputEl.value?.focus()
      inputEl.value?.select()
    }
  },
)

onMounted(() => window.addEventListener('keydown', onKeydown))
onUnmounted(() => window.removeEventListener('keydown', onKeydown))

async function submit() {
  const value = nickname.value.trim()
  if (value.length < 2 || value.length > 16) {
    error.value = '昵称长度为 2–16 字'
    return
  }
  busy.value = true
  error.value = ''
  try {
    const data = await updateNickname(value)
    emit('saved', data.nickname)
    emit('close')
  } catch (err) {
    error.value = err?.message || String(err)
  } finally {
    busy.value = false
  }
}
</script>

<template>
  <div v-if="open" class="mask">
    <div class="dialog panel" role="dialog" aria-modal="true" aria-labelledby="nick-title">
      <header class="head">
        <h2 id="nick-title">修改昵称</h2>
        <button
          class="close-x"
          type="button"
          aria-label="关闭"
          :disabled="busy"
          @click="close"
        >
          ×
        </button>
      </header>
      <p class="desc">2–16 个字，允许与他人重名。</p>
      <label class="field">
        <span>昵称</span>
        <input
          ref="inputEl"
          v-model="nickname"
          maxlength="16"
          type="text"
          placeholder="输入新昵称"
          :disabled="busy"
          @keydown.enter.prevent="submit"
        />
      </label>
      <div class="actions">
        <button class="ghost" type="button" :disabled="busy" @click="close">取消</button>
        <button class="primary" type="button" :disabled="busy" @click="submit">
          {{ busy ? '保存中…' : '保存' }}
        </button>
      </div>
      <p v-if="error" class="error">{{ error }}</p>
    </div>
  </div>
</template>

<style scoped>
.mask {
  position: fixed;
  inset: 0;
  background: rgba(28, 25, 23, 0.35);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1rem;
  z-index: 50;
}

.dialog {
  width: min(360px, 100%);
  background: var(--card);
  border: 1px solid var(--line);
  border-radius: 18px;
  padding: 1.25rem 1.35rem 1.4rem;
  box-shadow: 0 16px 40px rgba(28, 25, 23, 0.12);
}

.head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 0.75rem;
}

h2 {
  margin: 0;
  font-size: 1.15rem;
}

.close-x {
  width: 2rem;
  height: 2rem;
  border-radius: 999px;
  border: 1px solid var(--line);
  background: #fff;
  color: var(--ink);
  font-size: 1.25rem;
  line-height: 1;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 0;
  flex-shrink: 0;
}

.close-x:disabled {
  opacity: 0.45;
}

.desc {
  margin: 0.55rem 0 1rem;
  color: var(--muted);
  font-size: 0.9rem;
}

.field {
  display: grid;
  gap: 0.35rem;
  margin-bottom: 1rem;
}

.field span {
  color: var(--muted);
  font-size: 0.88rem;
}

.field input {
  border: 1px solid var(--line);
  border-radius: 10px;
  padding: 0.65rem 0.75rem;
}

.actions {
  display: flex;
  gap: 0.55rem;
  justify-content: flex-end;
}

.primary {
  border: 0;
  border-radius: 10px;
  padding: 0.65rem 1rem;
  background: var(--accent);
  color: #fff;
  font-weight: 600;
}

.primary:disabled,
.ghost:disabled {
  opacity: 0.55;
}

.ghost {
  border: 1px solid var(--line);
  background: #fff;
  border-radius: 10px;
  padding: 0.65rem 1rem;
}

.error {
  margin: 0.75rem 0 0;
  color: var(--bad);
}
</style>
