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

package com.cxxwl96.jfx.admin.server.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cxxwl96.jfx.admin.server.base.entity.BaseEntity;
import com.cxxwl96.jfx.admin.server.enums.RoleType;
import com.cxxwl96.jfx.admin.server.enums.Status;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 角色
 *
 * @author cxxwl96
 * @since 2021/8/22 13:33
 */

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
public class Role extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "角色名称不能为空")
    private String name;

    @NotBlank(message = "角色编码不能为空")
    @Pattern(regexp = "^(?![rR][oO][lL][eE]_?)[a-zA-Z0-9]{1,20}$",
        message = "角色编码仅数字或字母组成的20位以内字符串，且不能以“role“、“role_“、“ROLE“、“ROLE_“、“roLe“等不区分大小写的字样开头")
    private String code;

    private Integer level = 0;

    private RoleType type;

    private Status status = Status.ENABLE;

    private String description;
}
