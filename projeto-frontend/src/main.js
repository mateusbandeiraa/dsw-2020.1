import Vue from 'vue'
import Router from 'vue-router'

import App from './App.vue'
import Homepage from './components/Homepage.vue'
import Login from './components/login/login/Login.vue'

/* Criacao de conta */
import CriacaoConta from './components/login/criacao-conta/CriacaoConta.vue'
import CriacaoContaSucesso from './components/login/criacao-conta/CriacaoContaSucesso.vue'

/* Esquecimento de senha */
import EsquecimentoSenha from './components/login/esquecimento-senha/EsquecimentoSenha.vue'
import EsquecimentoSenhaSucesso from './components/login/esquecimento-senha/EsquecimentoSenhaSucesso.vue'
import RecuperacaoSenha from './components/login/esquecimento-senha/RecuperacaoSenha.vue'
import RecuperacaoSenhaSucesso from './components/login/esquecimento-senha/RecuperacaoSenhaSucesso.vue'

/* Troca de senha */
import TrocaSenha from './components/login/troca-senha/TrocaSenha.vue'
import TrocaSenhaSucesso from './components/login/troca-senha/TrocaSenhaSucesso.vue'

/* Itens compartilhados */
import ListaItemCompartilhado from './components/item/ListaItemCompartilhado.vue'
import NovoItemCompartilhado from './components/item/NovoItemCompartilhado.vue'
import AtualizaItemCompartilhado from './components/item/AtualizaItemCompartilhado.vue'
import RemoveItemCompartilhado from './components/item/RemoveItemCompartilhado.vue'
import DetalhesItemCompartilhado from './components/item/DetalhesItemCompartilhado.vue'

Vue.use(Router)

const router = new Router({
  mode: 'history',
  routes: [
    {
      path: '*',
      redirect: '/'
    },
    {
      path: '/',
      name: 'home',
      component: Homepage
    },
    {
      path: '/login',
      name: 'login',
      component: Login,
    },
    {
      path: '/login/new',
      name: 'create-account',
      component: CriacaoConta,
    },
    {
      path: '/login/account-created',
      name: 'account-created',
      component: CriacaoContaSucesso,
    },
    {
      path: '/login/forgot',
      name: 'forgot-password',
      component: EsquecimentoSenha,
    },
    {
      path: '/login/token-sent',
      name: 'token-sent',
      component: EsquecimentoSenhaSucesso,
    },  
    {
      path: '/login/reset',
      name: 'reset-password',
      component: RecuperacaoSenha,
    },
    {
      path: '/login/reseted',
      name: 'password-reseted',
      component: RecuperacaoSenhaSucesso,
    },  
    {
      path: '/login/change',
      name: 'change-password',
      component: TrocaSenha,
      meta: {
        requiresAuth: true
      }
    },
    {
      path: '/login/changed',
      name: 'password-changed',
      component: TrocaSenhaSucesso,
      meta: {
        requiresAuth: true
      }
    },
    {
      path: '/item/list',
      name: 'item-list',
      component: ListaItemCompartilhado,
      meta: {
        requiresAuth: true
      }
    },
    {
      path: '/item/update',
      name: 'item-update',
      component: AtualizaItemCompartilhado,
      props: true,
      meta: {
        requiresAuth: true
      }
    },
    {
      path: '/item/new',
      name: 'item-new',
      component: NovoItemCompartilhado,
      meta: {
        requiresAuth: true
      }
    },
    {
      path: '/item/delete',
      name: 'item-delete',
      component: RemoveItemCompartilhado,
      props: true,
      meta: {
        requiresAuth: true
      }
    },
    {
      path: '/item/details',
      name: 'item-details',
      component: DetalhesItemCompartilhado,
      props: true,
      meta: {
        requiresAuth: true
      }
    }
  ]
})

let vueObj = {}

router.beforeEach((to, from, next) => {
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth);

  if (requiresAuth && !vueObj.credentials) next('login')
  else next();
})

vueObj = new Vue({
  data: {
    credentials: null,
    config: {
      url: "http://localhost:8080"
    }
  },

 el: '#app',
 render: h => h(App),
 router
})
