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

package com.cxxwl96.jfx.admin.client.component.iconswitch;

import com.jfoenix.svg.SVGGlyph;

import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * IconSwitchSkin
 *
 * @author cxxwl96
 * @since 2022/12/5 00:10
 */
public class IconSwitchSkin extends SkinBase<IconSwitch> {
    private static final double PREFERRED_WIDTH = 80;

    private static final double PREFERRED_HEIGHT = 32;

    private static final double MINIMUM_WIDTH = 20;

    private static final double MINIMUM_HEIGHT = 8;

    private static final double MAXIMUM_WIDTH = 1024;

    private static final double MAXIMUM_HEIGHT = 1024;

    private Pane pane;

    private Rectangle background;

    private SVGGlyph symbol;

    private Label thumb;

    private TranslateTransition moveToDeselected;

    private TranslateTransition moveToSelected;

    public IconSwitchSkin(final IconSwitch CONTROL) {
        super(CONTROL);
        init();
        initGraphics();
        registerListeners();
        resize();
    }

    private void init() {
        if (Double.compare(getSkinnable().getPrefWidth(), 0.0) <= 0
            || Double.compare(getSkinnable().getPrefHeight(), 0.0) <= 0
            || Double.compare(getSkinnable().getWidth(), 0.0) <= 0
            || Double.compare(getSkinnable().getHeight(), 0.0) <= 0) {
            if (getSkinnable().getPrefWidth() > 0 && getSkinnable().getPrefHeight() > 0) {
                getSkinnable().setPrefSize(getSkinnable().getPrefWidth(), getSkinnable().getPrefHeight());
            } else {
                getSkinnable().setPrefSize(PREFERRED_WIDTH, PREFERRED_HEIGHT);
            }
        }
        if (Double.compare(getSkinnable().getMinWidth(), 0.0) <= 0
            || Double.compare(getSkinnable().getMinHeight(), 0.0) <= 0) {
            getSkinnable().setMinSize(MINIMUM_WIDTH, MINIMUM_HEIGHT);
        }
        if (Double.compare(getSkinnable().getMaxWidth(), 0.0) <= 0
            || Double.compare(getSkinnable().getMaxHeight(), 0.0) <= 0) {
            getSkinnable().setMaxSize(MAXIMUM_WIDTH, MAXIMUM_HEIGHT);
        }
        if (getSkinnable().getPrefWidth() != PREFERRED_WIDTH || getSkinnable().getPrefHeight() != PREFERRED_HEIGHT) {
            double aspectRatio = getSkinnable().getPrefHeight() / getSkinnable().getPrefWidth();
        }
    }

    private void initGraphics() {
        background = new Rectangle();
        background.getStyleClass().setAll("background");
        symbol = new SVGGlyph("");
        symbol.setSize(20, 20);
        symbol.setId("symbol-a");
        thumb = new Label("");
        thumb.setGraphic(symbol);
        thumb.setAlignment(Pos.CENTER);
        thumb.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        thumb.getStyleClass().setAll("thumb-a");
        thumb.setMouseTransparent(true);
        pane = new Pane(background, thumb);
        pane.getStyleClass().setAll("icon-switch");
        moveToDeselected = new TranslateTransition(Duration.millis(180), thumb);
        moveToSelected = new TranslateTransition(Duration.millis(180), thumb);
        // Add all nodes
        getChildren().setAll(pane);
    }

    private void registerListeners() {
        getSkinnable().widthProperty().addListener(observable -> handleControlPropertyChanged("RESIZE"));
        getSkinnable().heightProperty().addListener(observable -> handleControlPropertyChanged("RESIZE"));
        getSkinnable().selectedProperty().addListener(observable -> handleControlPropertyChanged("SELECTED"));
        pane.setOnMouseClicked(mouseEvent -> getSkinnable().setSelected(!getSkinnable().isSelected()));
    }

    protected void handleControlPropertyChanged(final String PROPERTY) {
        if ("RESIZE".equals(PROPERTY)) {
            resize();
        } else if ("SELECTED".equals(PROPERTY)) {
            thumb.getStyleClass().removeAll("thumb-a-selected", "thumb-a");
            thumb.getStyleClass().setAll(getSkinnable().isSelected() ? "thumb-a-selected" : "thumb-a");
            symbol.setId(getSkinnable().isSelected() ? "symbol-a-selected" : "symbol-a");
            if (getSkinnable().isSelected()) {
                moveToSelected.play();
            } else {
                moveToDeselected.play();
            }
        }
    }

    @Override
    protected double computeMinWidth(final double HEIGHT, double TOP_INSET, double RIGHT_INSET, double BOTTOM_INSET,
        double LEFT_INSET) {
        return super.computeMinWidth(Math.max(MINIMUM_HEIGHT, HEIGHT - TOP_INSET - BOTTOM_INSET), TOP_INSET,
            RIGHT_INSET, BOTTOM_INSET, LEFT_INSET);
    }

    @Override
    protected double computeMinHeight(final double WIDTH, double TOP_INSET, double RIGHT_INSET, double BOTTOM_INSET,
        double LEFT_INSET) {
        return super.computeMinHeight(Math.max(MINIMUM_WIDTH, WIDTH - LEFT_INSET - RIGHT_INSET), TOP_INSET, RIGHT_INSET,
            BOTTOM_INSET, LEFT_INSET);
    }

    @Override
    protected double computeMaxWidth(final double HEIGHT, double TOP_INSET, double RIGHT_INSET, double BOTTOM_INSET,
        double LEFT_INSET) {
        return super.computeMaxWidth(Math.min(MAXIMUM_HEIGHT, HEIGHT - TOP_INSET - BOTTOM_INSET), TOP_INSET,
            RIGHT_INSET, BOTTOM_INSET, LEFT_INSET);
    }

    @Override
    protected double computeMaxHeight(final double WIDTH, double TOP_INSET, double RIGHT_INSET, double BOTTOM_INSET,
        double LEFT_INSET) {
        return super.computeMaxHeight(Math.min(MAXIMUM_WIDTH, WIDTH - LEFT_INSET - RIGHT_INSET), TOP_INSET, RIGHT_INSET,
            BOTTOM_INSET, LEFT_INSET);
    }

    @Override
    protected double computePrefWidth(final double HEIGHT, double TOP_INSET, double RIGHT_INSET, double BOTTOM_INSET,
        double LEFT_INSET) {
        double prefHeight = PREFERRED_HEIGHT;
        if (HEIGHT != -1) {
            prefHeight = Math.max(0, HEIGHT - TOP_INSET - BOTTOM_INSET);
        }
        return super.computePrefWidth(prefHeight, TOP_INSET, RIGHT_INSET, BOTTOM_INSET, LEFT_INSET);
    }

    @Override
    protected double computePrefHeight(final double WIDTH, double TOP_INSET, double RIGHT_INSET, double BOTTOM_INSET,
        double LEFT_INSET) {
        double prefWidth = PREFERRED_WIDTH;
        if (WIDTH != -1) {
            prefWidth = Math.max(0, WIDTH - LEFT_INSET - RIGHT_INSET);
        }
        return super.computePrefHeight(prefWidth, TOP_INSET, RIGHT_INSET, BOTTOM_INSET, LEFT_INSET);
    }

    // ******************** Private Methods ***********************************
    private void resize() {
        double width = getSkinnable().getWidth();
        double height = getSkinnable().getHeight();
        double size = Math.min(width, height) * 0.65;

        if (width > 0 && height > 0) {
            background.heightProperty().set(height * 0.35);
            background.widthProperty().set(width);
            background.arcHeightProperty().setValue(background.getHeight());
            background.arcWidthProperty().setValue(background.getHeight());
            background.setTranslateY((height - background.getHeight()) / 2.0);

            thumb.setPrefSize(size, size);
            thumb.setTranslateX(getSkinnable().isSelected() ? width - size : 0);
            thumb.setTranslateY((height - size) / 2.0);

            symbol.setSize((size * 0.65), (size * 0.65));

            moveToDeselected.setFromX(width - size);
            moveToDeselected.setToX(0);

            moveToSelected.setFromX(0);
            moveToSelected.setToX(width - size);
        }
    }

}
