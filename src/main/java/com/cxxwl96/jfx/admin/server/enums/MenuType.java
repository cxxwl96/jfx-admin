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
 * 菜单类型 [0：目录 1：菜单 2：按钮]
 *
 * @author cxxwl96
 * @since 2021/7/20 23:49
 */
public enum MenuType implements IEnum<Integer> {
    // 目录
    DIRECTORY(0, "目录"),
    // 菜单
    MENU(1, "菜单"),
    // 按钮
    BUTTON(2, "资源按钮/资源权限");

    @Getter
    private final Integer value;

    @Getter
    private final String remark;

    MenuType(Integer value, String remark) {
        this.value = value;
        this.remark = remark;
    }
}
