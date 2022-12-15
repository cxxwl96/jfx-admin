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

package com.cxxwl96.jfx.admin.server.lang;

import java.io.Serializable;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import lombok.Data;

/**
 * 响应结果
 *
 * @author cxxwl96
 * @since 2021/6/18 23:41
 */
@Data
public class Result<T> implements Serializable {
    // 成功码
    public static final int CODE_SUCCESS = 200;

    // 失败码
    public static final int CODE_FAILED = 400;

    private int code;

    private String msg;

    private T data;

    public static <T> Result<T> formatFailed(String format, Object... args) {
        return Result.failed(String.format(format, args));
    }

    public static <T> Result<T> formatSuccess(String format, Object... args) {
        return Result.success(String.format(format, args));
    }

    public static <T> Result<T> success() {
        return success(200, "操作成功", null);
    }

    public static <T> Result<T> success(String msg) {
        return success(200, msg, null);
    }

    public static <T> Result<T> success(T data) {
        return success(200, "操作成功", data);
    }

    public static <T> Result<T> success(String msg, T data) {
        return success(200, msg, data);
    }

    public static <T> Result<T> success(int code, String msg, T data) {
        return newResult(code, msg, data);
    }

    public static <T> Result<T> failed() {
        return failed(400, "操作失败", null);
    }

    public static <T> Result<T> failed(String msg) {
        return failed(400, msg, null);
    }

    public static <T> Result<T> failed(int code, String msg, T data) {
        return newResult(code, msg, data);
    }

    private static <T> Result<T> newResult(int code, String msg, T data) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }

    /**
     * 是否成功
     *
     * @return 是否成功
     */
    public boolean isSuccess() {
        return code == CODE_SUCCESS;
    }

    /**
     * optional封装
     *
     * @return optional object
     */
    public Optional<T> optional() {
        if (code != CODE_SUCCESS) {
            return Optional.empty();
        }
        return Optional.ofNullable(data);
    }

    /**
     * 成功并执行
     *
     * @param consumer consumer
     */
    public Result<T> successAndThen(Consumer<Result<T>> consumer) {
        if (isSuccess()) {
            consumer.accept(this);
        }
        return this;
    }

    /**
     * 失败并执行
     *
     * @param consumer consumer
     */
    public Result<T> failedAndThen(BiConsumer<Integer, String> consumer) {
        if (!isSuccess()) {
            consumer.accept(code, msg);
        }
        return this;
    }

}
