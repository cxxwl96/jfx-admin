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

import java.time.LocalDateTime;

import cn.hutool.core.date.LocalDateTimeUtil;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;

/**
 * LocalDateTimeCellFactory
 *
 * @author cxxwl96
 * @since 2022/10/20 23:05
 */
public class LocalDateTimeCellFactory extends AbstractTableCellFactory<TableItem, LocalDateTime> {
    @Override
    public TableCell<TableItem, LocalDateTime> call(TableColumn<TableItem, LocalDateTime> param) {
        return new TableCell<TableItem, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                this.setText(null);
                this.setGraphic(null);
                if (!empty) {
                    this.setText(LocalDateTimeUtil.format(item, "yyyy-MM-dd HH:mm:ss.SSS"));
                }
            }
        };
    }
}
