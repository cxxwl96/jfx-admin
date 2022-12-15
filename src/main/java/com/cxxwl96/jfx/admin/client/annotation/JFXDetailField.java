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

package com.cxxwl96.jfx.admin.client.annotation;

import com.cxxwl96.jfx.admin.client.component.detail.DetailValueFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * JFXDetailField
 *
 * @author cxxwl96
 * @since 2022/11/5 01:46
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JFXDetailField {
    /**
     * 列标题
     *
     * @return 列标题
     */
    String title() default "";

    /**
     * 控件类型
     *
     * @return 控件类型
     */
    Class<? extends DetailValueFactory> valueFactory() default DetailValueFactory.class;

    /**
     * 字段名
     *
     * @return 字段名
     */
    String name() default "";

    /**
     * JFXFormField表单中的第几行
     *
     * @return row
     */
    int rowIndex() default 0;

    /**
     * JFXFormField表单中的第几列
     *
     * @return column
     */
    int columnIndex() default 0;

    /**
     * JFXFormField表单中的跨行
     *
     * @return rowSpan
     */
    int rowSpan() default 1;

    /**
     * JFXFormField表单中的跨列
     *
     * @return columnSpan
     */
    int columnSpan() default 1;
}
