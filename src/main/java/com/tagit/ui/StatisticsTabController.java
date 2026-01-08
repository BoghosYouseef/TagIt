package com.tagit.ui;

import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.BarChart;

public class StatisticsTabController {
    @FXML
    private PieChart fileTypeChart;

    @FXML
    private BarChart<?, ?> tagFrequencyChart;

    @FXML
    public void initialize() {
        // #TODO: initialize charts with data from service
        // #TODO: bind data to pie chart (file types)
        // #TODO: bind data to bar chart (tag frequency)
    }
}
