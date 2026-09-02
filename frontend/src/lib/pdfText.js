import { getDocument, GlobalWorkerOptions } from 'pdfjs-dist'
import pdfWorker from 'pdfjs-dist/build/pdf.worker.min.mjs?url'

GlobalWorkerOptions.workerSrc = pdfWorker

/**
 * Extract plain text from a PDF File using pdf.js (browser-side).
 */
export async function extractTextFromPdf(file) {
  const buffer = await file.arrayBuffer()
  const pdf = await getDocument({ data: buffer }).promise
  const parts = []

  for (let pageNum = 1; pageNum <= pdf.numPages; pageNum += 1) {
    const page = await pdf.getPage(pageNum)
    const content = await page.getTextContent()
    const pageText = content.items.map((item) => ('str' in item ? item.str : '')).join(' ')
    if (pageText.trim()) {
      parts.push(pageText.trim())
    }
  }

  const text = parts.join('\n\n').replace(/[ \t]+\n/g, '\n').trim()
  if (!text) {
    throw new Error('未能从 PDF 提取到文字。请使用可选中文字的 PDF（扫描件暂不支持）。')
  }
  return text
}
