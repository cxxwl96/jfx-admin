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

import com.cxxwl96.jfx.admin.server.base.enums.IEnum;
import com.jfoenix.controls.JFXRadioButton;

import java.lang.reflect.Field;

import cn.hutool.core.util.StrUtil;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import lombok.SneakyThrows;

/**
 * RadioButtonControlFactory
 *
 * @author cxxwl96
 * @since 2022/11/10 20:01
 */
public class RadioButtonControlFactory extends AbstractFormControlFactory {
    /**
     * 获取控件
     *
     * @param title 标题
     * @return 控件
     */
    @Override
    public Node getControl(String title) {
        final Field classField = super.getClassField();
        // 必须是继承与IEnum的枚举类才支持
        if (!super.getClassField().getType().isEnum() && IEnum.class.isAssignableFrom(
            super.getClassField().getType())) {
            final String message = classField.getType() + " must be an enumeration that inherits "
                + IEnum.class.getTypeName();
            throw new UnsupportedOperationException(message);
        }
        return enumInjectControl(classField.getType());
    }

    /**
     * 枚举转换控件
     *
     * @return 控件
     */
    @SneakyThrows
    private Node enumInjectControl(Class<?> enumClazz) {
        final Field[] fields = enumClazz.getFields();
        final HBox hBox = new HBox();
        final ToggleGroup group = new ToggleGroup();
        // 变更事件监听
        group.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                super.setValue(newValue.getUserData().toString());
            } else {
                super.setValue(null);
            }
        });
        super.valueProperty().addListener((observable, oldValue, newValue) -> {
            // 没有值默认选中第一个
            if (StrUtil.isBlank(newValue) && group.getToggles().size() > 0) {
                final Toggle toggle = group.getToggles().get(0);
                group.selectToggle(toggle);
            }
            group.getToggles()
                .stream()
                .filter(item -> item.getUserData().equals(newValue))
                .findFirst()
                .ifPresent(item -> item.setSelected(true));
        });
        for (Field field : fields) {
            Object enumObject = field.get(field.getName()); // 对应枚举实例
            final String remark = ((IEnum<?>) enumObject).getRemark();
            JFXRadioButton radio = new JFXRadioButton(remark);
            radio.setPadding(new Insets(10));
            radio.setToggleGroup(group);
            radio.setUserData(field.getName());
            // 默认选中第一个
            if (group.getSelectedToggle() == null) {
                group.selectToggle(radio);
            }
            hBox.getChildren().add(radio);
        }
        return hBox;
    }
}
