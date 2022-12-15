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

import com.cxxwl96.jfx.admin.client.component.form.AbstractFormControlFactory;
import com.cxxwl96.jfx.admin.client.component.form.TextFieldControlFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * JFXFormField
 *
 * @author cxxwl96
 * @since 2022/10/22 23:03
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JFXFormField {
    /**
     * 是否必填
     *
     * @return 是否必填
     */
    boolean required() default false;

    /**
     * 列标题
     *
     * @return 列标题
     */
    String title() default "";

    /**
     * 提示
     *
     * @return 提示
     */
    String help() default "";

    /**
     * 控件工厂
     *
     * @return 控件工厂
     */
    Class<? extends AbstractFormControlFactory> controlFactory() default TextFieldControlFactory.class;

    /**
     * 字段名
     *
     * @return 字段名
     */
    String name() default "";

    /**
     * 是否显示
     *
     * @return 是否显示
     */
    boolean show() default true;

    /**
     * 是否存在
     * true: 与实体数据绑定
     * false: 不与实体数据绑定
     *
     * @return 是否存在
     */
    boolean exists() default true;

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

    /**
     * 自定义校验正则
     *
     * @return 自定义校验正则
     */
    String pattern() default "";

    /**
     * 校验失败消息
     *
     * @return 校验失败消息
     */
    String message() default "";
}
