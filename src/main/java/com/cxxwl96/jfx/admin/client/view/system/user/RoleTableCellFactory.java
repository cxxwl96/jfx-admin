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

package com.cxxwl96.jfx.admin.client.view.system.user;

import com.cxxwl96.jfx.admin.client.component.JFXTag;
import com.cxxwl96.jfx.admin.client.component.tabledata.AbstractTableCellFactory;
import com.cxxwl96.jfx.admin.client.component.tabledata.TableItem;
import com.cxxwl96.jfx.admin.server.entity.Role;

import java.util.Comparator;
import java.util.List;

import cn.hutool.core.util.StrUtil;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.HBox;

/**
 * RoleTableCellFactory
 *
 * @author cxxwl96
 * @since 2022/11/27 15:17
 */
public class RoleTableCellFactory extends AbstractTableCellFactory<TableItem, List<Role>> {
    @Override
    public TableCell<TableItem, List<Role>> call(TableColumn<TableItem, List<Role>> param) {
        return new TableCell<TableItem, List<Role>>() {
            @Override
            protected void updateItem(List<Role> roles, boolean empty) {
                super.updateItem(roles, empty);
                this.setText(null);
                this.setGraphic(null);
                if (!empty && roles != null) {

                    roles.stream().max(Comparator.comparing(Role::getLevel)).ifPresent(role -> {
                        final String tagText;
                        if (StrUtil.isNotBlank(role.getCode())) {
                            tagText = role.getName() + " " + role.getCode();
                        } else {
                            tagText = role.getName();
                        }
                        final JFXTag tag = new JFXTag();
                        tag.setText(tagText);
                        tag.setType(JFXTag.JFXTagType.PRIMARY);
                        final HBox hBox = new HBox(tag);
                        hBox.setAlignment(Pos.CENTER);
                        super.setGraphic(hBox);
                    });
                }
            }
        };
    }
}