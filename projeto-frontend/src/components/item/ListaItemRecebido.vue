<template lang="html">
  <div class="lista-items-compartilhados row" v-if="this.$root.credentials">
    <div class="col-md-10 col-md-offset-1 text-left">
      <div>
        <div class="header">
          <h2 class="form-title">Itens Recebidos</h2>
          <h6 class="form-subtitle">Abaixo estão os seus itens recebidos.</h6>
        </div>
      </div>

      <div class="form-inline">
        <input class="form-control" type="search" placeholder="Pesquisar" aria-label="Search" id="inputFiltro" v-model="filtro" v-on:keyup.enter="processForm">
        <button class="btn btn-outline-success" @click="processForm()">Pesquisar</button>
      </div>

      <table class="table table-striped" id="tbItens">
        <thead>
            <tr>
                <th>Nome</th>
                <th>Dono</th>
                <th>Data Início</th>
                <th>Data Término</th>
                <th>Status</th>
                <th class="commands"></th>
            </tr>
        </thead>

        <tbody>
            <tr v-for="item in items">
                <td>{{item.itemCompartilhado.nome}}</td>
                <td>{{item.nomeDono}}</td>
                <td>{{item.dataInicio | readableDate}}</td>
                <td>{{item.dataTermino | readableDate}}</td>
                <td>{{item.status}}</td>
                <td class="commands">
                    <span v-if="item.status == 'ABERTO'" class="glyphicon glyphicon-ok" aria-hidden="true" title="Aceitar" @click="aceita(item)"></span>
                    <span v-if="item.status == 'ABERTO'" class="glyphicon glyphicon-remove" aria-hidden="true" title="Rejeitar" @click="rejeita(item)"></span>
                </td>
            </tr>
        </tbody>
      </table>

      <div v-if="checkRejeita">
        <span>Tem certeza que deseja rejeitar este compartilhamento?</span>
        <button class="btn btn-danger" @click="confirmaRejeita(toRemove)">Sim</button>
      </div>

      <div v-if="checkAceita">
          <span>Tem certeza que deseja aceitar este compartilhamento?</span>
          <button class="btn btn-success" @click="del(toRemove)">Sim</button>
        </div>
      </div>
    </div>

  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      items: [],
      filtro: '',
      checkRejeita: false,
      checkAceita: false,
      toRejeita: {},
      toAceita: {},
      error: {},

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
      axios.get(`/api/compartilhamento/?idUsuario=${this.$root.credentials.id}`,this.httpOptions)
        .then(response => {
          this.items = response.data.data;
        })
        .catch(error => {
          this.error = error.response.data.errors;
        });
    },

    novo: function() {
      this.$router.push({ name: 'item-new' });
    },

    confirma: function(compartilhamento) {
      this.checkAceita = false;

      axios
        .post(`/api/compartilhamento/${compartilhamento.id}/status/`, {status:"ACEITO"}, this.httpOptions)
        .then(response => {
          compartilhamento.status = response.data.data.status;
        })
        .catch((error) => {
          this.error[compartilhamento.id] = error.response.data.errors;
        });
    },

    aceita: function(compartilhamento) {
      this.toAceita = compartilhamento
      this.checkRejeita = false;
      this.checkAceita = true;
    },

    rejeita: function(compartilhamento) {
      this.toRejeita = compartilhamento
      this.checkAceita = false;
      this.checkRejeita = true;
    },

    detalhe: function (item) {
      this.$router.push({
          name: 'item-details',
          params: { item: item }
      });
    },

    confirmaRejeita: function(compartilhamento) {
      this.checkRejeita = false;

      axios
        .post(`/api/compartilhamento/${compartilhamento.id}/status/`, {status:"REJEITADO"}, this.httpOptions)
        .then(response => {
          compartilhamento.status = response.data.data.status;
        })
        .catch((error) => {
          this.error[compartilhamento.id] = error.response.data.errors;
        });
    },
  },

  filters: {
    readableDate(date){
      return new Date(date).toLocaleDateString('pt-BR', {
        timeZone:"UTC"
      });
    }
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