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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONPath;

import org.jasypt.util.text.BasicTextEncryptor;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;

import cn.hutool.core.util.StrUtil;

/**
 * YamlUtil
 *
 * @author cxxwl96
 * @since 2022/12/2 20:25
 */
public class YamlUtil {
    private static String json;

    static {
        final InputStream resource = YamlUtil.class.getClassLoader().getResourceAsStream("application.yml");
        if (resource != null) {
            final Yaml yaml = new Yaml();
            final Object obj = yaml.load(resource);
            json = JSON.toJSONString(obj);
        }
    }

    /**
     * 获取配置数据
     *
     * @param path path
     * @param clazz 值类型
     * @param <T> 数据类型
     * @return 配置数据
     */
    public static <T> T get(String path, Class<T> clazz) {
        if (StrUtil.isBlank(json)) {
            return null;
        }
        return JSONPath.read(json, path, clazz);
    }

    /**
     * 加密配置数据
     *
     * @param salt 盐
     * @param text 需要加密的文本
     * @return 加密后的文本
     */
    public static String encrypt(String salt, String text) {
        BasicTextEncryptor encryptor = new BasicTextEncryptor();
        encryptor.setPassword(salt);
        return encryptor.encrypt(text);
    }

    /**
     * 解密配置数据
     *
     * @param salt 盐
     * @param encryptedText 已加密的文本
     * @return 解密后的文本
     */
    public static String decrypt(String salt, String encryptedText) {
        BasicTextEncryptor encryptor = new BasicTextEncryptor();
        encryptor.setPassword(salt);
        return encryptor.decrypt(encryptedText);
    }
}
