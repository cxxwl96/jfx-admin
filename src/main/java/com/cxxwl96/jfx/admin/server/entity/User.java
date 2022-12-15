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

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cxxwl96.jfx.admin.server.base.entity.BaseEntity;
import com.cxxwl96.jfx.admin.server.enums.Sex;
import com.cxxwl96.jfx.admin.server.valid.AddValidGroup;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 系统用户
 *
 * @author cxxwl96
 * @since 2021-06-29
 */

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_user")
public class User extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "账号不能为空")
    @Pattern(message = "账号只能由16位 A-Z a-z 0-9 @ . _ 组成", regexp = "^[A-Za-z0-9@._]{1,16}$")
    private String username;

    @NotBlank(message = "密码不能为空", groups = {AddValidGroup.class})
    @Pattern(message = "密码只能由16位 A-Z a-z 0-9 @ . _ 组成", regexp = "^[A-Za-z0-9@._]{0,16}$",
        groups = {AddValidGroup.class})
    private String password;

    private String name;

    private Sex sex = Sex.UNSET;

    private Integer age;

    private LocalDateTime birthday;

    @Pattern(message = "邮箱格式不正确", regexp = "^(\\w+@(\\w+\\.)+(\\w+))?$")
    private String email;

    @Pattern(message = "手机号码格式不正确", regexp = "^(1[345789]\\d{9})?$")
    private String phone;

    private String avatar;

    private String city;

    private LocalDateTime lastLogin;

    private String deptId;

    @TableField(exist = false)
    private Dept dept;

    @TableField(exist = false)
    private List<Role> roles;
}
