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
import com.cxxwl96.jfx.admin.server.enums.RoleType;
import com.cxxwl96.jfx.admin.server.enums.Status;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * SearchFormSchema
 *
 * @author cxxwl96
 * @since 2022/11/8 00:03
 */
@Data
@Accessors(chain = true)
public class SearchFormSchema {

    @JFXFormField(title = "名称")
    private String name;

    @JFXFormField(title = "编码")
    private String code;

    @JFXFormField(title = "类型", controlFactory = ComboBoxControlFactory.class)
    private RoleType type;

    @JFXFormField(title = "状态", controlFactory = ComboBoxControlFactory.class)
    private Status status;

}