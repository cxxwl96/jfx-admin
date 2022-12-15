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

import com.jfoenix.controls.JFXSlider;

import cn.hutool.core.util.NumberUtil;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 * SliderControlFactory
 *
 * @author cxxwl96
 * @since 2022/11/27 13:30
 */
public class SliderControlFactory extends AbstractFormControlFactory {
    /**
     * 获取控件
     *
     * @param title 标题
     * @return 控件
     */
    @Override
    public Node getControl(String title) {
        final JFXSlider slider = new JFXSlider();
        slider.setBlockIncrement(1);
        slider.valueProperty()
            .addListener((observable, oldValue, newValue) -> value.set(String.valueOf(newValue.intValue())));
        value.addListener((observable, oldValue, newValue) -> {
            if (NumberUtil.isNumber(newValue)) {
                slider.setValue(Double.parseDouble(newValue));
            }
        });
        slider.setValue(0);
        HBox.setHgrow(slider, Priority.ALWAYS);
        final Label valueLabel = new Label();
        valueLabel.setText(String.valueOf(((int) slider.getValue())));
        valueLabel.textProperty().bind(value);
        return new HBox(slider, valueLabel);
    }
}
