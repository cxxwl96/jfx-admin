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

import com.cxxwl96.jfx.admin.server.enums.Condition;
import com.cxxwl96.jfx.admin.server.function.PropertyFunc;
import com.cxxwl96.jfx.admin.server.utils.NamingUtil;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 过滤条件
 *
 * @author cxxwl96
 * @since 2021/8/22 16:18
 */

@Data
@Accessors(chain = true)
public class WhereItem {

    private WhereOperator whereOperator = WhereOperator.AND;

    private String column;

    private Condition condition;

    private Object value;

    public static enum WhereOperator {
        OR,
        AND
    }

    /**
     * 生成where条件实例
     *
     * @param column 字段
     * @param condition 条件
     * @param value 字段值
     * @param <T> 类型
     * @return 条件实例
     */
    public static <T> WhereItem whereItem(PropertyFunc<T, ?> column, Condition condition, Object value) {
        final WhereItem whereItem = new WhereItem();
        // 转数据库字段
        whereItem.column = NamingUtil.toLowerCaseUnderline(column.getFieldName());
        whereItem.condition = condition;
        whereItem.value = value;
        return whereItem;
    }
}
