/*
 * Copyright (c) 2021-2022, jad (cxxwl96@sina.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cxxwl96.jfx.admin.client.view.home;

import com.cxxwl96.jfx.admin.client.common.AbstractController;
import com.cxxwl96.jfx.admin.client.common.ApplicationStore;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.TileBuilder;
import eu.hansolo.tilesfx.TimeSection;
import eu.hansolo.tilesfx.TimeSectionBuilder;
import eu.hansolo.tilesfx.addons.Indicator;
import eu.hansolo.tilesfx.chart.ChartData;
import eu.hansolo.tilesfx.chart.RadarChart;
import eu.hansolo.tilesfx.chart.TilesFXSeries;
import eu.hansolo.tilesfx.skins.BarChartItem;
import eu.hansolo.tilesfx.skins.LeaderBoardItem;
import eu.hansolo.tilesfx.tools.Country;
import eu.hansolo.tilesfx.tools.CountryPath;
import eu.hansolo.tilesfx.tools.Location;
import eu.hansolo.tilesfx.weather.DarkSky;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.context.ActionHandler;
import io.datafx.controller.flow.context.FlowActionHandler;
import javafx.animation.AnimationTimer;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

/**
 * HomeController
 *
 * @author cxxwl96
 * @since 2022/9/23 21:33
 */
@SuppressWarnings( {"unchecked", "DuplicatedCode", "rawtypes", "FieldCanBeLocal"})
@ViewController("/views/home/Index.fxml")
public class IndexController extends AbstractController {
    @ActionHandler
    private FlowActionHandler actionHandler;

    @FXML
    private GridPane centerPane;

    public static final Color BACKGROUND_DARK = Color.rgb(39, 49, 66); // #2a2a2a

    public static final Color BACKGROUND_LIGHT = Color.rgb(255, 255, 255); // #2a2a2a

    public static final Color FOREGROUND_DARK = Color.rgb(223, 223, 223); // #2a2a2a

    public static final Color FOREGROUND_LIGHT = Color.rgb(52, 52, 52); // #2a2a2a

    public static final Color BORDERCOLOR_DARK = Color.rgb(49, 61, 79); // #2a2a2a

    public static final Color BORDERCOLOR_LIGHT = Color.rgb(185, 185, 185, 0.3f); // #2a2a2a

    private static final Random RND = new Random();

    private BarChartItem barChartItem1;

    private BarChartItem barChartItem2;

    private BarChartItem barChartItem3;

    private BarChartItem barChartItem4;

    private LeaderBoardItem leaderBoardItem1;

    private LeaderBoardItem leaderBoardItem2;

    private LeaderBoardItem leaderBoardItem3;

    private LeaderBoardItem leaderBoardItem4;

    private ChartData chartData1;

    private ChartData chartData2;

    private ChartData chartData3;

    private ChartData chartData4;

    private ChartData chartData5;

    private ChartData chartData6;

    private ChartData chartData7;

    private ChartData chartData8;

    private ChartData smoothChartData1;

    private ChartData smoothChartData2;

    private ChartData smoothChartData3;

    private ChartData smoothChartData4;

    private Tile radialChartTile;

    private Tile donutChartTile;

    private Tile circularProgressTile;

    private Tile radarChartTile1;

    private Tile worldTile;

    private Tile ephemerisTile;

    private Tile statusTile;

    private Tile sparkLineTile;

    private Tile areaChartTile;

    private Tile lineChartTile;

    private long lastTimerCall;

    private AnimationTimer timer;

    private DoubleProperty value;

    private final Map<String, List<CountryPath>> hiresCountryPaths = new ConcurrentHashMap<>();

    @PostConstruct
    private void init() {
        long start = System.currentTimeMillis();

        value = new SimpleDoubleProperty(0);

        // AreaChart Data
        XYChart.Series<String, Number> series1 = new XYChart.Series<>();
        series1.setName("Whatever");
        series1.getData().add(new XYChart.Data("MO", 23));
        series1.getData().add(new XYChart.Data("TU", 21));
        series1.getData().add(new XYChart.Data("WE", 20));
        series1.getData().add(new XYChart.Data("TH", 22));
        series1.getData().add(new XYChart.Data("FR", 24));
        series1.getData().add(new XYChart.Data("SA", 22));
        series1.getData().add(new XYChart.Data("SU", 20));

        // LineChart Data
        XYChart.Series<String, Number> series2 = new XYChart.Series<>();
        series2.setName("Inside");
        series2.getData().add(new XYChart.Data("MO", 8));
        series2.getData().add(new XYChart.Data("TU", 5));
        series2.getData().add(new XYChart.Data("WE", 0));
        series2.getData().add(new XYChart.Data("TH", 2));
        series2.getData().add(new XYChart.Data("FR", 4));
        series2.getData().add(new XYChart.Data("SA", 3));
        series2.getData().add(new XYChart.Data("SU", 5));

        XYChart.Series<String, Number> series3 = new XYChart.Series<>();
        series3.setName("Outside");
        series3.getData().add(new XYChart.Data("MO", 8));
        series3.getData().add(new XYChart.Data("TU", 5));
        series3.getData().add(new XYChart.Data("WE", 0));
        series3.getData().add(new XYChart.Data("TH", 2));
        series3.getData().add(new XYChart.Data("FR", 4));
        series3.getData().add(new XYChart.Data("SA", 3));
        series3.getData().add(new XYChart.Data("SU", 5));

        // WorldMap Data
        for (int i = 0; i < Country.values().length; i++) {
            double value = RND.nextInt(10);
            Color color;
            if (value > 8) {
                color = Tile.RED;
            } else if (value > 6) {
                color = Tile.ORANGE;
            } else if (value > 4) {
                color = Tile.YELLOW_ORANGE;
            } else if (value > 2) {
                color = Tile.GREEN;
            } else {
                color = Tile.BLUE;
            }
            Country.values()[i].setColor(color);
        }

        // TimeControl Data
        TimeSection timeSection = TimeSectionBuilder.create()
            .start(LocalTime.now().plusSeconds(20))
            .stop(LocalTime.now().plusHours(1))
            //.days(DayOfWeek.MONDAY, DayOfWeek.FRIDAY)
            .color(Tile.GRAY)
            .highlightColor(Tile.RED)
            .build();

        timeSection.setOnTimeSectionEntered(e -> System.out.println("Section ACTIVE"));
        timeSection.setOnTimeSectionLeft(e -> System.out.println("Section INACTIVE"));

        // Weather (You can get a DarkSky API key here: https://darksky.net/dev/ )
        DarkSky darkSky = new DarkSky("YOUR DARKSKY API KEY", DarkSky.Unit.CA, DarkSky.Language.ENGLISH, 51.911858,
            7.632815);
        //darkSky.update();

        // BarChart Items
        barChartItem1 = new BarChartItem("Gerrit", 47, Tile.BLUE);
        barChartItem2 = new BarChartItem("Sandra", 43, Tile.RED);
        barChartItem3 = new BarChartItem("Lilli", 12, Tile.GREEN);
        barChartItem4 = new BarChartItem("Anton", 8, Tile.ORANGE);

        barChartItem1.setFormatString("%.1f kWh");

        // LeaderBoard Items
        leaderBoardItem1 = new LeaderBoardItem("Gerrit", 47);
        leaderBoardItem2 = new LeaderBoardItem("Sandra", 43);
        leaderBoardItem3 = new LeaderBoardItem("Lilli", 12);
        leaderBoardItem4 = new LeaderBoardItem("Anton", 8);

        // Chart Data
        chartData1 = new ChartData("Item 1", 24.0, Tile.GREEN);
        chartData2 = new ChartData("Item 2", 10.0, Tile.BLUE);
        chartData3 = new ChartData("Item 3", 12.0, Tile.RED);
        chartData4 = new ChartData("Item 4", 13.0, Tile.YELLOW_ORANGE);
        chartData5 = new ChartData("Item 5", 13.0, Tile.BLUE);
        chartData6 = new ChartData("Item 6", 13.0, Tile.BLUE);
        chartData7 = new ChartData("Item 7", 13.0, Tile.BLUE);
        chartData8 = new ChartData("Item 8", 13.0, Tile.BLUE);
        chartData1.setTextColor(FOREGROUND_LIGHT);
        chartData2.setTextColor(FOREGROUND_LIGHT);
        chartData3.setTextColor(FOREGROUND_LIGHT);
        chartData4.setTextColor(FOREGROUND_LIGHT);
        chartData5.setTextColor(FOREGROUND_LIGHT);
        chartData6.setTextColor(FOREGROUND_LIGHT);
        chartData7.setTextColor(FOREGROUND_LIGHT);
        chartData8.setTextColor(FOREGROUND_LIGHT);
        //ChartData.animated = false;

        smoothChartData1 = new ChartData("Item 1", RND.nextDouble() * 25, Tile.BLUE);
        smoothChartData2 = new ChartData("Item 2", RND.nextDouble() * 25, Tile.BLUE);
        smoothChartData3 = new ChartData("Item 3", RND.nextDouble() * 25, Tile.BLUE);
        smoothChartData4 = new ChartData("Item 4", RND.nextDouble() * 25, Tile.BLUE);

        sparkLineTile = TileBuilder.create()
            .skinType(Tile.SkinType.SPARK_LINE)
            .foregroundBaseColor(FOREGROUND_LIGHT)
            .title("SparkLine Tile")
            .unit("mb")
            .borderRadius(14)
            .gradientStops(new Stop(0, Tile.GREEN), new Stop(0.5, Tile.YELLOW), new Stop(1.0, Tile.RED))
            .strokeWithGradient(true)
            .backgroundColor(BACKGROUND_LIGHT)
            .borderColor(BORDERCOLOR_LIGHT)
            .borderWidth(0.8d)
            //.smoothing(true)
            .build();

        areaChartTile = TileBuilder.create()
            .skinType(Tile.SkinType.SMOOTHED_CHART)
            .foregroundBaseColor(FOREGROUND_LIGHT)
            .backgroundColor(BACKGROUND_LIGHT)
            .borderRadius(14)
            .borderColor(BORDERCOLOR_LIGHT)
            .borderWidth(0.8d)
            .title("SmoothedChart Tile")
            .chartType(Tile.ChartType.AREA)
            //.animated(true)
            .smoothing(true)
            .tooltipTimeout(1000)
            .tilesFxSeries(new TilesFXSeries<>(series1, Tile.BLUE,
                new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, new Stop(0, Tile.BLUE),
                    new Stop(1, Color.TRANSPARENT))))
            .build();

        lineChartTile = TileBuilder.create()
            .skinType(Tile.SkinType.SMOOTHED_CHART)
            .foregroundBaseColor(FOREGROUND_LIGHT)
            .backgroundColor(BACKGROUND_LIGHT)
            .borderRadius(14)
            .borderColor(BORDERCOLOR_LIGHT)
            .borderWidth(0.8d)
            .title("SmoothedChart Tile")
            .animated(true)
            .smoothing(false)
            .series(series2, series3)
            .build();

        worldTile = TileBuilder.create()
            .skinType(Tile.SkinType.WORLDMAP)
            .foregroundBaseColor(FOREGROUND_LIGHT)
            .backgroundColor(BACKGROUND_LIGHT)
            .borderRadius(14)
            .borderColor(BORDERCOLOR_LIGHT)
            .borderWidth(0.8d)
            .title("WorldMap Tile")
            .text("Whatever text")
            .textVisible(false)
            .shadowsEnabled(true)
            .build();

        radialChartTile = TileBuilder.create()
            .skinType(Tile.SkinType.RADIAL_CHART)
            .foregroundBaseColor(FOREGROUND_LIGHT)
            .backgroundColor(BACKGROUND_LIGHT)
            .borderRadius(14)
            .borderColor(BORDERCOLOR_LIGHT)
            .borderWidth(0.8d)
            .title("RadialChart")
            .text("Some text")
            .textVisible(false)
            .chartData(chartData1, chartData2, chartData3, chartData4)
            .shadowsEnabled(true)
            .build();

        donutChartTile = TileBuilder.create()
            .skinType(Tile.SkinType.DONUT_CHART)
            .title("DonutChart")
            .foregroundBaseColor(FOREGROUND_LIGHT)
            .backgroundColor(BACKGROUND_LIGHT)
            .borderRadius(14)
            .borderColor(BORDERCOLOR_LIGHT)
            .borderWidth(0.8d)
            .text("Some text")
            .textVisible(false)
            .chartData(chartData1, chartData2, chartData3, chartData4)
            .build();

        circularProgressTile = TileBuilder.create()
            .skinType(Tile.SkinType.CIRCULAR_PROGRESS)
            .title("CircularProgress")
            .foregroundBaseColor(FOREGROUND_LIGHT)
            .backgroundColor(BACKGROUND_LIGHT)
            .borderRadius(14)
            .borderColor(BORDERCOLOR_LIGHT)
            .borderWidth(0.8d)
            .text("Some text")
            .unit("\u0025")
            //.graphic(new WeatherSymbol(ConditionAndIcon.CLEAR_DAY, 48, Color.WHITE))
            .build();

        radarChartTile1 = TileBuilder.create()
            .skinType(Tile.SkinType.RADAR_CHART)
            .minValue(0)
            .maxValue(50)
            .foregroundBaseColor(FOREGROUND_LIGHT)
            .backgroundColor(BACKGROUND_LIGHT)
            .borderRadius(14)
            .borderColor(BORDERCOLOR_LIGHT)
            .borderWidth(0.8d)
            .title("RadarChart Sector")
            .unit("Unit")
            .radarChartMode(RadarChart.Mode.SECTOR)
            .gradientStops(new Stop(0.00000, Color.TRANSPARENT), new Stop(0.00001, Color.web("#3552a0")),
                new Stop(0.09090, Color.web("#456acf")), new Stop(0.27272, Color.web("#45a1cf")),
                new Stop(0.36363, Color.web("#30c8c9")), new Stop(0.45454, Color.web("#30c9af")),
                new Stop(0.50909, Color.web("#56d483")), new Stop(0.72727, Color.web("#9adb49")),
                new Stop(0.81818, Color.web("#efd750")), new Stop(0.90909, Color.web("#ef9850")),
                new Stop(1.00000, Color.web("#ef6050")))
            .text("Test")
            .chartData(chartData1, chartData2, chartData3, chartData4, chartData5, chartData6, chartData7, chartData8)
            .tooltipText("")
            .animated(true)
            .build();

        ephemerisTile = TileBuilder.create()
            .skinType(Tile.SkinType.EPHEMERIS)
            .title("Ephemeris")
            .foregroundBaseColor(FOREGROUND_LIGHT)
            .backgroundColor(BACKGROUND_LIGHT)
            .borderRadius(14)
            .borderColor(BORDERCOLOR_LIGHT)
            .borderWidth(0.8d)
            .currentLocation(new Location(51.911515, 7.6340026, "Hiltrup"))
            .text("Hiltrup")
            .build();

        Indicator leftGraphics = new Indicator(Tile.RED);
        leftGraphics.setOn(true);

        Indicator middleGraphics = new Indicator(Tile.YELLOW);
        middleGraphics.setOn(true);

        Indicator rightGraphics = new Indicator(Tile.GREEN);
        rightGraphics.setOn(true);

        statusTile = TileBuilder.create()
            .skinType(Tile.SkinType.STATUS)
            .title("Status Tile")
            .foregroundBaseColor(FOREGROUND_LIGHT)
            .borderWidth(0.8d)
            .backgroundColor(BACKGROUND_LIGHT)
            .borderRadius(14)
            .borderColor(BORDERCOLOR_LIGHT)
            .borderWidth(0.8d)
            .description("Notifications")
            .leftText("CRITICAL")
            .middleText("WARNING")
            .rightText("INFORMATION")
            .leftGraphics(leftGraphics)
            .middleGraphics(middleGraphics)
            .rightGraphics(rightGraphics)
            .text("Text")
            .build();

        lastTimerCall = System.nanoTime();
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (now > lastTimerCall + 3_500_000_000L) {

                    sparkLineTile.setValue(
                        RND.nextDouble() * sparkLineTile.getRange() * 1.5 + sparkLineTile.getMinValue());
                    //value.set(RND.nextDouble() * sparkLineTile.getRange() * 1.5 + sparkLineTile.getMinValue());
                    //sparkLineTile.setValue(20);

                    for (XYChart.Data<String, Number> stringNumberData : series1.getData()) {
                        stringNumberData.setYValue(RND.nextInt(100));
                    }
                    for (XYChart.Data<String, Number> stringNumberData : series2.getData()) {
                        stringNumberData.setYValue(RND.nextInt(30));
                    }
                    for (XYChart.Data<String, Number> data : series3.getData()) {
                        data.setYValue(RND.nextInt(10));
                    }

                    chartData1.setValue(RND.nextDouble() * 50);
                    chartData2.setValue(RND.nextDouble() * 50);
                    chartData3.setValue(RND.nextDouble() * 50);
                    chartData4.setValue(RND.nextDouble() * 50);
                    chartData5.setValue(RND.nextDouble() * 50);
                    chartData6.setValue(RND.nextDouble() * 50);
                    chartData7.setValue(RND.nextDouble() * 50);
                    chartData8.setValue(RND.nextDouble() * 50);

                    circularProgressTile.setValue(RND.nextDouble() * 120);

                    smoothChartData1.setValue(smoothChartData2.getValue());
                    smoothChartData2.setValue(smoothChartData3.getValue());
                    smoothChartData3.setValue(smoothChartData4.getValue());
                    smoothChartData4.setValue(RND.nextDouble() * 25);

                    if (statusTile.getLeftValue() > 1000) {
                        statusTile.setLeftValue(0);
                    }
                    if (statusTile.getMiddleValue() > 1000) {
                        statusTile.setMiddleValue(0);
                    }
                    if (statusTile.getRightValue() > 1000) {
                        statusTile.setRightValue(0);
                    }
                    statusTile.setLeftValue(statusTile.getLeftValue() + RND.nextInt(4));
                    statusTile.setMiddleValue(statusTile.getMiddleValue() + RND.nextInt(3));
                    statusTile.setRightValue(statusTile.getRightValue() + RND.nextInt(3));

                    lastTimerCall = now;
                }
            }
        };

        centerPane.add(radialChartTile, 0, 0);
        centerPane.add(donutChartTile, 1, 0);
        centerPane.add(circularProgressTile, 2, 0);
        centerPane.add(radarChartTile1, 3, 0);
        centerPane.add(worldTile, 0, 1, 2, 1);
        centerPane.add(ephemerisTile, 2, 1);
        centerPane.add(statusTile, 3, 1);
        centerPane.add(sparkLineTile, 0, 2);
        centerPane.add(areaChartTile, 1, 2);
        centerPane.add(lineChartTile, 2, 2, 2, 1);
        ApplicationStore.darkThemeProperty().addListener((observable, oldValue, newValue) -> changeTheme(newValue));
        changeTheme(ApplicationStore.getDarkTheme());
        timer.start();

    }

    private void changeTheme(boolean isDarkTheme) {
        centerPane.getChildren().forEach(node -> {
            if (node instanceof Tile) {
                if (isDarkTheme) {
                    ((Tile) node).setBackgroundColor(BACKGROUND_DARK);
                    ((Tile) node).setBorderColor(BORDERCOLOR_DARK);
                    ((Tile) node).setForegroundBaseColor(FOREGROUND_DARK);
                    ((Tile) node).setShadowsEnabled(true);
                    ((Tile) node).getChartData().forEach(chartData -> {
                        chartData.setTextColor(FOREGROUND_DARK);
                    });
                } else {
                    ((Tile) node).setBackgroundColor(BACKGROUND_LIGHT);
                    ((Tile) node).setBorderColor(BORDERCOLOR_LIGHT);
                    ((Tile) node).setForegroundBaseColor(FOREGROUND_LIGHT);
                    ((Tile) node).setShadowsEnabled(true);
                    ((Tile) node).getChartData().forEach(chartData -> {
                        chartData.setTextColor(FOREGROUND_LIGHT);
                    });
                }
            }
        });
    }

    @PreDestroy
    private void destroy() {
        timer.stop();
    }

}
