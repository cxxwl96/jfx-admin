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

import com.jfoenix.controls.JFXDatePicker;

import javafx.scene.Node;

/**
 * DatePickerControlFactory
 *
 * @author cxxwl96
 * @since 2022/11/10 20:19
 */
public class DatePickerControlFactory extends AbstractFormControlFactory {
    /**
     * 获取控件
     *
     * @param title 标题
     * @return 控件
     */
    @Override
    public Node getControl(String title) {
        final JFXDatePicker picker = new JFXDatePicker();
        picker.setPromptText("请输入" + title);
        picker.valueProperty().addListener((observable, oldValue, newValue) -> value.set(newValue.toString()));
        return picker;
    }
}
