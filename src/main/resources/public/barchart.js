    var barChartDataEmpty = {
        labels: [],
        datasets: []
    }

    window.onload = function() {

        var ctx = document.getElementById('canvas').getContext('2d');
        window.myBar = new Chart(ctx, {
            type: 'horizontalBar',
            data: barChartDataEmpty,
            options: {
                title: {
                    display: true,
                    text: 'Change Sets'
                },
                tooltips: {
                    mode: 'index',
                    intersect: false
                },
                responsive: true,
                scales: {
                    xAxes: [{
                        stacked: true,
                    }],
                    yAxes: [{
                        stacked: true
                    }]
                }
            }
        });
    };


    var appBarChart = new Vue({
      el: '#app-barchart',

      data: {
        barChartUrlVue: barChartUrl,
        barChartDataVue: barChartDataEmpty,
        successMessage : '',
        errorMessage : '',
        settings: { workingDir:''}
      },

      created: function () {
            console.log('fetchBarChart: '+this.barChartUrlVue)
              this.$http.get(this.barChartUrlVue)
              .then((response) => {
                    this.barChartDataVue.labels = response.body.labels;
                    this.barChartDataVue.datasets = response.body.datasets;
       			    window.myBar.update();
                    this.errorMessage = ''
                    this.successMessage = 'OK'
                  console.log("barchart updated");
              })
              .catch((response) => {
                    this.errorMessage = 'KO: '+response.body.message
                    this.successMessage = null
              });
      },


      methods: {

          vueScalingFactor: function () {
            console.log('vueScalingFactor')
              this.$http.get(this.barChartUrlVue)
              .then((response) => {
                    this.barChartDataVue.labels = response.body.labels;
                    this.barChartDataVue.datasets = response.body.datasets;
       			    window.myBar.update();
                    this.errorMessage = ''
                    this.successMessage = 'OK'
                  console.log("barchart updated");
              })
              .catch((response) => {
                    this.errorMessage = 'KO: '+response
                    this.successMessage = null
              });
          },

        },
    })