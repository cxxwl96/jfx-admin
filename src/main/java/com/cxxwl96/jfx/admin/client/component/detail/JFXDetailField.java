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

import javafx.beans.DefaultProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Node;
import lombok.extern.slf4j.Slf4j;

/**
 * JFXDetailField
 *
 * @author cxxwl96
 * @since 2022/10/26 21:37
 */
@Slf4j
@DefaultProperty("content")
public class JFXDetailField extends JFXComponent {
    // label标题
    private final SimpleStringProperty labelText = new SimpleStringProperty();

    private final SimpleIntegerProperty rowIndex = new SimpleIntegerProperty(0);

    private final SimpleIntegerProperty columnIndex = new SimpleIntegerProperty(0);

    private final SimpleIntegerProperty rowSpan = new SimpleIntegerProperty(1);

    private final SimpleIntegerProperty columnSpan = new SimpleIntegerProperty(1);

    private final SimpleStringProperty name = new SimpleStringProperty();

    private final SimpleObjectProperty<Node> content = new SimpleObjectProperty<>();

    private DetailValueFactory valueFactory;

    public JFXDetailField() {
        this(0, 0);
    }

    public JFXDetailField(int rowIndex, int columnIndex) {
        setRowIndex(rowIndex);
        setColumnIndex(columnIndex);
    }

    public String getLabelText() {
        return labelText.get();
    }

    public SimpleStringProperty labelTextProperty() {
        return labelText;
    }

    public void setLabelText(String labelText) {
        this.labelText.set(labelText);
    }

    public int getRowIndex() {
        return rowIndex.get();
    }

    public SimpleIntegerProperty rowIndexProperty() {
        return rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex.set(rowIndex);
    }

    public int getColumnIndex() {
        return columnIndex.get();
    }

    public SimpleIntegerProperty columnIndexProperty() {
        return columnIndex;
    }

    public void setColumnIndex(int columnIndex) {
        this.columnIndex.set(columnIndex);
    }

    public int getRowSpan() {
        return rowSpan.get();
    }

    public SimpleIntegerProperty rowSpanProperty() {
        return rowSpan;
    }

    public void setRowSpan(int rowSpan) {
        this.rowSpan.set(rowSpan);
    }

    public int getColumnSpan() {
        return columnSpan.get();
    }

    public SimpleIntegerProperty columnSpanProperty() {
        return columnSpan;
    }

    public void setColumnSpan(int columnSpan) {
        this.columnSpan.set(columnSpan);
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public Node getContent() {
        return content.get();
    }

    public SimpleObjectProperty<Node> contentProperty() {
        return content;
    }

    public void setContent(Node content) {
        this.content.set(content);
    }

    public DetailValueFactory getValueFactory() {
        return valueFactory;
    }

    public void setValueFactory(DetailValueFactory valueFactory) {
        this.valueFactory = valueFactory;
    }
}
