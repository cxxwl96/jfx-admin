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

package com.cxxwl96.jfx.admin.client.view.setting.user;

import com.cxxwl96.jfx.admin.client.annotation.JFXFormField;
import com.cxxwl96.jfx.admin.client.component.form.DatePickerControlFactory;
import com.cxxwl96.jfx.admin.client.component.form.NumberFieldControlFactory;
import com.cxxwl96.jfx.admin.client.component.form.RadioButtonControlFactory;
import com.cxxwl96.jfx.admin.server.enums.Sex;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * BaseFormSchema
 *
 * @author cxxwl96
 * @since 2022/11/26 23:28
 */
@Data
@Accessors(chain = true)
public class BaseFormSchema {
    @JFXFormField(title = "id", show = false)
    private String id;

    @JFXFormField(title = "姓名", rowIndex = 0, columnIndex = 0)
    private String name;

    @JFXFormField(title = "性别", rowIndex = 1, columnIndex = 0, controlFactory = RadioButtonControlFactory.class)
    private Sex sex = Sex.UNSET;

    @JFXFormField(title = "邮箱", rowIndex = 2, columnIndex = 0)
    private String email;

    @JFXFormField(title = "电话", rowIndex = 3, columnIndex = 0)
    private String phone;

    @JFXFormField(title = "年龄", rowIndex = 4, columnIndex = 0, controlFactory = NumberFieldControlFactory.class)
    private Integer age;

    @JFXFormField(title = "生日", rowIndex = 5, columnIndex = 0, controlFactory = DatePickerControlFactory.class)
    private LocalDateTime birthday;
}
