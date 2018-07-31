package com.ampro.evemu.util.visualization

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.chart.LineChart
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.stage.Stage
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking


class LineChartSample : Application() {

    override fun start(stage: Stage) {
        stage.title = "Line Chart Sample"
        //defining the axes
        val xAxis = NumberAxis()
        val yAxis = NumberAxis()
        xAxis.label = "Number of Month"
        //creating the chart
        val lineChart = LineChart(xAxis, yAxis)

        lineChart.title = "Stock Monitoring, 2010"
        //defining a series
        val series = XYChart.Series<Number, Number>()
        series.name = "My portfolio"
        //populating the series with data
        series.data.add(XYChart.Data(1.0, 23.0))
        series.data.add(XYChart.Data(2.0, 14.0))
        series.data.add(XYChart.Data(3.0, 15.0))
        series.data.add(XYChart.Data(4.0, 24.0))
        series.data.add(XYChart.Data(5.0, 34.0))
        series.data.add(XYChart.Data(6.0, 36.0))
        series.data.add(XYChart.Data(7.0, 22.0))

        val scene = Scene(lineChart, 800.0, 600.0)
        lineChart.data.add(series)

        stage.scene = scene
        stage.show()
        series.data.add(XYChart.Data(20.0, 1.0))
    }
}

fun main(args: Array<String>) = runBlocking {
    Application.launch(LineChartSample::class.java)
    delay(1000)
    
}
