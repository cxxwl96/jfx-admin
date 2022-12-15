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

package com.cxxwl96.jfx.admin.client.component.detail;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cxxwl96.jfx.admin.server.base.enums.IEnum;
import com.cxxwl96.jfx.admin.server.enums.Status;

import java.lang.reflect.Field;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import cn.hutool.core.util.ReflectUtil;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import lombok.extern.slf4j.Slf4j;

/**
 * DetailValueFactory
 *
 * @author cxxwl96
 * @since 2022/11/6 00:53
 */
@Slf4j
public class DetailValueFactory {
    public Node getValue(@NotNull Object data, @NotBlank String fieldName) {
        String value = getStringValue(data, fieldName);
        try {
            final Field fieldNameField = ReflectUtil.getField(data.getClass(), fieldName);
            // 是否枚举
            if (fieldNameField.getType().isEnum() && IEnum.class.isAssignableFrom(fieldNameField.getType())) {
                Object enumObject = ReflectUtil.getFieldValue(fieldNameField.getType(), value);
                value = ((IEnum<?>) enumObject).getRemark();
                // 是否状态枚举
                if (fieldNameField.getType() == Status.class) {
                    final Status status = (Status) ReflectUtil.getFieldValue(data, fieldNameField);
                    return createStatusNode(status);
                }
            }
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
            return new Label(value);
        }
        return new Label(value);
    }

    protected String getStringValue(@NotNull Object data, @NotBlank String fieldName) {
        JSONObject object = JSON.parseObject(JSON.toJSONString(data));
        return object.getString(fieldName);
    }

    private Node createStatusNode(Status status) {
        final Circle circle = new Circle(5);
        circle.setStrokeWidth(0);
        if (status == Status.ENABLE) {
            circle.setFill(Paint.valueOf("#87d068")); // 绿色
        } else {
            circle.setFill(Paint.valueOf("#f50f50")); // 红色
        }
        final Label label = new Label(status.getRemark(), circle);
        return new HBox(label);
    }

}
