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
import com.jfoenix.controls.JFXComboBox;

import java.lang.reflect.Field;
import java.util.Optional;

import javafx.scene.Node;
import javafx.scene.control.Label;
import lombok.SneakyThrows;

/**
 * ComboBoxControlFactory
 *
 * @author cxxwl96
 * @since 2022/11/10 23:18
 */
public class ComboBoxControlFactory extends AbstractFormControlFactory {
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
        return enumInjectControl(classField.getType(), title);
    }

    /**
     * 枚举转换控件
     *
     * @return 控件
     */
    @SneakyThrows
    private JFXComboBox<Label> enumInjectControl(Class<?> enumClazz, String title) {
        final Field[] fields = enumClazz.getFields();
        final JFXComboBox<Label> comboBox = new JFXComboBox<>();
        comboBox.setPromptText("请选择" + title);
        // 变更事件监听
        comboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                super.setValue(newValue.getUserData().toString());
            } else {
                super.setValue(null);
            }
        });
        super.valueProperty().addListener((observable, oldValue, newValue) -> {
            final Optional<Label> optional = comboBox.getItems()
                .stream()
                .filter(item -> item.getUserData().equals(newValue))
                .findFirst();
            if (optional.isPresent()) {
                comboBox.getSelectionModel().select(optional.get());
            } else {
                comboBox.getSelectionModel().clearSelection();
            }
        });
        for (Field field : fields) {
            Object enumObject = field.get(field.getName()); // 对应枚举实例
            final String remark = ((IEnum<?>) enumObject).getRemark();
            final Label item = new Label(remark);
            item.setUserData(field.getName());
            comboBox.getItems().add(item);
        }
        return comboBox;
    }
}
