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

package com.cxxwl96.jfx.admin.client.component.detail;

import com.cxxwl96.jfx.admin.client.common.JFXComponent;
import com.sun.javafx.collections.TrackableObservableList;

import java.lang.reflect.Field;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

import cn.hutool.core.util.StrUtil;
import io.datafx.controller.ViewController;
import javafx.beans.DefaultProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * JFXDetail
 *
 * @author cxxwl96
 * @since 2022/11/4 23:35
 */
@Slf4j
@ViewController("/views/component/detail/JFXDetail.fxml")
@DefaultProperty("detailFields")
public class JFXDetail<T> extends JFXComponent {
    /************************************************************************************************
     * 视图对象                                                                                       *
     ************************************************************************************************/
    @FXML
    private Label titleLabel;

    @FXML
    private GridPane detailGrid;

    @FXML
    private HBox toolBottom;

    /************************************************************************************************
     * 视图绑定对象                                                                                   *
     ************************************************************************************************/

    // 详情标题
    private SimpleStringProperty title;

    // 详情字段
    private final ObservableList<JFXDetailField> detailFields = new TrackableObservableList<JFXDetailField>() {
        @Override
        protected void onChanged(ListChangeListener.Change<JFXDetailField> change) {
            // 刷新详情字段
            refreshDetailFields();
        }
    };

    /************************************************************************************************
     * 类属性                                                                                        *
     ************************************************************************************************/

    /************************************************************************************************
     * 初始化页面及事件                                                                                *
     ************************************************************************************************/

    @PostConstruct
    private void init() {
        // 初始化详情标题
        title = new SimpleStringProperty();
        titleLabel.textProperty().bindBidirectional(title);
    }

    /**
     * 刷新详情字段
     */
    private void refreshDetailFields() {
        // 取最大行最大列，此处不能使用流方式取
        int maxRow = 0;
        int maxCol = 0;
        for (JFXDetailField detailField : detailFields) {
            maxRow = Math.max(detailField.getRowIndex(), maxRow);
            maxCol = Math.max(detailField.getColumnIndex(), maxCol);
        }
        // 添加行列约束
        detailGrid.getRowConstraints().clear();
        detailGrid.getColumnConstraints().clear();
        detailGrid.getChildren().clear();
        for (int i = 0; i <= maxRow; i++) {
            final RowConstraints constraints = new RowConstraints();
            constraints.setFillHeight(true);
            constraints.setVgrow(Priority.ALWAYS);
            constraints.setValignment(VPos.CENTER);
            detailGrid.getRowConstraints().add(constraints);
        }
        // 添加列约束
        for (int i = 0; i <= maxCol; i++) {
            final ColumnConstraints labelConstraints = new ColumnConstraints();
            labelConstraints.setFillWidth(true);
            labelConstraints.setHgrow(Priority.ALWAYS);
            labelConstraints.setMinWidth(50);
            labelConstraints.setPrefWidth(100);
            labelConstraints.setMaxWidth(200);
            final ColumnConstraints controlConstraints = new ColumnConstraints();
            controlConstraints.setFillWidth(true);
            controlConstraints.setHgrow(Priority.ALWAYS);
            controlConstraints.setMinWidth(50);
            controlConstraints.setPrefWidth(200);
            detailGrid.getColumnConstraints().addAll(labelConstraints, controlConstraints);
        }
        // 添加表单label及控件
        for (JFXDetailField detailField : detailFields) {
            final Label label = createCellTitle(detailField);
            GridPane.setRowIndex(label, detailField.getRowIndex());
            GridPane.setColumnIndex(label, detailField.getColumnIndex() * 2);
            GridPane.setRowSpan(label, detailField.getRowSpan());
            detailGrid.getChildren().add(label);
            final Node value = createCellValue(detailField);
            GridPane.setRowIndex(value, detailField.getRowIndex());
            GridPane.setColumnIndex(value, detailField.getColumnIndex() * 2 + 1);
            GridPane.setRowSpan(value, detailField.getRowSpan());
            GridPane.setColumnSpan(value, detailField.getColumnSpan() * 2 - 1);
            detailGrid.getChildren().add(value);
        }
    }

    /**
     * 创建详情字段title节点
     *
     * @param detailField detailField
     * @return 详情字段title节点
     */
    private Label createCellTitle(JFXDetailField detailField) {
        final Label label = new Label(detailField.getLabelText());
        GridPane.setVgrow(label, Priority.ALWAYS);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setMaxHeight(Double.MAX_VALUE);
        label.setPadding(new Insets(10));
        label.getStyleClass().addAll("cell", "cell-title");
        if (StrUtil.isNotBlank(detailField.getLabelText())) {
            label.setTooltip(new Tooltip(detailField.getLabelText()));
        }
        return label;
    }

    /**
     * 创建详情字段value节点
     *
     * @param detailField detailField
     * @return 详情字段value节点
     */
    private Node createCellValue(JFXDetailField detailField) {
        final VBox vBox = new VBox();
        vBox.getStyleClass().addAll("cell", "cell-value");
        vBox.setMaxWidth(Double.MAX_VALUE);
        vBox.setMaxWidth(Double.MAX_VALUE);
        vBox.setAlignment(Pos.CENTER_LEFT);
        vBox.setPadding(new Insets(10));
        GridPane.setVgrow(vBox, Priority.ALWAYS);
        Node content = detailField.getContent();
        if (content == null) {
            return vBox;
        }
        vBox.getChildren().add(content);
        return vBox;
    }
    /************************************************************************************************
     * 外部方法                                                                                      *
     ************************************************************************************************/
    /**
     * 设置详情约束
     *
     * @param schemaClass 约束类
     */
    @SneakyThrows
    public void initDetailSchema(@NotNull Class<?> schemaClass) {
        if (schemaClass == null) {
            throw new NullPointerException("Detail column schema is null");
        }
        final Field[] fields = schemaClass.getDeclaredFields();
        for (Field field : fields) {
            String title = StrUtil.EMPTY; // 标题
            Class<? extends DetailValueFactory> valueFactoryClass = DetailValueFactory.class; // 控件类型
            String name = field.getName(); // 对应字段名
            int rowIndex = 0;
            int columnIndex = 0;
            int rowSpan = 1;
            int columnSpan = 1;
            if (field.isAnnotationPresent(com.cxxwl96.jfx.admin.client.annotation.JFXDetailField.class)) {
                final com.cxxwl96.jfx.admin.client.annotation.JFXDetailField annotation = field.getDeclaredAnnotation(
                    com.cxxwl96.jfx.admin.client.annotation.JFXDetailField.class);
                title = annotation.title();
                valueFactoryClass = annotation.valueFactory();
                if (StrUtil.isNotBlank(annotation.name())) {
                    name = annotation.name();
                }
                rowIndex = annotation.rowIndex();
                columnIndex = annotation.columnIndex();
                rowSpan = annotation.rowSpan();
                columnSpan = annotation.columnSpan();
            }
            // 添加到表单容器
            final JFXDetailField detailField = new JFXDetailField(rowIndex, columnIndex);
            detailField.setRowSpan(rowSpan);
            detailField.setColumnSpan(columnSpan);
            detailField.setLabelText(title);
            detailField.setValueFactory(valueFactoryClass.newInstance());
            detailField.setName(name);
            detailFields.add(detailField);
        }
    }

    /**
     * 设置详情标题
     *
     * @param title 标题
     * @param graphic 图标
     */
    public void setTitle(String title, Node graphic) {
        this.titleLabel.setText(title);
        this.titleLabel.setGraphic(graphic);
    }

    /**
     * 设置详情数据
     *
     * @param detailData 详情数据
     */
    public void setDetailData(@NotNull T detailData) {
        if (detailData == null) {
            throw new NullPointerException("DetailData is null");
        }
        // 设置详情数据
        detailFields.forEach(detailField -> {
            final Node value = detailField.getValueFactory().getValue(detailData, detailField.getName());
            detailField.setContent(value);
        });
        // 刷新详情字段
        refreshDetailFields();
    }

    /************************************************************************************************
     * setter、getter、property                                                                      *
     ************************************************************************************************/

    public String getTitle() {
        return title.get();
    }

    public SimpleStringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public GridPane getDetailGrid() {
        return detailGrid;
    }

    public ObservableList<JFXDetailField> getDetailFields() {
        return detailFields;
    }

    public HBox getToolBottom() {
        return toolBottom;
    }
}
