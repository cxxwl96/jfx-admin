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

import com.cxxwl96.jfx.admin.server.base.enums.IEnum;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;

/**
 * EnumTableCellFactory
 *
 * @author cxxwl96
 * @since 2022/11/10 21:40
 */
public class EnumTableCellFactory extends AbstractTableCellFactory<TableItem, IEnum<?>> {
    @Override
    public TableCell<TableItem, IEnum<?>> call(TableColumn<TableItem, IEnum<?>> param) {
        return new TableCell<TableItem, IEnum<?>>() {
            @Override
            protected void updateItem(IEnum<?> item, boolean empty) {
                super.updateItem(item, empty);
                this.setText(null);
                this.setGraphic(null);
                if (!empty) {
                    this.setText(item.getRemark());
                }
            }
        };
    }
}
