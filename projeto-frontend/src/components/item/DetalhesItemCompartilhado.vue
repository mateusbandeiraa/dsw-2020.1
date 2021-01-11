<template lang="html">
  <div class="form-items-compartilhados row" v-if="this.$root.credentials">
    <div class="col-md-10 col-md-offset-1 text-left">
      <div>
        <h2 class="form-title">Detalhes de item compartilhado</h2>
        <h3>{{item.nome}}</h3>
        <h4>Descrição</h4>
        <p>{{item.descricao}}</p>
        <h4>Tipo</h4>
        <p>{{item.tipo}}</p>
      </div>

      <table class="table table-striped" id="tbItens">
        <thead>
          <tr>
            <th>Data Início</th>
            <th>Data Término</th>
            <th>Usuário</th>
            <th>Status</th>
            <th class="commands"></th>
          </tr>
        </thead>

        <tbody>
          <tr v-for="compartilhamento in compartilhamentos">
            <td>{{readableDate(compartilhamento.dataInicio)}}</td>
            <td>{{readableDate(compartilhamento.dataTermino)}}</td>
            <td>{{compartilhamento.nomeUsuario}}</td>
            <td>{{compartilhamento.status}}</td>
            <td class="commands">
              <span v-if="!compartilhamento.status.includes('CANCELADO')" class="glyphicon glyphicon-remove" aria-hidden="true" @click="remove(compartilhamento)"></span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script>
import axios from "axios";

export default {
  props: ["item"],

  data() {
    return {
      error: {},
      success: false,
      compartilhamentos: {},

      httpOptions: {
          baseURL: this.$root.config.url,
          headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + this.$root.credentials.token
          }
      },
    };
  },

  created: function () {
    this.getCompartilhamentos();
  },

  methods: {
    getCompartilhamentos: function () {
      axios
        .get(`/api/compartilhamento/?idItem=${this.item.id}`, this.httpOptions)
        .then(response => {
          this.compartilhamentos = response.data.data;
        })
    },
    processForm: function () {
      axios
        .post("/api/item/atualiza", this.item, this.httpOptions)
        .then((response) => {
          this.success = true;
          this.error = {};
          setTimeout(this.goBackToList, 3000);
        })
        .catch((error) => {
          this.error = error.response.data.errors;
        });
    },

    goBackToList: function () {
      this.$router.replace("/item/list");
    },

    remove: function(compartilhamento) {
      this.$root.credentials;
      axios
        .get(`/api/compartilhamento/${compartilhamento.id}/status/?status=CANCELADO`, this.httpOptions)
        .then(response => {
          this.compartilhamentos = response.data.data;
        })
    },

    readableDate(date){
      return new Intl.DateTimeFormat().format(new Date(date));
    }
  },
};
</script>

<style lang="css" scoped>
div.form-items-compartilhados {
  margin-top: 32px;
}
div.success {
  border: 1px solid green;
  background: lightgreen;
  padding: 8px;
  margin-bottom: 24px;
}
</style>