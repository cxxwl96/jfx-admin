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

package com.cxxwl96.jfx.admin.client.utils;

import com.cxxwl96.jfx.admin.client.common.ApplicationConfig;
import com.jfoenix.svg.SVGGlyph;
import com.jfoenix.svg.SVGGlyphLoader;

import org.kordamp.ikonli.javafx.FontIcon;

import cn.hutool.core.util.StrUtil;
import javafx.scene.Node;
import lombok.extern.slf4j.Slf4j;

/**
 * Icon
 *
 * @author cxxwl96
 * @since 2022/10/2 12:07
 */
@Slf4j
public class IconUtil extends SVGGlyph {
    public static Node getIcon(String icon) {
        if (StrUtil.isBlank(icon)) {
            return null;
        }
        try {
            if (icon.startsWith("fas:")) {
                final String name = icon.substring("fas:".length());
                final FontIcon fontIcon = new FontIcon();
                fontIcon.setIconLiteral(name);
                return fontIcon;
            } else {
                try {
                    return SVGGlyphLoader.getIcoMoonGlyph(ApplicationConfig.getFontIconPrefix() + "." + icon);
                } catch (Exception exception) {
                    return null;
                }
            }
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
        }
        return null;
    }
}
