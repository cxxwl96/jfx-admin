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

package com.cxxwl96.jfx.admin.server.utils;

import com.cxxwl96.jfx.admin.server.exception.BadRequestException;
import com.cxxwl96.jfx.admin.server.lang.Result;

import org.springframework.validation.BindingResult;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

/**
 * 实体校验工具类
 *
 * @author cxxwl96
 * @since 2021/8/9 23:54
 */
public class ValidatorUtil {
    private static final Validator validator;

    static {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    /**
     * 校验对象
     *
     * @param object 待校验对象
     * @param groups 待校验的组
     */
    public static void validateEntity(Object object, Class<?>... groups) {
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(object, groups);
        if (!constraintViolations.isEmpty()) {
            constraintViolations.forEach(violation -> {
                throw new BadRequestException(Result.failed(violation.getMessage()));
            });
        }
    }

    /**
     * 校验结果
     *
     * @param bindingResult 校验结果
     */
    public static void validateEntity(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(validator -> {
                throw new BadRequestException(Result.failed(validator.getDefaultMessage()));
            });
        }
    }
}
