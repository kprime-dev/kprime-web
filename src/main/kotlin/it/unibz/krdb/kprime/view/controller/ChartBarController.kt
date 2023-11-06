package it.unibz.krdb.kprime.view.controller

import io.javalin.http.Handler
import it.unibz.krdb.kprime.domain.DataService
import unibz.cs.semint.kprime.domain.ddl.*

interface ChartBarHandlers {
    val getStackedChart: Handler
    val getStackedChartEntity: Handler
}

class ChartBarController(val dataService: DataService) : ChartBarHandlers {


    /*
    var barChartData = {
        labels: ['January', 'February', 'March', 'April', 'May', 'June', 'July'],
        datasets: [{
            label: 'Dataset 1',
            backgroundColor: window.chartColors.red,
            data: [
                randomScalingFactor(),
                randomScalingFactor(),
                randomScalingFactor(),
                randomScalingFactor(),
                randomScalingFactor(),
                randomScalingFactor(),
                randomScalingFactor()
            ]
        }, {
            label: 'Dataset 2',
            backgroundColor: window.chartColors.blue,
            data: [
                randomScalingFactor(),
                randomScalingFactor(),
                randomScalingFactor(),
                randomScalingFactor(),
                randomScalingFactor(),
                randomScalingFactor(),
                randomScalingFactor()
            ]
        }, {
            label: 'Dataset 3',
            backgroundColor: window.chartColors.green,
            data: [
                randomScalingFactor(),
                randomScalingFactor(),
                randomScalingFactor(),
                randomScalingFactor(),
                randomScalingFactor(),
                randomScalingFactor(),
                randomScalingFactor()
            ]
        }]

    };
     */

    data class DataSetDimensions(
        val tables_plus:Int,
        val tables_minus:Int,
        val views_plus:Int,
        val views_minus:Int,
        val constraints_plus:Int,
        val constraints_minus:Int,
        val mappings_plus:Int,
        val mappings_minus:Int
    )
    data class DataSet(val label:String, val backgroundColor:String,val data:MutableList<Int>)
    data class BarChartData(val labels:MutableList<String>,val datasets:List<DataSet>)

    override val getStackedChart = Handler { ctx ->
        val traceName = ctx.pathParam("traceName")
        var fileContent = ChartTraceController::class.java.getResource("/public/barchart.html").readText()

        val barChartUrl= "/barchart/entity/"+traceName
        fileContent = fileContent.replace("{{barChartUrl}}",barChartUrl)
        ctx.html(fileContent)

    }

    override val getStackedChartEntity = Handler { ctx ->
        val traceName = ctx.pathParam("traceName")
        ctx.sessionAttribute<String>("")

        val changeSetList = dataService.getChangeSets(traceName)

        var barChartData =
                BarChartData(mutableListOf(),
                        listOf(
                                DataSet("+tables","rgba(0,100,0,0.5)",
                                        mutableListOf()),
                                DataSet("-tables","rgba(200,100,0,0.5)",
                                        mutableListOf()),
                                DataSet("+views","rgba(0,200,200,0.5)",
                                        mutableListOf()),
                                DataSet("-views","rgba(200,200,200,0.5)",
                                        mutableListOf()),
                                DataSet("+mappings","rgba(0,100,200,0.5)",
                                        mutableListOf()),
                                DataSet("-mappings","rgba(255,0,0,0.5)",
                                        mutableListOf()),
                                DataSet("+constraints","rgba(0,50,150,0.5)",
                                        mutableListOf()),
                                DataSet("-constraints","rgba(200,50,150,0.5)",
                                        mutableListOf())
                        )
                )


        var index = 0
        for (changeSet in changeSetList) {
            index++
            val dataSetDimensions = computeDataSetDimensions(changeSet)
            barChartData.labels.add(changeSet.id)
            barChartData.datasets[0].data.add(dataSetDimensions.tables_plus)
            barChartData.datasets[1].data.add(dataSetDimensions.tables_minus)
            barChartData.datasets[2].data.add(dataSetDimensions.views_plus)
            barChartData.datasets[3].data.add(dataSetDimensions.views_minus)
            barChartData.datasets[4].data.add(dataSetDimensions.mappings_plus)
            barChartData.datasets[5].data.add(dataSetDimensions.mappings_minus)
            barChartData.datasets[6].data.add(dataSetDimensions.constraints_plus)
            barChartData.datasets[7].data.add(dataSetDimensions.constraints_minus)
        }
        ctx.json(barChartData)
    }

    private fun computeDataSetDimensions(changeSet:ChangeSet) : DataSetDimensions {

        return  DataSetDimensions(
                changeSet.createTable.size,         -1 *changeSet.dropTable.size,
                changeSet.createView.size,          -1 *changeSet.dropView.size,
                changeSet.createConstraint.size,    -1 *changeSet.dropConstraint.size,
                changeSet.createMapping.size,       -1 *changeSet.dropMapping.size)
    }
}