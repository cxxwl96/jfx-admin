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

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * 图片验证码配置类
 *
 * @author cxxwl96
 * @since 2021/6/20 11:51
 */
@Configuration
public class KaptchaConfig {
    @Bean
    DefaultKaptcha producer() {
        Properties properties = new Properties();
        properties.put("kaptcha.border", "no");
        properties.put("kaptcha.textproducer.font.color", "55,76,230");
        properties.put("kaptcha.textproducer.char.space", "4");
        properties.put("kaptcha.textproducer.font.size", "30");
        properties.put("kaptcha.image.height", "45");
        properties.put("kaptcha.image.width", "163");
        properties.put("kaptcha.noise.color", "55,76,230"); // 干扰颜色
        properties.put("kaptcha.background.clear.from", "156,167,241"); // 背景颜色渐变，开始颜色
        properties.put("kaptcha.background.clear.to", "white"); // 背景颜色渐变，结束颜色

        Config config = new Config(properties);
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        defaultKaptcha.setConfig(config);

        return defaultKaptcha;
    }
}
