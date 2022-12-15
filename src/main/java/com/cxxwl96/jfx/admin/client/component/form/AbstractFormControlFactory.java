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

import java.lang.reflect.Field;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Node;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * 表单控件接口
 *
 * @author cxxwl96
 * @since 2022/11/10 19:28
 */
@EqualsAndHashCode
public abstract class AbstractFormControlFactory {
    /**
     * 字段值
     */
    protected SimpleStringProperty value = new SimpleStringProperty();

    /**
     * 约束类
     */
    @Getter
    @Setter
    protected Class<?> schemaClass;

    /**
     * 约束类字段
     */
    @Getter
    @Setter
    protected Field classField;

    /**
     * 获取控件
     *
     * @param title 标题
     * @return 控件
     */
    public abstract Node getControl(String title);

    /**
     * 获取字段值
     *
     * @return 字段值
     */
    public String getValue() {
        return value.get();
    }

    /**
     * 字段值属性
     *
     * @return 字段值属性
     */
    public SimpleStringProperty valueProperty() {
        return value;
    }

    /**
     * 设置字段值
     *
     * @param value 字段值
     */
    public void setValue(String value) {
        this.value.set(value);
    }
}
