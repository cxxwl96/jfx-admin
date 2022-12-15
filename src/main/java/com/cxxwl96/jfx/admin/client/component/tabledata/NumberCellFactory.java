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

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;

/**
 * NumberCellFactory
 *
 * @author cxxwl96
 * @since 2022/10/21 01:11
 */
public class NumberCellFactory<S extends TableItem> extends AbstractTableCellFactory<S, Integer> {
    @Override
    public TableCell<S, Integer> call(TableColumn<S, Integer> param) {
        return new TableCell<S, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                this.setText(null);
                this.setGraphic(null);
                if (!empty) {
                    this.setText(String.valueOf(this.getIndex() + 1));
                }
            }
        };
    }
}
