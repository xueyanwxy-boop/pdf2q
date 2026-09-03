import { createRouter, createWebHistory } from 'vue-router'
import QuizListView from './views/QuizListView.vue'
import CreateQuizView from './views/CreateQuizView.vue'
import QuizView from './views/QuizView.vue'
import QuizRecordView from './views/QuizRecordView.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', name: 'home', component: QuizListView },
    { path: '/create', name: 'create', component: CreateQuizView },
    { path: '/quiz/:id', name: 'quiz', component: QuizView, props: true },
    { path: '/quiz/:id/record', name: 'quiz-record', component: QuizRecordView, props: true },
  ],
})

export default router
