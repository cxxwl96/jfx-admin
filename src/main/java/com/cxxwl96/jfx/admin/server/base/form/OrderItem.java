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

package com.cxxwl96.jfx.admin.server.base.form;

import com.cxxwl96.jfx.admin.server.function.PropertyFunc;
import com.cxxwl96.jfx.admin.server.utils.NamingUtil;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 排序条件
 *
 * @author cxxwl96
 * @since 2021/8/22 16:18
 */

@Data
@Accessors(chain = true)
public class OrderItem {

    private String column;

    private boolean asc;

    /**
     * 生成order实例
     *
     * @param column 字段
     * @param asc 升序？
     * @param <T> 类型
     * @return 条件实例
     */
    public <T> OrderItem orderItem(PropertyFunc<T, ?> column, boolean asc) {
        // 转数据库字段
        this.column = NamingUtil.toLowerCaseUnderline(column.getFieldName());
        this.asc = asc;
        return this;
    }

    public String getColumn() {
        return column;
    }
}
