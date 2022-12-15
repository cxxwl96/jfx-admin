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

package com.cxxwl96.jfx.admin.server.service.impl;

import com.cxxwl96.jfx.admin.server.service.UserSecurity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import cn.hutool.crypto.digest.MD5;

/**
 * UserSecurityImpl
 *
 * @author cxxwl96
 * @since 2022/12/8 22:07
 */
@Service
public class UserSecurityImpl implements UserSecurity {
    @Value("${pw.salt}")
    private String pwSalt;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * 根据用户名，用户密码以及密码盐生成加密密码
     *
     * @param username 用户名
     * @param password 密码
     * @return 加密后的密码
     */
    @Override
    public String encodePassword(String username, String password) {
        return passwordEncoder.encode(upset(username, password));
    }

    /**
     * 已加密的密码与未加密的密码是否匹配
     *
     * @param username 用户名
     * @param password 密码
     * @param encodedPassword 已加密的密码
     * @return 是否能匹配
     */
    @Override
    public boolean matchesPassword(String username, String password, String encodedPassword) {
        final String text = upset(username, password);
        return passwordEncoder.matches(text, encodedPassword);
    }

    private String upset(String username, String password) {
        StringBuilder pwd = new StringBuilder();
        int maxLen = Math.max(username.length(), password.length());
        maxLen = Math.max(maxLen, pwSalt.length());
        for (int i = 0; i < maxLen; i++) {
            if (i < username.length()) {
                pwd.append(username.charAt(i));
            }
            if (i < password.length()) {
                pwd.append(password.charAt(i));
            }
            if (i < pwSalt.length()) {
                pwd.append(pwSalt.charAt(i));
            }
        }
        return MD5.create().digestHex(pwd.toString());
    }
}
