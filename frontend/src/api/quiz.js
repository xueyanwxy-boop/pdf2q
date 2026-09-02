export async function generateQuiz(text, count = 10) {
  const res = await fetch('/api/generate', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ text, count }),
  })

  const data = await res.json().catch(() => ({}))
  if (!res.ok) {
    throw new Error(data.error || `生成失败（${res.status}）`)
  }
  if (!Array.isArray(data.questions) || data.questions.length === 0) {
    throw new Error('后端未返回题目')
  }
  return data.questions
}
