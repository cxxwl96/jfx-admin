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

import com.cxxwl96.jfx.admin.client.annotation.JFXDetailField;
import com.cxxwl96.jfx.admin.client.component.detail.DateTimeDetailValueFactory;
import com.cxxwl96.jfx.admin.server.enums.RoleType;
import com.cxxwl96.jfx.admin.server.enums.Status;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * DetailSchema
 *
 * @author cxxwl96
 * @since 2022/11/8 00:04
 */
@Data
@Accessors(chain = true)
public class DetailSchema {
    @JFXDetailField(title = "名称", rowIndex = 0, columnIndex = 0)
    private String name;

    @JFXDetailField(title = "编码", rowIndex = 0, columnIndex = 1)
    private String code;

    @JFXDetailField(title = "等级", rowIndex = 1, columnIndex = 0)
    private Integer level = 0;

    @JFXDetailField(title = "类型", rowIndex = 1, columnIndex = 1)
    private RoleType type;

    @JFXDetailField(title = "状态", rowIndex = 2, columnIndex = 0, columnSpan = 2)
    private Status status = Status.ENABLE;

    @JFXDetailField(title = "描述", rowIndex = 3, columnIndex = 0, columnSpan = 2)
    private String description;

    @JFXDetailField(title = "备注", rowIndex = 4, columnIndex = 0, columnSpan = 2)
    private String remark;

    @JFXDetailField(title = "创建时间", rowIndex = 5, columnIndex = 0, columnSpan = 2,
        valueFactory = DateTimeDetailValueFactory.class)
    private LocalDateTime createTime;
}
