<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { deleteQuizSet, listQuizSets, resetProgress } from '../api/quizSets'
import { getAuth } from '../api/client'
import { logout, syncCloud } from '../api/auth'
import CloudAuthModal from '../components/CloudAuthModal.vue'
import NicknameModal from '../components/NicknameModal.vue'

const router = useRouter()
const loading = ref(true)
const error = ref('')
const items = ref([])
const auth = ref(getAuth())
const showAuth = ref(false)
const showNickname = ref(false)
const syncing = ref(false)
const syncMsg = ref('')

async function load() {
  loading.value = true
  error.value = ''
  try {
    items.value = await listQuizSets()
  } catch (err) {
    error.value = err?.message || String(err)
  } finally {
    loading.value = false
  }
}

function progressLabel(item) {
  if (item.finished) return `已完成 ${item.answeredCount}/${item.questionCount}`
  if (item.answeredCount === 0) return `未开始 · 共 ${item.questionCount} 题`
  return `进度 ${item.answeredCount}/${item.questionCount}`
}

function actionLabel(item) {
  if (item.finished) return '重新作答'
  if (item.answeredCount > 0) return '继续作答'
  return '开始作答'
}

async function openQuiz(item) {
  if (item.finished) {
    const ok = window.confirm('重新作答将清空该题库进度，确定吗？')
    if (!ok) return
    try {
      await resetProgress(item.id)
    } catch (err) {
      error.value = err?.message || String(err)
      return
    }
  }
  router.push(`/quiz/${item.id}`)
}

async function remove(item) {
  const ok = window.confirm(`确定删除题库「${item.title}」？`)
  if (!ok) return
  try {
    await deleteQuizSet(item.id)
    await load()
  } catch (err) {
    error.value = err?.message || String(err)
  }
}

async function onCloudClick() {
  syncMsg.value = ''
  if (!auth.value?.token) {
    showAuth.value = true
    return
  }
  syncing.value = true
  error.value = ''
  try {
    const result = await syncCloud()
    syncMsg.value = result.message || '同步完成'
    await load()
  } catch (err) {
    error.value = err?.message || String(err)
  } finally {
    syncing.value = false
  }
}

async function onLogout() {
  const ok = window.confirm('退出后将清理本机题库视图并换新本地身份，云端数据仍保留。确定退出？')
  if (!ok) return
  await logout()
  auth.value = null
  syncMsg.value = ''
  await load()
}

async function onSynced() {
  auth.value = getAuth()
  showAuth.value = false
  await load()
}

function onNicknameSaved(nickname) {
  auth.value = getAuth()
  if (auth.value) {
    auth.value = { ...auth.value, nickname }
  }
}

onMounted(load)
</script>

<template>
  <section>
    <header class="top">
      <div class="brand-block">
        <p class="brand">pdf2q</p>
        <p class="tag">我的题库</p>
        <div class="cloud-row">
          <button class="cloud-chip" type="button" :disabled="syncing" @click="onCloudClick">
            {{ syncing ? '同步中…' : auth ? '☁ 云端同步' : '☁ 云端同步 / 登录' }}
          </button>
          <template v-if="auth">
            <button
              class="user-chip"
              type="button"
              :title="`点击修改昵称 · ${auth.phone}`"
              @click="showNickname = true"
            >
              {{ auth.nickname || auth.phone }}
              <span class="edit-hint" aria-hidden="true">✎</span>
            </button>
            <button class="text-btn" type="button" @click="onLogout">退出</button>
          </template>
        </div>
        <p v-if="syncMsg" class="sync-msg">{{ syncMsg }}</p>
      </div>
      <button class="primary" type="button" @click="router.push('/create')">添加问答</button>
    </header>

    <p v-if="loading" class="muted">加载中…</p>
    <p v-if="error" class="error">{{ error }}</p>

    <div v-if="!loading && !items.length" class="empty panel">
      <p>还没有题库。</p>
      <button class="primary" type="button" @click="router.push('/create')">添加第一份问答</button>
    </div>

    <ul v-if="items.length" class="list">
      <li v-for="item in items" :key="item.id" class="panel card">
        <div class="card-main">
          <h2>{{ item.title }}</h2>
          <p class="muted">{{ progressLabel(item) }}</p>
          <div class="bar">
            <span
              :style="{
                width: item.questionCount
                  ? `${Math.round((item.answeredCount / item.questionCount) * 100)}%`
                  : '0%',
              }"
            />
          </div>
        </div>
        <div class="card-actions">
          <button class="primary" type="button" @click="openQuiz(item)">
            {{ actionLabel(item) }}
          </button>
          <button
            class="ghost"
            type="button"
            :disabled="item.answeredCount <= 0"
            @click="router.push(`/quiz/${item.id}/record`)"
          >
            答题记录
          </button>
          <button class="ghost danger" type="button" @click="remove(item)">删除</button>
        </div>
      </li>
    </ul>

    <CloudAuthModal :open="showAuth" @close="showAuth = false" @synced="onSynced" />
    <NicknameModal
      :open="showNickname"
      :current="auth?.nickname || ''"
      @close="showNickname = false"
      @saved="onNicknameSaved"
    />
  </section>
</template>

<style scoped>
.top {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.brand-block {
  min-width: 0;
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

.cloud-row {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 0.55rem;
  margin-top: 0.75rem;
  margin-left: -0.85rem;
}

.cloud-chip {
  border: 1px solid var(--line);
  background: rgba(255, 255, 255, 0.82);
  border-radius: 999px;
  padding: 0.4rem 0.85rem;
  font-size: 0.88rem;
  font-weight: 600;
  color: var(--ink);
  box-shadow: 0 4px 14px rgba(28, 25, 23, 0.04);
}

.cloud-chip:disabled {
  opacity: 0.55;
  cursor: not-allowed;
}

.user-chip {
  display: inline-flex;
  align-items: center;
  gap: 0.3rem;
  max-width: 10rem;
  border: 0;
  background: transparent;
  padding: 0.2rem 0.15rem;
  font-size: 0.88rem;
  color: var(--muted);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  cursor: pointer;
}

.user-chip:hover {
  color: var(--ink);
}

.edit-hint {
  flex-shrink: 0;
  font-size: 0.78rem;
  opacity: 0.7;
}

.text-btn {
  border: 0;
  background: transparent;
  color: var(--muted);
  padding: 0.2rem 0.15rem;
  font-size: 0.88rem;
}

.text-btn:hover {
  color: var(--bad);
}

.sync-msg {
  margin: 0.4rem 0 0;
  color: var(--warn);
  font-size: 0.88rem;
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
  padding: 0.65rem 0.9rem;
}

.ghost:disabled {
  opacity: 0.45;
  cursor: not-allowed;
}

.danger {
  color: var(--bad);
}

.panel {
  background: var(--card);
  border: 1px solid var(--line);
  border-radius: 18px;
  padding: 1.25rem 1.35rem;
  box-shadow: 0 10px 30px rgba(28, 25, 23, 0.04);
}

.list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: grid;
  gap: 0.85rem;
}

.card {
  display: flex;
  justify-content: space-between;
  gap: 1.25rem;
  align-items: center;
}

.card-main {
  flex: 1;
  min-width: 0;
}

.card h2 {
  margin: 0 0 0.35rem;
  font-size: 1.1rem;
}

.card-actions {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-end;
  align-items: center;
  gap: 0.5rem;
  flex-shrink: 0;
}

.card-actions .primary,
.card-actions .ghost {
  white-space: nowrap;
}

@media (max-width: 640px) {
  .card {
    flex-direction: column;
    align-items: stretch;
  }

  .card-actions {
    justify-content: stretch;
  }

  .card-actions .primary,
  .card-actions .ghost {
    flex: 1;
  }
}

.muted {
  color: var(--muted);
  margin: 0;
}

.error {
  color: var(--bad);
}

.empty {
  text-align: center;
  display: grid;
  gap: 1rem;
  justify-items: center;
}

.bar {
  margin-top: 0.75rem;
  height: 8px;
  background: #f5f5f4;
  border-radius: 999px;
  overflow: hidden;
}

.bar span {
  display: block;
  height: 100%;
  background: var(--accent);
}
</style>
