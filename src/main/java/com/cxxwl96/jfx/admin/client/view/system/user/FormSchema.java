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

import com.cxxwl96.jfx.admin.client.annotation.JFXFormField;
import com.cxxwl96.jfx.admin.client.component.form.DatePickerControlFactory;
import com.cxxwl96.jfx.admin.client.component.form.NumberFieldControlFactory;
import com.cxxwl96.jfx.admin.client.component.form.PasswordControlFactory;
import com.cxxwl96.jfx.admin.client.component.form.RadioButtonControlFactory;
import com.cxxwl96.jfx.admin.client.component.form.TextAreaControlFactory;
import com.cxxwl96.jfx.admin.server.enums.Sex;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * FormSchema
 *
 * @author cxxwl96
 * @since 2022/10/28 22:51
 */
@Data
@Accessors(chain = true)
public class FormSchema {
    @JFXFormField(title = "id", show = false)
    private String id;

    @JFXFormField(title = "账户", rowIndex = 0, columnIndex = 0, required = true, pattern = "^[a-zA-Z0-9]{2,16}$",
        message = "账户应为字母或数字2-16个字符")
    private String username;

    @JFXFormField(title = "密码", rowIndex = 0, columnIndex = 1, controlFactory = PasswordControlFactory.class)
    private String password;

    @JFXFormField(title = "姓名", rowIndex = 1, columnIndex = 0)
    private String name;

    @JFXFormField(title = "性别", rowIndex = 1, columnIndex = 1, controlFactory = RadioButtonControlFactory.class)
    private Sex sex = Sex.UNSET;

    @JFXFormField(title = "邮箱", rowIndex = 2, columnIndex = 0)
    private String email;

    @JFXFormField(title = "电话", rowIndex = 2, columnIndex = 1)
    private String phone;

    @JFXFormField(title = "年龄", rowIndex = 3, columnIndex = 0, controlFactory = NumberFieldControlFactory.class)
    private Integer age;

    @JFXFormField(title = "生日", rowIndex = 3, columnIndex = 1, controlFactory = DatePickerControlFactory.class)
    private LocalDateTime birthday;

    @JFXFormField(title = "备注", rowIndex = 4, columnIndex = 0, columnSpan = 2,
        controlFactory = TextAreaControlFactory.class)
    private String remark;
}
