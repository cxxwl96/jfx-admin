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

package com.cxxwl96.jfx.admin.server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

/**
 * AppConfig
 *
 * @author cxxwl96
 * @since 2022/12/1 00:49
 */
@Getter
@Configuration
public class AppConfig {
    // 应用logo
    @Value("${app.info.appLogo}")
    private String appLogo;

    // 应用名
    @Value("${app.info.appName}")
    private String appName;

    // 应用描述
    @Value("${app.info.appDescription}")
    private String appDescription;

    // 版本
    @Value("${app.info.version}")
    private String version;

    // 链接
    @Value("${app.info.url}")
    private String url;

    // 邮箱
    @Value("${app.info.email}")
    private String email;

    // FontIcon前缀
    @Value("${app.setting.fontIconPrefix}")
    private String fontIconPrefix;
}
