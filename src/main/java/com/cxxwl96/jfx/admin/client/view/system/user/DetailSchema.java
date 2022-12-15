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

import com.cxxwl96.jfx.admin.client.annotation.JFXDetailField;
import com.cxxwl96.jfx.admin.client.component.detail.DateDetailValueFactory;
import com.cxxwl96.jfx.admin.client.component.detail.DateTimeDetailValueFactory;
import com.cxxwl96.jfx.admin.server.entity.Role;
import com.cxxwl96.jfx.admin.server.enums.Sex;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * DetailSchema
 *
 * @author cxxwl96
 * @since 2022/11/5 01:44
 */
@Data
@Accessors(chain = true)
public class DetailSchema {
    @JFXDetailField(title = "账户", rowIndex = 0, columnIndex = 0)
    private String username;

    @JFXDetailField(title = "角色", rowIndex = 0, columnIndex = 1, valueFactory = RoleDetailValueFactory.class)
    private List<Role> roles;

    @JFXDetailField(title = "姓名", rowIndex = 1, columnIndex = 0)
    private String name;

    @JFXDetailField(title = "性别", rowIndex = 1, columnIndex = 1)
    private Sex sex = Sex.UNSET;

    @JFXDetailField(title = "年龄", rowIndex = 2, columnIndex = 0)
    private Integer age;

    @JFXDetailField(title = "生日", rowIndex = 2, columnIndex = 1, valueFactory = DateDetailValueFactory.class)
    private LocalDateTime birthday;

    @JFXDetailField(title = "邮箱", rowIndex = 3, columnIndex = 0)
    private String email;

    @JFXDetailField(title = "电话", rowIndex = 3, columnIndex = 1)
    private String phone;

    @JFXDetailField(title = "最后登录时间", rowIndex = 4, columnIndex = 0, columnSpan = 2,
        valueFactory = DateTimeDetailValueFactory.class)
    private LocalDateTime lastLogin;

    @JFXDetailField(title = "备注", rowIndex = 5, columnIndex = 0, columnSpan = 2)
    private String remark;

    @JFXDetailField(title = "创建时间", rowIndex = 6, columnIndex = 0, columnSpan = 2,
        valueFactory = DateTimeDetailValueFactory.class)
    private LocalDateTime createTime;

}
