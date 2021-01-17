<template lang="html">
  <div>
    <div class="login">
      <div class="row jumbotron-row">
        <div class="col-md-offset-1 col-md-10">
          <div class="jumbotron text-center">
            <h1>Jumbotron heading</h1>
            <p class="lead">Cras justo odio, dapibus ac facilisis in, egestas eget quam. Fusce dapibus, tellus ac cursus commodo, tortor mauris condimentum nibh, ut fermentum massa justo sit amet risus.</p>

            <p v-if="!$root.credentials">
              <router-link class="link" :to="{ name: 'login' }">
                <a class="btn btn-lg btn-success" href="#" role="button">Login</a>
              </router-link>
            </p>
            <div v-else>
              <p v-if="qtdItensAbertos > 0" style="color: red">
                Você tem {{qtdItensAbertos}} itens recebidos abertos.
              </p>
              <p v-else style="color: blue">
                Você não tem itens recebidos abertos.
              </p>
            </div>
          </div>
        </div>
      </div>
  
      <div class="row marketing">
        <div class="col-md-offset-1 col-md-4">
          <h4>Subheading</h4>
          <p>Donec id elit non mi porta gravida at eget metus. Maecenas faucibus mollis interdum. Donec id elit non mi porta gravida at eget metus. Maecenas faucibus mollis interdum.</p>

          <h4>Subheading</h4>
          <p>Morbi leo risus, porta ac consectetur ac, vestibulum at eros. Cras mattis consectetur purus sit amet fermentum. Morbi leo risus, porta ac consectetur ac, vestibulum at eros. Cras mattis consectetur purus sit amet fermentum.</p>

          <h4>Subheading</h4>
          <p>Maecenas sed diam eget risus varius blandit sit amet non magna, consectetur ac, vestibulum at eros. Maecenas sed diam eget risus varius blandit sit amet non magna, consectetur ac, vestibulum at eros. Maecenas sed diam eget risus varius blandit sit amet non magna, consectetur ac, vestibulum at eros. </p>
        </div>

        <div class="col-md-offset-2 col-md-4">
          <h4>Subheading</h4>
          <p>Donec id elit non mi porta gravida at eget metus. Maecenas faucibus mollis interdum. Donec id elit non mi porta gravida at eget metus. Maecenas faucibus mollis interdum.</p>

          <h4>Subheading</h4>
          <p>Morbi leo risus, porta ac consectetur ac, vestibulum at eros. Cras mattis consectetur purus sit amet fermentum. Morbi leo risus, porta ac consectetur ac, vestibulum at eros. Cras mattis consectetur purus sit amet fermentum.</p>

          <h4>Subheading</h4>
          <p>Maecenas sed diam eget risus varius blandit sit amet non magna, consectetur ac, vestibulum at eros. Maecenas sed diam eget risus varius blandit sit amet non magna, consectetur ac, vestibulum at eros. Maecenas sed diam eget risus varius blandit sit amet non magna, consectetur ac, vestibulum at eros. </p>
        </div>
      </div>
    </div>

    <div class="row footer-row">
      <div class="col-md-offset-1 col-md-10">
        <div class="footer">
          <p>©2020 UNIRIO, Universidade Federal do Estado do Rio de Janeiro</p>
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
        qtdItensAbertos: 0,

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

    created: function() {
      axios.get(`/api/compartilhamento/?idUsuario=${this.$root.credentials.id}`,this.httpOptions)
        .then(response => {
          this.qtdItensAbertos = response.data.data.filter(item => item.status == "ABERTO").length;
        })
        .catch(error => {
          this.error = error.response.data.errors;
        });
    }
  }
</script>

<style lang="css" scoped>
div.jumbotron-row {
  margin-top: 32px;
}
.marketing h4 {
  font-size: 20px;
}
.footer {
    margin-top: 64px;
    margin-bottom: 32px;
    padding-top: 8px;
    color: #777;
    border-top: 1px solid #e5e5e5;
}
</style>
