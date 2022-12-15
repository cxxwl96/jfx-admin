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

import com.cxxwl96.jfx.admin.client.annotation.JFXTableColumn;
import com.cxxwl96.jfx.admin.client.component.tabledata.LocalDateTimeCellFactory;
import com.cxxwl96.jfx.admin.client.component.tabledata.TableItem;
import com.cxxwl96.jfx.admin.server.entity.Role;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * ListSchema
 *
 * @author cxxwl96
 * @since 2022/10/20 00:19
 */
@Data
@Accessors(chain = true)
public class ListSchema implements TableItem {
    @JFXTableColumn(show = false)
    private String id;

    @JFXTableColumn(title = "账户")
    private String username;

    @JFXTableColumn(title = "姓名")
    private String name;

    @JFXTableColumn(title = "角色", cellFactory = RoleTableCellFactory.class)
    private List<Role> roles;

    @JFXTableColumn(title = "最后登录时间", cellFactory = LocalDateTimeCellFactory.class)
    private LocalDateTime lastLogin;
}

