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

/**
 * 登录类型枚举
 * 账号是必须的
 *
 * @author cxxwl96
 * @since 2021/6/28 00:13
 */
public enum LoginTypeEnum {
    // 仅密码
    PASSWORD("仅密码"),
    // 仅短信验证码
    SMS_CODE("仅短信验证码"),
    // 仅邮箱验证码
    EMAIL_CODE("仅邮箱验证码"),
    // 密码和图片验证码
    PASSWORD_CAPTCHA("密码和图片验证码"),
    // 密码和短信验证码
    PASSWORD_SMS_CODE("密码和短信验证码"),
    // 密码和邮箱验证码
    PASSWORD_EMAIL_CODE("密码和邮箱验证码");

    private final String des;

    LoginTypeEnum(String des) {
        this.des = des;
    }

    public String getDes() {
        return des;
    }
}
