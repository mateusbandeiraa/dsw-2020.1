<template lang="html">
  <div class="lista-items-compartilhados row" v-if="this.$root.credentials">
    <div class="col-md-10 col-md-offset-1 text-left">
      <div>
        <div class="header">
          <h2 class="form-title">Itens Compartilhados</h2>
          <h6 class="form-subtitle">Abaixo estão os seus itens compartilhados.</h6>
        </div>
        
        <div class="new-button">
          <button type="button" class="btn btn-primary" @click="novo">Novo Item</button>
        </div>
        <div class="clear"></div>
      </div>

      <div class="form-inline">
        <input class="form-control" type="search" placeholder="Pesquisar" aria-label="Search" id="inputFiltro" v-model="filtro" v-on:keyup.enter="processForm">
        <button class="btn btn-outline-success" @click="processForm()">Pesquisar</button>
      </div>

      <table class="table table-striped" id="tbItens">
      <thead>
        <tr>
          <th>Nome</th>
          <th>Descrição</th>
          <th>Tipo</th>
          <th class="commands"></th>
        </tr>
      </thead>

      <tbody>
        <tr v-for="item in items">
          <td>{{item.nome}}</td>
          <td>{{item.descricao}}</td>
          <td>{{item.tipo}}</td>
          <td class="commands">
            <span class="glyphicon glyphicon-pencil" aria-hidden="true" title="Editar" @click="edita(item)"></span>
            <span class="glyphicon glyphicon-eye-open" aria-hidden="true" title="Detalhes" style="color: blue" @click="detalhe(item)"></span>
            <span class="glyphicon glyphicon-remove" aria-hidden="true" title="Cancelar" style="color: red" @click="remove(item)"></span>
          </td>
        </tr>
      </tbody>
      </table>

      <div>
        <div class="page-item first" :class="{ disable: this.page == 1 }" @click="moveTo(page-1)">&lt;&lt;</div>
        <div class="page-item" v-if="page > 3" @click="moveTo(page-3)">{{page-3}}</div>
        <div class="page-item" v-if="page > 2" @click="moveTo(page-2)">{{page-2}}</div>
        <div class="page-item" v-if="page > 1" @click="moveTo(page-1)">{{page-1}}</div>
        <div class="page-item current disable">{{page}}</div>
        <div class="page-item" v-if="totalPages > page"   @click="moveTo(page+1)">{{page+1}}</div>
        <div class="page-item" v-if="totalPages > page+1" @click="moveTo(page+2)">{{page+2}}</div>
        <div class="page-item" v-if="totalPages > page+2" @click="moveTo(page+3)">{{page+3}}</div>
        <div class="page-item last" :class="{ disable: this.page == this.totalPages }" @click="moveTo(page+1)">&gt;&gt;</div>
        <div class="clear"></div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      page: 1,
      totalPages: 1,
      items: [],
      filtro: '',

      httpOptions: {
          baseURL: this.$root.config.url,
          headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + this.$root.credentials.token
          }
      },
    }
  },

  created: function () {
    this.processForm();
  },  

  methods: {
    processForm: function() {
      axios.get(`/api/item/lista?sort=&per_page=10&page=${this.page}&filter=${this.filtro}`,this.httpOptions)
        .then(response => {
          this.items = response.data.data.data;
          this.page = response.data.data.current_page;
          this.totalPages = response.data.data.last_page;
          this.error = {};
        })
        .catch(error => {
          this.error = error.response.data.errors;
        });
    },

    moveTo: function(page) {
      if (page < 1) 
        page = 1;

      if (page > this.totalPages) 
        page = this.totalPages;

      this.page = page;
      this.processForm();
    },

    novo: function() {
      this.$router.push({ name: 'item-new' });
    },

    edita: function(item) {
      this.$router.push({
          name: 'item-update',
          params: { item: item }
      });
    },

    detalhe: function (item) {
      this.$router.push({
          name: 'item-details',
          params: { item: item }
      });
    },

    remove: function(item) {
      this.$router.push({
          name: 'item-delete',
          params: { item: item }
      });
    },
  }
}
</script>

<style lang="css" scoped>
div.lista-items-compartilhados {
  margin-top: 32px;
}
td.commands {
  text-align: right;
}
td.commands > span {
  margin: 0 5px;
}
div.page-item {
  color: #2973b7;
  text-decoration: none;
  cursor: pointer;
  padding: 10px;
  border: 1px solid lightgray;
  margin: 0px -1px 0px 0px;
  float: left;
}
div.page-item.first {
  border-top-left-radius: 8px;
  border-bottom-left-radius: 8px;
}
div.page-item.last {
  border-top-right-radius: 8px;
  border-bottom-right-radius: 8px;
}
div.page-item.disable {
  color: gray;
  cursor: auto;
}
div.page-item.current {
  background-color: lightgray;
}
div.clear {
  clear: both;
}
div.header {
  float: left;
}
div.new-button {
  float: right;
  text-align: right;
}
</style>