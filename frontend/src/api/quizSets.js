import { api } from './client'

export function listQuizSets() {
  return api('/api/quiz-sets')
}

export function createQuizSet({ title, text, singleCount, multipleCount, judgeCount }) {
  return api('/api/quiz-sets', {
    method: 'POST',
    body: JSON.stringify({ title, text, singleCount, multipleCount, judgeCount }),
  })
}

export function getQuizSet(id) {
  return api(`/api/quiz-sets/${id}`)
}

export function saveProgress(id, { currentIndex, answers }) {
  return api(`/api/quiz-sets/${id}/progress`, {
    method: 'PUT',
    body: JSON.stringify({ currentIndex, answers }),
  })
}

export function resetProgress(id) {
  return api(`/api/quiz-sets/${id}/progress/reset`, { method: 'POST' })
}

export function deleteQuizSet(id) {
  return api(`/api/quiz-sets/${id}`, { method: 'DELETE' })
}
