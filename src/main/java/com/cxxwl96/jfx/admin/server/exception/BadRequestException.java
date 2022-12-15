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

package com.cxxwl96.jfx.admin.server.exception;

import com.cxxwl96.jfx.admin.server.lang.Result;

import java.util.Locale;

import lombok.Getter;

/**
 * 错误请求异常
 *
 * @author cxxwl96
 * @since 2021/6/27 16:27
 */
@Getter
public class BadRequestException extends RuntimeException {
    private final Result<?> result;

    public BadRequestException(Result<?> result) {
        super(result.getMsg());
        this.result = result;
    }

    public BadRequestException(String msg, Object... obj) {
        super(String.format(Locale.ROOT, msg, obj));
        final String format = String.format(Locale.ROOT, msg, obj);
        this.result = Result.failed(format);
    }
}
