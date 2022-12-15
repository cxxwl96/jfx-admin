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

import com.cxxwl96.jfx.admin.client.component.tabledata.AbstractTableCellFactory;
import com.cxxwl96.jfx.admin.client.component.tabledata.TableItem;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * JFXTableColumn
 *
 * @author cxxwl96
 * @since 2022/10/20 22:24
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JFXTableColumn {
    /**
     * 列标题
     *
     * @return 列标题
     */
    String title() default "";

    /**
     * 最小宽度
     *
     * @return 最小宽度
     */
    double min() default 0;

    /**
     * 显示宽度
     *
     * @return 显示宽度
     */
    double pref() default 120;

    /**
     * 最大宽度
     *
     * @return 最大宽度
     */
    double max() default Double.MAX_VALUE;

    /**
     * 是否排序
     *
     * @return 是否排序
     */
    boolean sortable() default true;

    /**
     * 字段名
     *
     * @return 字段名
     */
    String fieldName() default "";

    /**
     * 单元格工厂
     *
     * @return 单元格工厂
     */
    Class<? extends AbstractTableCellFactory<? extends TableItem, ?>>[] cellFactory() default {};

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
}
