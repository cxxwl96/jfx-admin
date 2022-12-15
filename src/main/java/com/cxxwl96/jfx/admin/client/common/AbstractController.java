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

package com.cxxwl96.jfx.admin.client.common;

import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * AbstractBaseController
 *
 * @author cxxwl96
 * @since 2022/9/19 20:35
 */
@Slf4j
public abstract class AbstractController implements IController {
    private final List<Field> beanFields = new ArrayList<>();

    @Override
    public void initialize() {
        // 扫描并注入被@Autowired注解的字段
        scanAndInjectBeans();
    }

    private void scanAndInjectBeans() {
        final Field[] fields = this.getClass().getDeclaredFields();
        Arrays.stream(fields).forEach(field -> {
            if (!field.isAnnotationPresent(Autowired.class)) {
                return;
            }
            try {
                final Class<?> clazz = field.getType();
                final boolean accessible = field.isAccessible();
                field.setAccessible(true);
                field.set(this, SpringUtil.getBean(clazz));
                field.setAccessible(accessible);
            } catch (IllegalAccessException exception) {
                log.error(exception.getMessage(), exception);
            }
        });
    }
}
