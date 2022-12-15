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

package com.cxxwl96.jfx.admin.server.function;

import com.cxxwl96.jfx.admin.server.utils.NamingUtil;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.function.Function;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ReflectUtil;
import lombok.SneakyThrows;

/**
 * lambda方法引用获取字段属性
 *
 * @author cxxwl96
 * @since 2021/8/22 16:23
 */
public interface PropertyFunc<T, R> extends Function<T, R>, Serializable {
    @SneakyThrows
    default String getFieldName() {
        Method method = ReflectUtil.getMethodByName(this.getClass(), "writeReplace");
        method.setAccessible(true);
        SerializedLambda serializedLambda = (SerializedLambda) method.invoke(this);
        String methodName = serializedLambda.getImplMethodName();
        if (methodName.startsWith("get")) {
            methodName = methodName.substring(3);
        } else if (methodName.startsWith("is")) {
            methodName = methodName.substring(2);
        }
        // 首字母变小写
        return CharSequenceUtil.lowerFirst(methodName);
    }

    @SneakyThrows
    default String getColumnName() {
        return NamingUtil.toLowerCaseUnderline(getFieldName());
    }
}
