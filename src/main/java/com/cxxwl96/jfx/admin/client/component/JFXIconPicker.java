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

import com.cxxwl96.jfx.admin.client.utils.IconUtil;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPopup;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.svg.SVGGlyph;
import com.jfoenix.svg.SVGGlyphLoader;

import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.pinyin.PinyinUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;

/**
 * JFXIconPicker
 *
 * @author cxxwl96
 * @since 2022/10/9 23:38
 */
@Slf4j
public class JFXIconPicker extends Label {

    // 窗体宽度
    private final static int WIDTH = 500;

    // 窗体高度
    private final static int HEIGHT = 300;

    // 按钮大小
    private final static int btnSize = 30;

    // popup窗体
    private final JFXPopup popup = new JFXPopup(createPopupContent());

    // 缓存的ICON按钮
    private static List<Label> iconBtns;

    // 选择的ICON值
    private final SimpleStringProperty valueProperty = new SimpleStringProperty();

    // 默认图标
    private Node defaultGraphic = IconUtil.getIcon("material-symbols:border-all-rounded");

    // 值的变更事件
    private ChangeListener<String> changeHandler;

    public JFXIconPicker() {
        super();
        super.setPrefSize(btnSize, btnSize);
        super.setMinSize(btnSize, btnSize);
        super.setMaxSize(btnSize, btnSize);
        super.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        super.setGraphicTextGap(0);
        super.setAlignment(Pos.CENTER);
        super.getStyleClass().add("jfx-label-button");
        if (StrUtil.isBlank(valueProperty.get())) {
            super.setGraphic(defaultGraphic);
        } else {
            super.setGraphic(IconUtil.getIcon(valueProperty.get()));
        }
        super.addEventHandler(MouseEvent.MOUSE_CLICKED, this::showPopup);
    }

    /**
     * 变更事件
     *
     * @param handler 事件处理
     */
    public void onChange(ChangeListener<String> handler) {
        changeHandler = handler;
    }

    /**
     * 显示popup窗体
     *
     * @param event 事件，可为null
     */
    private void showPopup(@Nullable Event event) {
        popup.show(this, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.RIGHT);
    }

    /**
     * 创建popup窗体内容
     *
     * @return 窗体内容
     */
    private VBox createPopupContent() {
        // 搜索图标
        Label searchLabel = new Label();
        searchLabel.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        searchLabel.setGraphic(IconUtil.getIcon("search"));
        searchLabel.setMaxHeight(Double.MAX_VALUE);
        // 搜索框
        JFXTextField searchField = new JFXTextField();
        searchField.setPromptText("搜索ICON");
        searchField.setStyle("-fx-background-size: 14px; -fx-font-size: 14px;");
        searchField.setLabelFloat(true);
        HBox.setHgrow(searchField, Priority.ALWAYS);
        // 清空选择按钮
        JFXButton clearBtn = new JFXButton();
        clearBtn.setPrefSize(btnSize, btnSize);
        clearBtn.setMinSize(btnSize, btnSize);
        clearBtn.setMaxSize(btnSize, btnSize);
        clearBtn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        clearBtn.setGraphic(IconUtil.getIcon("icon-ic-baseline-delete-outline"));
        clearBtn.setCursor(Cursor.HAND);
        Tooltip.install(clearBtn, new Tooltip("清空选择的ICON"));
        clearBtn.setOnAction(event -> {
            String value = valueProperty.get();
            valueProperty.set(null);
            if (changeHandler != null) {
                changeHandler.changed(valueProperty, value, valueProperty.get());
            }
            popup.hide();
        });
        HBox header = new HBox(searchLabel, searchField, clearBtn);
        header.setSpacing(10);
        // 图标内容区
        FlowPane flowPane = new FlowPane();
        flowPane.setCache(true);
        flowPane.setHgap(10);
        flowPane.setVgap(10);
        flowPane.getChildren().addAll(loadOrSearchButtons(null));
        flowPane.setPrefSize(WIDTH, HEIGHT);
        flowPane.getStyleClass().add("jfx-flow-pane");
        ScrollPane scrollPane = new ScrollPane(flowPane);
        scrollPane.setCache(true);
        scrollPane.setFitToWidth(true);
        VBox content = new VBox(header, scrollPane);
        content.setSpacing(10);
        content.setPadding(new Insets(30, 10, 10, 10));
        // 搜索事件
        searchField.textProperty().addListener(event -> {
            final List<Label> iconBtns = loadOrSearchButtons(searchField.getText());
            flowPane.getChildren().clear();
            flowPane.getChildren().addAll(iconBtns);
        });
        return content;
    }

    /**
     * 加载(搜索)ICON按钮
     *
     * @param searchKey 搜索关键字，为null表示返回所有ICON按钮
     * @return 搜索到的ICON按钮
     */
    private List<Label> loadOrSearchButtons(@Nullable String searchKey) {
        if (iconBtns == null) {
            iconBtns = SVGGlyphLoader.getAllGlyphsIDs().stream().sorted().map(glyphName -> {
                try {
                    final SVGGlyph glyph = SVGGlyphLoader.getIcoMoonGlyph(glyphName);
                    glyph.setSize(20);
                    return createIconButton(glyph);
                } catch (Exception exception) {
                    log.error(exception.getMessage(), exception);
                    return null;
                }
            }).filter(Objects::nonNull).collect(Collectors.toList());
        }
        if (StrUtil.isBlank(searchKey)) {
            return iconBtns;
        }
        final String searchKeyLowerCase = searchKey.toLowerCase(Locale.ROOT);
        final String searchKeyPinyin = PinyinUtil.getPinyin(searchKey).toLowerCase(Locale.ROOT);
        return iconBtns.stream().filter(iconBtn -> {
            final String glyphName = (String) iconBtn.getUserData();
            if (StrUtil.isBlank(glyphName)) {
                return false;
            }
            final String glyphNameLowerCase = glyphName.toLowerCase(Locale.ROOT);
            return glyphNameLowerCase.contains(searchKeyLowerCase) || glyphNameLowerCase.contains(searchKeyPinyin);
        }).collect(Collectors.toList());
    }

    /**
     * 创建ICON按钮
     *
     * @param glyph glyph
     * @return ICON按钮
     */
    private Label createIconButton(SVGGlyph glyph) {
        Label iconBtn = new Label(null, glyph);
        iconBtn.getStyleClass().add("jfx-label-button");
        iconBtn.setPrefSize(btnSize, btnSize);
        iconBtn.setMinSize(btnSize, btnSize);
        iconBtn.setMaxSize(btnSize, btnSize);
        iconBtn.setCursor(Cursor.HAND);
        iconBtn.setCache(true); // 对于提高滚动窗格中的动画性能非常重要，因此按钮被视为图像
        iconBtn.setUserData(glyph.getName());
        iconBtn.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            String glyphName = glyph.getName();
            if (changeHandler != null) {
                changeHandler.changed(valueProperty, valueProperty.get(), glyphName);
            }
            valueProperty.set(glyphName);
            popup.hide();
            super.setGraphic(IconUtil.getIcon(glyphName));
        });
        Tooltip.install(iconBtn, new Tooltip(glyph.getName()));
        return iconBtn;
    }

    public String getValueProperty() {
        return valueProperty.get();
    }

    public SimpleStringProperty valuePropertyProperty() {
        return valueProperty;
    }

    public void setValueProperty(String valueProperty) {
        this.valueProperty.set(valueProperty);
    }

    public Node getDefaultGraphic() {
        return defaultGraphic;
    }

    public void setDefaultGraphic(Node defaultGraphic) {
        this.defaultGraphic = defaultGraphic;
    }
}
