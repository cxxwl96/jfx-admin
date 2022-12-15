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

import com.cxxwl96.jfx.admin.client.annotation.JFXFormField;
import com.cxxwl96.jfx.admin.client.component.form.ComboBoxControlFactory;
import com.cxxwl96.jfx.admin.client.component.form.RadioButtonControlFactory;
import com.cxxwl96.jfx.admin.client.component.form.SliderControlFactory;
import com.cxxwl96.jfx.admin.client.component.form.TextAreaControlFactory;
import com.cxxwl96.jfx.admin.server.enums.RoleType;
import com.cxxwl96.jfx.admin.server.enums.Status;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * FormSchema
 *
 * @author cxxwl96
 * @since 2022/11/8 00:04
 */
@Data
@Accessors(chain = true)
public class FormSchema {
    @JFXFormField(title = "id", show = false)
    private String id;

    @JFXFormField(title = "名称", rowIndex = 0, columnIndex = 0, columnSpan = 2, required = true)
    private String name;

    @JFXFormField(title = "编码", rowIndex = 1, columnIndex = 0, columnSpan = 2, required = true,
        pattern = "^(?![rR][oO][lL][eE]_?)[a-zA-Z0-9]{1,20}$",
        message = "角色编码仅数字或字母组成的20位以内字符串，且不能以“role“、“role_“、“ROLE“、“ROLE_“、“roLe“等不区分大小写的字样开头", help = "全局唯一，用于标识角色")
    private String code;

    @JFXFormField(title = "等级", rowIndex = 2, columnIndex = 0, columnSpan = 2,
        controlFactory = SliderControlFactory.class, help = "数值越大，等级越高")
    private Integer level;

    @JFXFormField(title = "类型", rowIndex = 3, columnIndex = 0, columnSpan = 2,
        controlFactory = ComboBoxControlFactory.class,
        help = "普通用户（只能进入客户端）\n管理员（只能进入管理端）\n普通用户&管理员（客户端与管理端都可以进入）\n超级管理员（拥有所有权限）")
    private RoleType type;

    @JFXFormField(title = "状态", rowIndex = 4, columnIndex = 0, columnSpan = 2,
        controlFactory = RadioButtonControlFactory.class)
    private Status status;

    @JFXFormField(title = "描述", rowIndex = 5, columnIndex = 0, columnSpan = 2,
        controlFactory = TextAreaControlFactory.class)
    private String description;

    @JFXFormField(title = "备注", rowIndex = 6, columnIndex = 0, columnSpan = 2,
        controlFactory = TextAreaControlFactory.class)
    private String remark;
}
