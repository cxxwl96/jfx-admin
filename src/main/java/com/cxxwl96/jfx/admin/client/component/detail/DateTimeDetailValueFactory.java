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

import java.time.LocalDateTime;

import cn.hutool.core.date.LocalDateTimeUtil;
import javafx.scene.Node;
import javafx.scene.control.Label;
import lombok.extern.slf4j.Slf4j;

/**
 * DateTimeDetailValueFactory
 *
 * @author cxxwl96
 * @since 2022/11/8 21:10
 */
@Slf4j
public class DateTimeDetailValueFactory extends DetailValueFactory {
    @Override
    public Node getValue(Object data, String fieldName) {
        final String value = getStringValue(data, fieldName);
        try {
            return new Label(LocalDateTimeUtil.format(LocalDateTime.parse(value), "yyyy-MM-dd HH:mm:ss.SSS"));
        } catch (Exception exception) {
            return new Label();
        }
    }
}
