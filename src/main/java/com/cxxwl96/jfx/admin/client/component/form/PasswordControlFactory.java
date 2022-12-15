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

package com.cxxwl96.jfx.admin.client.component.form;

import com.jfoenix.controls.JFXPasswordField;

import javafx.scene.Node;

/**
 * PasswordControlFactory
 *
 * @author cxxwl96
 * @since 2022/11/10 19:26
 */
public class PasswordControlFactory extends AbstractFormControlFactory {
    /**
     * 获取控件
     *
     * @param title 标题
     * @return 控件
     */
    @Override
    public Node getControl(String title) {
        final JFXPasswordField field = new JFXPasswordField();
        field.setPromptText("请输入" + title);
        field.textProperty().bindBidirectional(value);
        return field;
    }
}
