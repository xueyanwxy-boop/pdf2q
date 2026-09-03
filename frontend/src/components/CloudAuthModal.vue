<script setup>
import { onMounted, onUnmounted, ref, watch } from 'vue'
import { defaultNickname } from '../api/client'
import { login, register, syncCloud } from '../api/auth'

const props = defineProps({
  open: { type: Boolean, default: false },
})

const emit = defineEmits(['close', 'synced'])

const phone = ref('')
const password = ref('')
const busy = ref(false)
const error = ref('')
const info = ref('')

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
  (v) => {
    if (v) {
      error.value = ''
      info.value = ''
      busy.value = false
    }
  },
)

onMounted(() => window.addEventListener('keydown', onKeydown))
onUnmounted(() => window.removeEventListener('keydown', onKeydown))

/** 先登录；没有账号则自动注册（昵称系统赋值）。 */
async function loginOrRegister() {
  const payload = {
    phone: phone.value.trim(),
    password: password.value,
  }
  try {
    await login(payload)
    return
  } catch (loginErr) {
    try {
      await register({
        ...payload,
        nickname: defaultNickname(),
      })
    } catch (regErr) {
      const msg = regErr?.message || ''
      // 手机号已存在说明密码不对，而不是去注册
      if (msg.includes('已注册') || msg.includes('409') || msg.includes('Conflict')) {
        throw new Error('手机号或密码错误')
      }
      // 登录失败且注册也不是“已注册”，把更可读的错误抛出
      if (msg) throw regErr
      throw loginErr
    }
  }
}

async function submit() {
  if (busy.value) return
  if (!/^1\d{10}$/.test(phone.value.trim())) {
    error.value = '请输入正确的 11 位手机号'
    return
  }
  if (!password.value || password.value.length < 6) {
    error.value = '密码至少 6 位'
    return
  }

  busy.value = true
  error.value = ''
  info.value = ''
  try {
    await loginOrRegister()
    info.value = '登录成功，正在同步…'
    const result = await syncCloud()
    info.value = result.message || '同步完成'
    emit('synced', result)
    setTimeout(() => emit('close'), 600)
  } catch (err) {
    error.value = err?.message || String(err)
  } finally {
    busy.value = false
  }
}
</script>

<template>
  <div v-if="open" class="mask">
    <div class="dialog panel" role="dialog" aria-modal="true">
      <header class="head">
        <h2>云端登录</h2>
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
      <div class="desc">
        <p>手机号唯一；无账号将自动注册（昵称系统分配）。</p>
        <p class="warn-line">登录后将一键合并本地与云端题库。</p>
      </div>

      <label class="field">
        <span>手机号</span>
        <input v-model="phone" maxlength="11" type="tel" placeholder="11 位手机号" :disabled="busy" />
      </label>
      <label class="field">
        <span>密码</span>
        <input v-model="password" minlength="6" type="password" placeholder="至少 6 位" :disabled="busy" />
      </label>

      <div class="actions">
        <button class="primary" type="button" :disabled="busy" @click="submit">
          {{ busy ? '处理中…' : '登录并同步' }}
        </button>
      </div>

      <p v-if="info" class="status">{{ info }}</p>
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
  width: min(420px, 100%);
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
  font-size: 1.2rem;
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
  line-height: 1.45;
}

.desc p {
  margin: 0;
}

.warn-line {
  margin-top: 0.35rem !important;
  color: var(--bad);
  font-weight: 600;
}

.field {
  display: grid;
  gap: 0.35rem;
  margin-bottom: 0.85rem;
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
  margin-top: 0.25rem;
}

.primary {
  width: 100%;
  border: 0;
  border-radius: 10px;
  padding: 0.75rem 1rem;
  background: var(--accent);
  color: #fff;
  font-weight: 600;
}

.primary:disabled {
  opacity: 0.55;
}

.status {
  margin: 0.75rem 0 0;
  color: var(--warn);
}

.error {
  margin: 0.75rem 0 0;
  color: var(--bad);
}
</style>
