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

import com.cxxwl96.jfx.admin.server.enums.Status;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

/**
 * StatusCellFactory
 *
 * @author cxxwl96
 * @since 2022/11/8 22:27
 */
public class StatusCellFactory extends AbstractTableCellFactory<TableItem, Status> {
    @Override
    public TableCell<TableItem, Status> call(TableColumn<TableItem, Status> param) {
        return new TableCell<TableItem, Status>() {
            @Override
            protected void updateItem(Status item, boolean empty) {
                super.updateItem(item, empty);
                this.setText(null);
                this.setGraphic(null);
                if (!empty && item != null) {
                    final Node statusNode = createStatusNode(item);
                    super.setGraphic(statusNode);
                }
            }
        };
    }

    private Node createStatusNode(Status status) {
        final Circle circle = new Circle(5);
        circle.setStrokeWidth(0);
        if (status == Status.ENABLE) {
            circle.setFill(Paint.valueOf("#87d068")); // 绿色
        } else {
            circle.setFill(Paint.valueOf("#f50f50")); // 红色
        }
        final Label label = new Label(status.getRemark(), circle);
        final HBox hBox = new HBox(label);
        hBox.setAlignment(Pos.CENTER);
        return hBox;
    }
}
