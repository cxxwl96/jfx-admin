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

package com.cxxwl96.jfx.admin.client.component;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.svg.SVGGlyph;

import java.util.ArrayList;
import java.util.List;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.BoundingBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

/**
 * JFX的JFXDecorator源码，增加setButton方法设置 全屏按钮、最小化按钮、最大化按钮 是否显示
 *
 * @author cxxwl96
 * @since 2022/9/22 23:44
 */
public class JFXDecorator extends VBox {

    private Stage primaryStage;

    private double xOffset = 0;

    private double yOffset = 0;

    private double initX;

    private double initY;

    private double initWidth = -1;

    private double initHeight = -1;

    private double initStageX = -1;

    private double initStageY = -1;

    private boolean allowMove = false;

    private boolean isDragging = false;

    private Timeline windowDecoratorAnimation;

    private final StackPane contentPlaceHolder = new StackPane();

    private HBox buttonsContainer;

    private final ObjectProperty<Runnable> onCloseButtonAction = new SimpleObjectProperty<>(
        () -> primaryStage.fireEvent(new WindowEvent(primaryStage, WindowEvent.WINDOW_CLOSE_REQUEST)));

    private final BooleanProperty customMaximize = new SimpleBooleanProperty(false);

    private boolean maximized = false;

    private BoundingBox originalBox;

    private BoundingBox maximizedBox;

    protected JFXButton btnMax;

    protected JFXButton btnFull;

    protected JFXButton btnClose;

    protected JFXButton btnMin;

    protected StringProperty title = new SimpleStringProperty();

    protected Text text;

    protected Node graphic;

    protected HBox graphicContainer;

    private List<JFXButton> btns;

    /**
     * Create a window decorator for the specified node with the options:
     * - full screen
     * - maximize
     * - minimize
     *
     * @param stage the primary stage used by the application
     * @param node the node to be decorated
     */
    public JFXDecorator(Stage stage, Node node) {
        this(stage, node, true, true, true);
    }

    /**
     * Create a window decorator for the specified node with the options:
     * - full screen
     * - maximize
     * - minimize
     *
     * @param stage the primary stage used by the application
     * @param node the node to be decorated
     * @param fullScreen indicates whether to show full screen option or not
     * @param max indicates whether to show maximize option or not
     * @param min indicates whether to show minimize option or not
     */
    public JFXDecorator(Stage stage, Node node, boolean fullScreen, boolean max, boolean min) {
        primaryStage = stage;
        // Note that setting the style to TRANSPARENT is causing performance
        // degradation, as an alternative we set it to UNDECORATED instead.
        primaryStage.initStyle(StageStyle.UNDECORATED);

        setPickOnBounds(false);
        getStyleClass().add("jfx-decorator");

        initializeButtons();
        initializeContainers(node, fullScreen, max, min);

        primaryStage.fullScreenProperty().addListener((o, oldVal, newVal) -> {
            if (newVal) {
                // remove border
                contentPlaceHolder.getStyleClass().remove("resize-border");
                /*
                 *  note the border property MUST NOT be bound to another property
                 *  when going full screen mode, thus the binding will be lost if exisited
                 */
                contentPlaceHolder.borderProperty().unbind();
                contentPlaceHolder.setBorder(Border.EMPTY);
                if (windowDecoratorAnimation != null) {
                    windowDecoratorAnimation.stop();
                }
                windowDecoratorAnimation = new Timeline(new KeyFrame(Duration.millis(320),
                    new KeyValue(this.translateYProperty(), -buttonsContainer.getHeight(), Interpolator.EASE_BOTH)));
                windowDecoratorAnimation.setOnFinished((finish) -> {
                    this.getChildren().remove(buttonsContainer);
                    this.setTranslateY(0);
                });
                windowDecoratorAnimation.play();
            } else {
                // add border
                if (windowDecoratorAnimation != null) {
                    if (windowDecoratorAnimation.getStatus() == Animation.Status.RUNNING) {
                        windowDecoratorAnimation.stop();
                    } else {
                        this.getChildren().add(0, buttonsContainer);
                    }
                }
                this.setTranslateY(-buttonsContainer.getHeight());
                windowDecoratorAnimation = new Timeline(new KeyFrame(Duration.millis(320),
                    new KeyValue(this.translateYProperty(), 0, Interpolator.EASE_BOTH)));
                windowDecoratorAnimation.setOnFinished((finish) -> {
                    contentPlaceHolder.setBorder(new Border(
                        new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY,
                            new BorderWidths(0, 4, 4, 4))));
                    contentPlaceHolder.getStyleClass().add("resize-border");
                });
                windowDecoratorAnimation.play();
            }
        });

        contentPlaceHolder.addEventHandler(MouseEvent.MOUSE_PRESSED, this::updateInitMouseValues);
        buttonsContainer.addEventHandler(MouseEvent.MOUSE_PRESSED, this::updateInitMouseValues);

        // show the drag cursor on the borders
        addEventFilter(MouseEvent.MOUSE_MOVED, this::showDragCursorOnBorders);

        // handle drag events on the decorator pane
        addEventFilter(MouseEvent.MOUSE_RELEASED, (mouseEvent) -> isDragging = false);
        this.setOnMouseDragged(this::handleDragEventOnDecoratorPane);
    }

    private void initializeButtons() {

        SVGGlyph full = new SVGGlyph(0, "FULLSCREEN",
            "M625.777778 256h142.222222V398.222222h113.777778V142.222222H625.777778v113.777778zM256 398.222222V256H398.222222v-113.777778H142.222222V398.222222h113.777778zM768 625.777778v142.222222H625.777778v113.777778h256V625.777778h-113.777778zM398.222222 768H256V625.777778h-113.777778v256H398.222222v-113.777778z",
            Color.WHITE);
        full.setSize(16, 16);
        SVGGlyph minus = new SVGGlyph(0, "MINUS",
            "M804.571 420.571v109.714q0 22.857-16 38.857t-38.857 16h-694.857q-22.857 0-38.857-16t-16-38.857v-109.714q0-22.857 16-38.857t38.857-16h694.857q22.857 0 38.857 16t16 38.857z",
            Color.WHITE);
        minus.setSize(12, 2);
        minus.setTranslateY(4);
        SVGGlyph resizeMax = new SVGGlyph(0, "RESIZE_MAX",
            "M629.556904 391.972323c17.328667 17.319457 47.028083 17.319457 66.814732 0l168.302147-165.81449 0 133.63765c0 19.805068 14.85022 34.646078 34.636868 34.646078l24.743544 0c19.806091 0 34.656311-12.371772 34.656311-29.691229L958.710506 119.732965l-2.478448 0 2.478448-17.318434c0-9.903557-2.478448-17.328667-7.435343-24.75173-4.936429-4.947685-14.848173-9.894348-24.743544-9.894348l-17.326621 0 0 0L664.211169 65.290005c-19.805068 0-34.654264 17.328667-34.654264 34.646078l0 24.75173c2.478448 22.274306 19.788695 34.646078 39.59274 34.646078l128.690988 0L632.035352 325.149405C609.751836 342.467839 609.751836 372.175441 629.556904 391.972323L629.556904 391.972323 629.556904 391.972323zM394.441049 629.557416c-17.310248-17.327644-47.00864-17.327644-66.814732 0l-168.301124 165.806303L159.325194 664.195308c0-19.787672-14.8318-34.637892-34.636868-34.637892l-24.76094 0c-19.787672 0-34.637892 12.371772-34.637892 29.698392l0 242.532779 2.477424 0-2.477424 17.327644c0 9.893324 2.477424 17.310248 7.4159 24.743544 4.955872 4.955872 14.867616 9.894348 24.76094 9.894348l17.327644 0 0 0 244.992807 2.477424c19.823488 0 34.655288-17.327644 34.655288-34.637892l0-24.759917c-2.478448-22.26612-19.787672-34.637892-39.59274-34.637892l-128.690988 0 168.283728-165.8237C411.768693 679.044504 411.768693 649.345088 394.441049 629.557416L394.441049 629.557416 394.441049 629.557416zM956.231035 904.266012 956.231035 661.71686c0-19.787672-17.328667-29.680996-34.638915-29.680996l-24.758893 0c-19.788695 0-34.638915 17.309224-34.638915 34.637892l0 131.168412L693.893188 632.035864c-17.308201-17.328667-47.00864-17.328667-66.814732 0-17.326621 17.309224-17.326621 47.00864 0 66.813708l168.283728 165.806303-128.690988 0c-19.786649 0-37.115316 12.388145-39.59274 34.654264l0 24.744567c0 19.805068 17.328667 34.654264 34.638915 34.654264l240.071727-2.478448 0 0 17.328667 0c9.893324 0 17.310248-2.477424 24.743544-9.893324 4.954849-4.955872 7.414877-14.85022 7.414877-24.744567L956.231035 904.266012C953.752588 904.266012 956.231035 904.266012 956.231035 904.266012L956.231035 904.266012 956.231035 904.266012zM228.635769 159.334915l128.690988 0c19.805068 0 37.115316-12.372795 39.59274-34.646078L396.919497 99.936083c0-19.796882-17.309224-34.646078-34.654264-34.646078l-244.992807 2.478448 0 0-17.34504 0c-9.875928 0-17.310248 2.477424-24.743544 9.894348-4.938476 4.955872-7.4159 14.849196-7.4159 24.75173l2.477424 17.318434-2.477424 0 0 245.01839c0 19.796882 14.85022 29.691229 34.637892 29.691229l24.743544 0c19.822464 0 34.654264-14.84101 34.654264-34.646078L161.803641 226.157833l168.283728 165.81449c17.34504 17.319457 47.044456 17.319457 66.832128 0 17.328667-17.327644 17.328667-47.026036 0-66.822918L228.635769 159.334915 228.635769 159.334915 228.635769 159.334915zM228.635769 159.334915",
            Color.WHITE);
        resizeMax.setSize(12, 12);
        SVGGlyph resizeMin = new SVGGlyph(0, "RESIZE_MIN",
            "M923.378813 305.414945l-141.483338-0.514723L940.653245 147.093103c17.34504-17.545608 17.34504-45.962821 0-63.456241-17.450441-17.493419-45.684482-17.493419-63.045895 0L717.313597 243.092527 718.395232 98.782817c0.418532-18.11252-13.872962-33.870412-31.898501-33.44267l-22.806425 0c-18.008142 0.470721-32.927947 11.236925-33.29429 29.350468l-0.942465 250.378467c0 0.322341 1.169639 0.558725 1.169639 0.889253l-1.744737 16.420995c-0.140193 9.029654 1.989307 17.161868 7.818059 22.946618 5.740748 5.879917 13.837146 9.405207 22.806425 9.179057l16.280802-0.331551c0.366344 0 0.610914-0.140193 0.942465-0.192382l250.700809 1.413186c18.025539-0.418532 32.875758-15.381315 33.346479-33.529651l0-23.006993C958.050474 317.507354 941.35114 304.996412 923.378813 305.414945L923.378813 305.414945zM959.07992 687.11225l-0.052189-22.998807c-0.418532-18.148335-15.233959-33.102932-33.154098-33.538861l-249.393024 1.360997c-0.36532 0-0.610914-0.140193-0.942465-0.140193l-16.210193-0.36532c-8.951883-0.279363-16.996093 3.298116-22.720468 9.125845-5.792936 5.827729-7.90504 13.960966-7.730055 23.034623l1.744737 16.419971c0 0.331551-1.169639 0.576121-1.169639 0.890276l0.942465 248.449535c0.366344 18.11252 15.181771 28.879748 33.154098 29.315676l24.185842 0.034792c17.921161 0.435928 32.125675-15.373129 31.688723-33.486672l-1.029446-141.396357 159.64907 158.479431c17.257036 17.48421 45.3867 17.48421 62.696947 0 17.310248-17.502629 17.310248-45.910633 0-63.448054L781.284561 719.621665l140.540873-0.523933C939.711803 719.568453 956.357925 708.470697 959.07992 687.11225L959.07992 687.11225zM385.61094 640.590704c-5.740748-5.827729-13.837146-9.405207-22.807449-9.108448l-16.33299 0.366344c-0.278339 0-0.557702 0.140193-0.87288 0.140193l-247.367901-1.414209c-17.97335 0.436952-32.893154 15.390525-33.311687 33.538861l-0.052189 22.998807c2.721994 21.358447 19.438725 32.422434 37.412075 32.089859l139.494031 0.505513L81.722768 878.884948c-17.415648 17.554818-17.415648 45.962821 0 63.448054 17.379832 17.502629 45.560662 17.502629 62.958914 0l160.311149-159.403476-1.082658 140.540873c-0.435928 18.11252 13.872962 33.869389 31.846312 33.450856l22.859637 0c18.008142-0.471744 32.910551-11.203156 33.29429-29.351492l0.942465-246.670006c0-0.315178-1.169639-0.558725-1.169639-0.890276l1.780553-16.419971C393.549749 654.568043 391.456065 646.436852 385.61094 640.590704L385.61094 640.590704zM392.817062 345.070106l-0.941442-250.378467c-0.348947-18.113543-15.199167-28.92375-33.120328-29.394471l-24.184819 0c-17.903765-0.38374-32.107255 15.373129-31.688723 33.44267l1.081635 143.315056L144.367527 83.636862c-17.362436-17.493419-45.438888-17.493419-62.749136 0-17.310248 17.493419-17.310248 45.909609 0 63.456241l158.042479 157.807119-139.232065 0.514723c-17.86795-0.418532-34.480302 12.092409-37.202297 33.443693l0.034792 23.006993c0.435928 18.147312 15.251356 33.110095 33.172517 33.529651l249.374604-1.413186c0.348947 0.052189 0.576121 0.192382 0.907673 0.192382l16.228613 0.37453c8.933463 0.23536 17.030885-3.288906 22.719444-9.169847 5.792936-5.836939 7.887644-13.96813 7.712659-23.050995l-1.779529-16.410762C391.596258 345.584829 392.817062 345.392448 392.817062 345.070106L392.817062 345.070106zM392.817062 345.070106",
            Color.WHITE);
        resizeMin.setSize(12, 12);
        SVGGlyph close = new SVGGlyph(0, "CLOSE",
            "M810 274l-238 238 238 238-60 60-238-238-238 238-60-60 238-238-238-238 60-60 238 238 238-238z",
            Color.WHITE);
        close.setSize(12, 12);
        btnFull = new JFXButton();
        btnFull.getStyleClass().add("jfx-decorator-button");
        btnFull.setCursor(Cursor.HAND);
        btnFull.setOnAction((action) -> primaryStage.setFullScreen(!primaryStage.isFullScreen()));
        btnFull.setGraphic(full);
        btnFull.setTranslateX(-30);
        btnFull.setRipplerFill(Color.WHITE);

        btnClose = new JFXButton();
        btnClose.getStyleClass().add("jfx-decorator-button");
        btnClose.setCursor(Cursor.HAND);
        btnClose.setOnAction((action) -> onCloseButtonAction.get().run());
        btnClose.setGraphic(close);
        btnClose.setRipplerFill(Color.WHITE);

        btnMin = new JFXButton();
        btnMin.getStyleClass().add("jfx-decorator-button");
        btnMin.setCursor(Cursor.HAND);
        btnMin.setOnAction((action) -> primaryStage.setIconified(true));
        btnMin.setGraphic(minus);
        btnMin.setRipplerFill(Color.WHITE);

        btnMax = new JFXButton();
        btnMax.getStyleClass().add("jfx-decorator-button");
        btnMax.setCursor(Cursor.HAND);
        btnMax.setRipplerFill(Color.WHITE);
        btnMax.setOnAction((action) -> maximize(resizeMin, resizeMax));
        btnMax.setGraphic(resizeMax);
    }

    private void maximize(SVGGlyph resizeMin, SVGGlyph resizeMax) {
        if (!isCustomMaximize()) {
            primaryStage.setMaximized(!primaryStage.isMaximized());
            maximized = primaryStage.isMaximized();
            if (primaryStage.isMaximized()) {
                btnMax.setGraphic(resizeMin);
                btnMax.setTooltip(new Tooltip("Restore Down"));
            } else {
                btnMax.setGraphic(resizeMax);
                btnMax.setTooltip(new Tooltip("Maximize"));
            }
        } else {
            if (!maximized) {
                // store original bounds
                originalBox = new BoundingBox(primaryStage.getX(), primaryStage.getY(), primaryStage.getWidth(),
                    primaryStage.getHeight());
                // get the max stage bounds
                Screen screen = Screen.getScreensForRectangle(primaryStage.getX(), primaryStage.getY(),
                    primaryStage.getWidth(), primaryStage.getHeight()).get(0);
                Rectangle2D bounds = screen.getVisualBounds();
                maximizedBox = new BoundingBox(bounds.getMinX(), bounds.getMinY(), bounds.getWidth(),
                    bounds.getHeight());
                // maximized the stage
                primaryStage.setX(maximizedBox.getMinX());
                primaryStage.setY(maximizedBox.getMinY());
                primaryStage.setWidth(maximizedBox.getWidth());
                primaryStage.setHeight(maximizedBox.getHeight());
                btnMax.setGraphic(resizeMin);
                btnMax.setTooltip(new Tooltip("Restore Down"));
            } else {
                // restore stage to its original size
                primaryStage.setX(originalBox.getMinX());
                primaryStage.setY(originalBox.getMinY());
                primaryStage.setWidth(originalBox.getWidth());
                primaryStage.setHeight(originalBox.getHeight());
                originalBox = null;
                btnMax.setGraphic(resizeMax);
                btnMax.setTooltip(new Tooltip("Maximize"));
            }
            maximized = !maximized;
        }
    }

    private void initializeContainers(Node node, boolean fullScreen, boolean max, boolean min) {
        buttonsContainer = new HBox();
        buttonsContainer.getStyleClass().add("jfx-decorator-buttons-container");
        buttonsContainer.setBackground(
            new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
        // BINDING
        buttonsContainer.setPadding(new Insets(4));
        buttonsContainer.setAlignment(Pos.CENTER_RIGHT);
        // customize decorator buttons
        btns = new ArrayList<>();
        if (fullScreen) {
            btns.add(btnFull);
        }
        if (min) {
            btns.add(btnMin);
        }
        if (max) {
            btns.add(btnMax);
            // maximize/restore the window on header double click
            buttonsContainer.addEventHandler(MouseEvent.MOUSE_CLICKED, (mouseEvent) -> {
                if (mouseEvent.getClickCount() == 2) {
                    btnMax.fire();
                }
            });
        }
        btns.add(btnClose);

        text = new Text();
        text.getStyleClass().addAll("jfx-decorator-text", "title", "jfx-decorator-title");
        text.setFill(Color.WHITE);
        text.textProperty().bind(title); //binds the Text's text to title
        title.bind(primaryStage.titleProperty()); //binds title to the primaryStage's title

        graphicContainer = new HBox();
        graphicContainer.setPickOnBounds(false);
        graphicContainer.setAlignment(Pos.CENTER_LEFT);
        graphicContainer.getChildren().setAll(text);

        HBox graphicTextContainer = new HBox(graphicContainer, text);
        graphicTextContainer.getStyleClass().add("jfx-decorator-title-container");
        graphicTextContainer.setAlignment(Pos.CENTER_LEFT);
        graphicTextContainer.setPickOnBounds(false);
        HBox.setHgrow(graphicTextContainer, Priority.ALWAYS);
        HBox.setMargin(graphicContainer, new Insets(0, 8, 0, 8));

        buttonsContainer.getChildren().setAll(graphicTextContainer);
        buttonsContainer.getChildren().addAll(btns);
        // 源码此处在mac系统上不兼容 更改为MouseEvent.ANY // buttonsContainer.addEventHandler(MouseEvent.MOUSE_ENTERED, (enter) -> allowMove = true);
        // 源码此处在mac系统上不兼容 更改为MouseEvent.ANY // buttonsContainer.addEventHandler(MouseEvent.MOUSE_EXITED, (enter) -> {
        // 源码此处在mac系统上不兼容 更改为MouseEvent.ANY //     if (!isDragging) {
        // 源码此处在mac系统上不兼容 更改为MouseEvent.ANY //         allowMove = false;
        // 源码此处在mac系统上不兼容 更改为MouseEvent.ANY //     }
        // 源码此处在mac系统上不兼容 更改为MouseEvent.ANY // });
        buttonsContainer.addEventHandler(MouseEvent.ANY, (event) -> {
            event.consume();
            if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            } else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                primaryStage.setX(event.getScreenX() - xOffset);
                if (event.getScreenY() - yOffset < 0) {
                    primaryStage.setY(0);
                } else {
                    primaryStage.setY(event.getScreenY() - yOffset);
                }
            }
        });
        buttonsContainer.setMinWidth(180);
        contentPlaceHolder.getStyleClass().add("jfx-decorator-content-container");
        contentPlaceHolder.setMinSize(0, 0);
        StackPane clippedContainer = new StackPane(node);
        contentPlaceHolder.getChildren().add(clippedContainer);
        ((Region) node).setMinSize(0, 0);
        VBox.setVgrow(contentPlaceHolder, Priority.ALWAYS);
        contentPlaceHolder.getStyleClass().add("resize-border");
        contentPlaceHolder.setBorder(new Border(
            new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 4, 4, 4))));
        // BINDING
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(clippedContainer.widthProperty());
        clip.heightProperty().bind(clippedContainer.heightProperty());
        clippedContainer.setClip(clip);
        this.getChildren().addAll(buttonsContainer, contentPlaceHolder);
    }

    /**
     * 设置按钮
     *
     * @param fullScreen
     * @param max
     * @param min
     */
    public void setButton(boolean fullScreen, boolean max, boolean min) {
        btns.clear();
        if (fullScreen) {
            btns.add(btnFull);
        }
        if (min) {
            btns.add(btnMin);
        }
        if (max) {
            btns.add(btnMax);
            // maximize/restore the window on header double click
            buttonsContainer.addEventHandler(MouseEvent.MOUSE_CLICKED, (mouseEvent) -> {
                if (mouseEvent.getClickCount() == 2) {
                    btnMax.fire();
                }
            });
        }
        btns.add(btnClose);
        buttonsContainer.getChildren().remove(btnFull);
        buttonsContainer.getChildren().remove(btnMin);
        buttonsContainer.getChildren().remove(btnMax);
        buttonsContainer.getChildren().remove(btnClose);
        buttonsContainer.getChildren().addAll(btns);
    }

    private void showDragCursorOnBorders(MouseEvent mouseEvent) {
        if (primaryStage.isMaximized() || primaryStage.isFullScreen() || maximized) {
            this.setCursor(Cursor.DEFAULT);
            return; // maximized mode does not support resize
        }
        if (!primaryStage.isResizable()) {
            return;
        }
        double x = mouseEvent.getX();
        double y = mouseEvent.getY();
        if (contentPlaceHolder.getBorder() != null && contentPlaceHolder.getBorder().getStrokes().size() > 0) {
            double borderWidth = contentPlaceHolder.snappedLeftInset();
            if (isRightEdge(x)) {
                if (y < borderWidth) {
                    this.setCursor(Cursor.NE_RESIZE);
                } else if (y > this.getHeight() - borderWidth) {
                    this.setCursor(Cursor.SE_RESIZE);
                } else {
                    this.setCursor(Cursor.E_RESIZE);
                }
            } else if (isLeftEdge(x)) {
                if (y < borderWidth) {
                    this.setCursor(Cursor.NW_RESIZE);
                } else if (y > this.getHeight() - borderWidth) {
                    this.setCursor(Cursor.SW_RESIZE);
                } else {
                    this.setCursor(Cursor.W_RESIZE);
                }
            } else if (isTopEdge(y)) {
                this.setCursor(Cursor.N_RESIZE);
            } else if (isBottomEdge(y)) {
                this.setCursor(Cursor.S_RESIZE);
            } else {
                this.setCursor(Cursor.DEFAULT);
            }
        }
    }

    private void handleDragEventOnDecoratorPane(MouseEvent mouseEvent) {
        isDragging = true;
        if (!mouseEvent.isPrimaryButtonDown() || (xOffset == -1 && yOffset == -1)) {
            return;
        }
        /*
         * Long press generates drag event!
         */
        if (primaryStage.isFullScreen() || mouseEvent.isStillSincePress() || primaryStage.isMaximized() || maximized) {
            return;
        }

        double newX = mouseEvent.getScreenX();
        double newY = mouseEvent.getScreenY();

        double deltax = newX - initX;
        double deltay = newY - initY;
        Cursor cursor = this.getCursor();

        if (Cursor.E_RESIZE.equals(cursor)) {
            setStageWidth(initWidth + deltax);
            mouseEvent.consume();
        } else if (Cursor.NE_RESIZE.equals(cursor)) {
            if (setStageHeight(initHeight - deltay)) {
                primaryStage.setY(initStageY + deltay);
            }
            setStageWidth(initWidth + deltax);
            mouseEvent.consume();
        } else if (Cursor.SE_RESIZE.equals(cursor)) {
            setStageWidth(initWidth + deltax);
            setStageHeight(initHeight + deltay);
            mouseEvent.consume();
        } else if (Cursor.S_RESIZE.equals(cursor)) {
            setStageHeight(initHeight + deltay);
            mouseEvent.consume();
        } else if (Cursor.W_RESIZE.equals(cursor)) {
            if (setStageWidth(initWidth - deltax)) {
                primaryStage.setX(initStageX + deltax);
            }
            mouseEvent.consume();
        } else if (Cursor.SW_RESIZE.equals(cursor)) {
            if (setStageWidth(initWidth - deltax)) {
                primaryStage.setX(initStageX + deltax);
            }
            setStageHeight(initHeight + deltay);
            mouseEvent.consume();
        } else if (Cursor.NW_RESIZE.equals(cursor)) {
            if (setStageWidth(initWidth - deltax)) {
                primaryStage.setX(initStageX + deltax);
            }
            if (setStageHeight(initHeight - deltay)) {
                primaryStage.setY(initStageY + deltay);
            }
            mouseEvent.consume();
        } else if (Cursor.N_RESIZE.equals(cursor)) {
            if (setStageHeight(initHeight - deltay)) {
                primaryStage.setY(initStageY + deltay);
            }
            mouseEvent.consume();
        } else if (allowMove) {
            primaryStage.setX(mouseEvent.getScreenX() - xOffset);
            primaryStage.setY(mouseEvent.getScreenY() - yOffset);
            mouseEvent.consume();
        }
    }

    private void updateInitMouseValues(MouseEvent mouseEvent) {
        initStageX = primaryStage.getX();
        initStageY = primaryStage.getY();
        initWidth = primaryStage.getWidth();
        initHeight = primaryStage.getHeight();
        initX = mouseEvent.getScreenX();
        initY = mouseEvent.getScreenY();
        xOffset = mouseEvent.getSceneX();
        yOffset = mouseEvent.getSceneY();
    }

    private boolean isRightEdge(double x) {
        final double width = this.getWidth();
        return x < width && x > width - contentPlaceHolder.snappedLeftInset();
    }

    private boolean isTopEdge(double y) {
        return y >= 0 && y < contentPlaceHolder.snappedLeftInset();
    }

    private boolean isBottomEdge(double y) {
        final double height = this.getHeight();
        return y < height && y > height - contentPlaceHolder.snappedLeftInset();
    }

    private boolean isLeftEdge(double x) {
        return x >= 0 && x < contentPlaceHolder.snappedLeftInset();
    }

    boolean setStageWidth(double width) {
        if (width >= primaryStage.getMinWidth() && width >= buttonsContainer.getMinWidth()) {
            primaryStage.setWidth(width);
            //            initX = newX;
            return true;
        } else if (width >= primaryStage.getMinWidth() && width <= buttonsContainer.getMinWidth()) {
            width = buttonsContainer.getMinWidth();
            primaryStage.setWidth(width);
        }
        return false;
    }

    boolean setStageHeight(double height) {
        if (height >= primaryStage.getMinHeight() && height >= buttonsContainer.getHeight()) {
            primaryStage.setHeight(height);
            //            initY = newY;
            return true;
        } else if (height >= primaryStage.getMinHeight() && height <= buttonsContainer.getHeight()) {
            height = buttonsContainer.getHeight();
            primaryStage.setHeight(height);
        }
        return false;
    }

    /**
     * set a speficed runnable when clicking on the close button
     *
     * @param onCloseButtonAction runnable to be executed
     */
    public void setOnCloseButtonAction(Runnable onCloseButtonAction) {
        this.onCloseButtonAction.set(onCloseButtonAction);
    }

    /**
     * this property is used to replace JavaFX maximization
     * with a custom one that prevents hiding windows taskbar when
     * the JFXDecorator is maximized.
     *
     * @return customMaximizeProperty whether to use custom maximization or not.
     */
    public final BooleanProperty customMaximizeProperty() {
        return this.customMaximize;
    }

    /**
     * @return whether customMaximizeProperty is active or not
     */
    public final boolean isCustomMaximize() {
        return this.customMaximizeProperty().get();
    }

    /**
     * set customMaximize property
     *
     * @param customMaximize
     */
    public final void setCustomMaximize(final boolean customMaximize) {
        this.customMaximizeProperty().set(customMaximize);
    }

    /**
     * @param maximized
     */
    public void setMaximized(boolean maximized) {
        if (this.maximized != maximized) {
            Platform.runLater(() -> {
                btnMax.fire();
            });
        }
    }

    /**
     * will change the decorator content
     *
     * @param content
     */
    public void setContent(Node content) {
        this.contentPlaceHolder.getChildren().setAll(content);
    }

    /**
     * will set the title
     *
     * @param text
     * @deprecated Use {@link JFXDecorator#setTitle(java.lang.String)} instead.
     */
    public void setText(String text) {
        setTitle(text);
    }

    /**
     * will get the title
     *
     * @deprecated Use {@link JFXDecorator#setTitle(java.lang.String)} instead.
     */
    public String getText() {
        return getTitle();
    }

    public String getTitle() {
        return title.get();
    }

    /**
     * By default this title property is bound to the primaryStage's title property.
     * <p>
     * To change it to something else, use <pre>
     *     {@code jfxDecorator.titleProperty().unbind();}</pre> first.
     */
    public StringProperty titleProperty() {
        return title;
    }

    /**
     * If you want the {@code primaryStage}'s title and the {@code JFXDecorator}'s title to be different, then
     * go ahead and use this method.
     * <p>
     * By default, this title property is bound to the {@code primaryStage}'s title property-so merely setting the
     * {@code primaryStage}'s title, will set the {@code JFXDecorator}'s title.
     */
    public void setTitle(String title) {
        this.title.unbind();
        this.title.set(title);
    }

    public void setGraphic(Node node) {
        if (graphic != null) {
            graphicContainer.getChildren().remove(graphic);
        }
        if (node != null) {
            graphicContainer.getChildren().add(0, node);
        }
        graphic = node;
    }

    public Node getGraphic() {
        return graphic;
    }
}
