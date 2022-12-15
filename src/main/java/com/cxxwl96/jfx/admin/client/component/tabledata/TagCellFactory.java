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

package com.cxxwl96.jfx.admin.client.component.tabledata;

import com.cxxwl96.jfx.admin.client.component.JFXTag;

import java.util.List;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.FlowPane;

/**
 * TagCellFactory
 *
 * @author cxxwl96
 * @since 2022/10/21 21:01
 */
public class TagCellFactory extends AbstractTableCellFactory<TableItem, List<String>> {
    @Override
    public TableCell<TableItem, List<String>> call(TableColumn<TableItem, List<String>> param) {
        return new TableCell<TableItem, List<String>>() {
            @Override
            protected void updateItem(List<String> items, boolean empty) {
                super.updateItem(items, empty);
                this.setText(null);
                this.setGraphic(null);
                if (!empty && items != null) {
                    final FlowPane pane = new FlowPane();
                    pane.setVgap(10);
                    pane.setHgap(10);
                    items.forEach(item -> pane.getChildren().add(new JFXTag(item)));
                    super.setGraphic(pane);
                }
            }
        };
    }
}
