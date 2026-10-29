package com.tagit.analytics.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;

import com.tagit.analytics.service.AnalyticsService;
import com.tagit.analytics.service.AnalyticsService.TagDistribution;
import com.tagit.file.FileMetaDataUtils;
import com.tagit.utils.ColorUtils;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;


@Controller
public class AnalyticsTabController {
    private final AnalyticsService analyticsService;
    private final List<TagDistribution> tagDistributions;
    private final Map<Long, CheckBox> tagCheckboxes = new LinkedHashMap<>();
    private PieChart pieChart;
    private VBox chartLegend;
    private RadioButton fileCountButton;
    private RadioButton percentageButton;

    @FXML
    private VBox content;

    public AnalyticsTabController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
        this.tagDistributions = analyticsService.getTagDistribution();
    }

    @FXML
    public void initialize() {
        content.getChildren().add(createTagDistributionPieChart());
        updatePieChart();
    }

    private HBox createTagDistributionPieChart() {
        HBox pieChartSection = new HBox(16);
        pieChartSection.setMaxWidth(Double.MAX_VALUE);
        pieChartSection.setMinHeight(500.00);
        pieChart = new PieChart(FXCollections.observableArrayList());
        pieChart.setTitle("Files by tag");
        pieChart.setLabelsVisible(true);
        pieChart.setLegendVisible(false);
        HBox.setHgrow(pieChart, Priority.ALWAYS);

        chartLegend = new VBox(8);
        chartLegend.getStyleClass().add("analytics-legend");
        Label legendTitle = new Label("Tags");
        legendTitle.getStyleClass().add("analytics-legend-title");
        chartLegend.getChildren().add(legendTitle);

        VBox pieChartControls = new VBox(8);
        pieChartControls.getStyleClass().add("label");
        pieChartControls.getChildren().add(new Label("Measure"));
        ToggleGroup measureGroup = new ToggleGroup();
        fileCountButton = createOption("Number of files per tag", measureGroup, true);
        RadioButton totalSizeButton = createOption("Total file size per tag", measureGroup, false);
        measureGroup.selectedToggleProperty().addListener(this::onOptionChanged);
        pieChartControls.getChildren().addAll(fileCountButton, totalSizeButton);

        pieChartControls.getChildren().add(new Label("Labels"));
        ToggleGroup displayGroup = new ToggleGroup();
        percentageButton = createOption("Percentage", displayGroup, true);
        RadioButton absoluteButton = createOption("Absolute numbers", displayGroup, false);
        displayGroup.selectedToggleProperty().addListener(this::onOptionChanged);
        pieChartControls.getChildren().addAll(percentageButton, absoluteButton);

        pieChartControls.getChildren().add(new Label("Included tags"));
        VBox tagOptions = new VBox(4);
        for (TagDistribution distribution : tagDistributions) {
            CheckBox tagCheckbox = new CheckBox(distribution.tag().getText());
            tagCheckbox.setSelected(true);
            tagCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> updatePieChart());
            tagCheckboxes.put(distribution.tag().getId(), tagCheckbox);
            tagOptions.getChildren().add(tagCheckbox);
        }

        ScrollPane tagScrollPane = new ScrollPane(tagOptions);
        tagScrollPane.setFitToWidth(true);
        tagScrollPane.setPrefViewportHeight(240);
        VBox.setVgrow(tagScrollPane, Priority.ALWAYS);
        pieChartControls.getChildren().add(tagScrollPane);
        pieChartControls.setPrefWidth(240);
        pieChartSection.getChildren().addAll(pieChartControls, pieChart, chartLegend);
        return pieChartSection;
    }

    private RadioButton createOption(String text, ToggleGroup group, boolean selected) {
        RadioButton option = new RadioButton(text);
        option.setToggleGroup(group);
        option.setSelected(selected);
        return option;
    }

    private void onOptionChanged(
        ObservableValue<? extends Toggle> observable,
        Toggle oldValue,
        Toggle newValue
    ) {
        if (newValue != null) {
            updatePieChart();
        }
    }

    private void updatePieChart() {
        if (pieChart == null || fileCountButton == null || percentageButton == null) {
            return;
        }

        boolean showFileCount = fileCountButton.isSelected();
        boolean showPercentage = percentageButton.isSelected();
        double total = tagDistributions.stream()
            .filter(distribution -> isTagIncluded(distribution))
            .mapToDouble(distribution -> getValue(distribution, showFileCount))
            .sum();

        ObservableList<PieChart.Data> chartData = FXCollections.observableArrayList();
        chartLegend.getChildren().remove(1, chartLegend.getChildren().size());
        for (TagDistribution distribution : tagDistributions) {
            if (!isTagIncluded(distribution)) {
                continue;
            }

            double value = getValue(distribution, showFileCount);
            String label = showPercentage
                ? String.format("%s (%.1f%%)", distribution.tag().getText(), total == 0 ? 0 : value / total * 100)
                : String.format("%s (%s)", distribution.tag().getText(), formatAbsoluteValue(distribution, showFileCount));
            PieChart.Data slice = new PieChart.Data(label, value);
            String tagColor = ColorUtils.toCssColor(distribution.tag().getColorHashString());
            slice.nodeProperty().addListener((observable, oldNode, newNode) -> applyTagColor(newNode, tagColor));
            chartData.add(slice);

            Rectangle swatch = new Rectangle(12, 12, Color.web(tagColor));
            swatch.getStyleClass().add("analytics-legend-swatch");
            Label tagLabel = new Label(distribution.tag().getText());
            tagLabel.getStyleClass().add("analytics-legend-label");
            HBox legendItem = new HBox(8, swatch, tagLabel);
            legendItem.getStyleClass().add("analytics-legend-item");
            chartLegend.getChildren().add(legendItem);
        }
        pieChart.setData(chartData);
        pieChart.setTitle(showFileCount ? "Files by tag" : "File size by tag");
    }

    private boolean isTagIncluded(TagDistribution distribution) {
        CheckBox checkbox = tagCheckboxes.get(distribution.tag().getId());
        return checkbox != null && checkbox.isSelected();
    }

    private double getValue(TagDistribution distribution, boolean showFileCount) {
        return showFileCount ? distribution.fileCount() : distribution.totalSizeBytes();
    }

    private String formatAbsoluteValue(TagDistribution distribution, boolean showFileCount) {
        return showFileCount
            ? Long.toString(distribution.fileCount())
            : FileMetaDataUtils.formatSize(distribution.totalSizeBytes());
    }

    private void applyTagColor(Node sliceNode, String tagColor) {
        if (sliceNode != null && tagColor != null) {
            sliceNode.setStyle("-fx-pie-color: " + tagColor + ";");
        }
    }
}
