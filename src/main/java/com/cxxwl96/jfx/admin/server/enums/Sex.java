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

package com.cxxwl96.jfx.admin.server.enums;

import com.cxxwl96.jfx.admin.server.base.enums.IEnum;

import lombok.Getter;

/**
 * 性别【未设置：0,男：1,女：2】
 *
 * @author cxxwl96
 * @since 2021/8/29 21:13
 */
public enum Sex implements IEnum<Integer> {
    UNSET(0, "不设置"),
    MAN(1, "男"),
    WOMAN(2, "女");

    @Getter
    private final Integer value;

    @Getter
    private final String remark;

    Sex(int value, String remark) {
        this.value = value;
        this.remark = remark;
    }
}
