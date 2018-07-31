package com.ampro.evemu.util.visualization

import javafx.application.Application
import javafx.scene.chart.CategoryAxis
import javafx.scene.chart.NumberAxis
import tornadofx.*

fun main(args: Array<String>) {
    Application.launch(ChartApp::class.java, *args)
}

class ChartApp : App() {
    override val primaryView = TestView::class
}

class TestView : View("My View") {
    override val root =
            linechart("Unit Sales Q2 2016", CategoryAxis(), NumberAxis()) {
                series("Product X") {
                    data("MAR", 10245)
                    data("APR", 23963)
                    data("MAY", 15038)
                }
                series("Product Y") {
                    data("MAR", 28443)
                    data("APR", 22845)
                    data("MAY", 19045)
                }
            }
}
