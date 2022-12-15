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

import java.util.Optional;

import lombok.Getter;

/**
 * 角色类型[0：普通用户（只能进入客户端） 1：管理员（只能进入管理端） 2：普通用户&管理员（客户端与管理端都可以进入） 3：超级管理员（拥有所有权限）]
 *
 * @author cxxwl96
 * @since 2022/9/24 10:22
 */
public enum RoleType implements IEnum<Integer> {
    // 普通用户（只能进入客户端）
    GENERAL(0, "普通用户"),
    // 管理员（只能进入管理端）
    ADMIN(1, "管理员"),
    // 普通用户&管理员（客户端与管理端都可以进入）
    GENERAL_ADMIN(2, "普通用户&管理员"),
    // 超级管理员（拥有所有权限）
    ADMINISTRATOR(3, "超级管理员");

    @Getter
    private final Integer value;

    @Getter
    private final String remark;

    RoleType(int value, String remark) {
        this.value = value;
        this.remark = remark;
    }

    public static Optional<RoleType> valueOf(Integer value) {
        for (RoleType roleType : RoleType.values()) {
            if (roleType.getValue().equals(value)) {
                return Optional.of(roleType);
            }
        }
        return Optional.empty();
    }
}
