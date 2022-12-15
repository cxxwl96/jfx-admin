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

import java.util.function.BiFunction;

import javafx.scene.Node;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;

/**
 * 操作单元格
 *
 * @author cxxwl96
 * @since 2022/10/21 01:27
 */
public class OperateCellFactory<S extends TableItem> extends AbstractTableCellFactory<S, Node> {
    private final BiFunction<TableColumn<S, Node>, TableCell<S, Node>, Node> operateCallback;

    public OperateCellFactory(BiFunction<TableColumn<S, Node>, TableCell<S, Node>, Node> operateCallback) {
        this.operateCallback = operateCallback;
    }

    @Override
    public TableCell<S, Node> call(TableColumn<S, Node> param) {
        return new TableCell<S, Node>() {
            @Override
            protected void updateItem(Node item, boolean empty) {
                super.updateItem(item, empty);
                this.setText(null);
                this.setGraphic(null);
                if (!empty) {
                    this.setGraphic(operateCallback.apply(param, this));
                }
            }
        };
    }
}
