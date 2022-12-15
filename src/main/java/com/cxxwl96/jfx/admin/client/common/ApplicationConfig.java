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

package com.cxxwl96.jfx.admin.client.common;

import com.cxxwl96.jfx.admin.server.config.AppConfig;
import com.cxxwl96.jfx.admin.server.function.PropertyFunc;
import com.cxxwl96.jfx.admin.server.utils.YamlUtil;

import org.springframework.beans.factory.annotation.Value;

import java.lang.reflect.Field;
import java.util.Optional;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * ApplicationConfig
 *
 * @author cxxwl96
 * @since 2022/12/2 20:59
 */
@Slf4j
public class ApplicationConfig {
    /**
     * 应用logo
     *
     * @return 应用logo
     */
    public static String getAppLogo() {
        return getAppConfig().map(AppConfig::getAppLogo).orElse(getFromYaml(AppConfig::getAppLogo));
    }

    /**
     * 应用名
     *
     * @return 应用名
     */
    public static String getAppName() {
        return getAppConfig().map(AppConfig::getAppName).orElse(getFromYaml(AppConfig::getAppName));
    }

    /**
     * 应用描述
     *
     * @return 应用描述
     */
    public static String getAppDescription() {
        return getAppConfig().map(AppConfig::getAppDescription).orElse(getFromYaml(AppConfig::getAppDescription));
    }

    /**
     * 版本
     *
     * @return 版本
     */
    public static String getVersion() {
        return getAppConfig().map(AppConfig::getVersion).orElse(getFromYaml(AppConfig::getVersion));
    }

    /**
     * 链接
     *
     * @return 链接
     */
    public static String getUrl() {
        return getAppConfig().map(AppConfig::getUrl).orElse(getFromYaml(AppConfig::getUrl));
    }

    /**
     * 邮箱
     *
     * @return 邮箱
     */
    public static String getEmail() {
        return getAppConfig().map(AppConfig::getEmail).orElse(getFromYaml(AppConfig::getEmail));
    }

    /**
     * FontIcon前缀
     *
     * @return FontIcon前缀
     */
    public static String getFontIconPrefix() {
        return getAppConfig().map(AppConfig::getFontIconPrefix).orElse(getFromYaml(AppConfig::getFontIconPrefix));
    }

    private static Optional<AppConfig> getAppConfig() {
        try {
            final AppConfig appConfig = SpringUtil.getBean(AppConfig.class);
            return Optional.ofNullable(appConfig);
        } catch (Exception exception) {
            return Optional.empty();
        }
    }

    private static String getFromYaml(PropertyFunc<AppConfig, String> func) {
        final Field field = ReflectUtil.getField(AppConfig.class, func.getFieldName());
        if (field == null) {
            return StrUtil.EMPTY;
        }
        if (field.isAnnotationPresent(Value.class)) {
            final Value annotation = field.getAnnotation(Value.class);
            final String value = annotation.value();
            if (StrUtil.isBlank(value) || !value.matches("^\\$\\{.+}$")) {
                return StrUtil.EMPTY;
            }
            // 得到AppConfig字段配置的yaml路径
            final String path = value.substring(2, value.length() - 1);
            return YamlUtil.get(path, String.class);
        }
        return StrUtil.EMPTY;
    }
}
