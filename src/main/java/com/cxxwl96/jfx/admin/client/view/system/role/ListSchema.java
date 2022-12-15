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

package com.cxxwl96.jfx.admin.client.view.system.role;

import com.cxxwl96.jfx.admin.client.annotation.JFXTableColumn;
import com.cxxwl96.jfx.admin.client.component.tabledata.StatusCellFactory;
import com.cxxwl96.jfx.admin.client.component.tabledata.TableItem;
import com.cxxwl96.jfx.admin.server.enums.RoleType;
import com.cxxwl96.jfx.admin.server.enums.Status;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * ListSchema
 *
 * @author cxxwl96
 * @since 2022/11/8 00:02
 */
@Data
@Accessors(chain = true)
public class ListSchema implements TableItem {
    @JFXTableColumn(show = false)
    private String id;

    @JFXTableColumn(title = "名称")
    private String name;

    @JFXTableColumn(title = "编码")
    private String code;

    @JFXTableColumn(title = "等级")
    private Integer level;

    @JFXTableColumn(title = "类型")
    private RoleType type;

    @JFXTableColumn(title = "状态", cellFactory = StatusCellFactory.class)
    private Status status;
}
