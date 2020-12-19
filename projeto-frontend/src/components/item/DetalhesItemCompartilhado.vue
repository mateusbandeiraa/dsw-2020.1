<template lang="html">
  <div class="form-items-compartilhados row" v-if="this.$root.credentials">
    <div class="col-md-10 col-md-offset-1 text-left">
      <h2 class="form-title">Detalhes de item compartilhado</h2>
      <h3>{{item.nome}}</h3>
      <h4>Descrição</h4>
      <p>{{item.descricao}}</p>
      <h4>Tipo</h4>
      <p>{{item.tipo}}</p>
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

      httpOptions: {
        baseURL: this.$root.config.url,
        headers: {
          Accept: "application/json",
          "Content-Type": "application/json",
          Authorization: "Bearer " + this.$root.credentials.token,
        },
      },
    };
  },

  methods: {
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