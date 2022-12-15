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

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;

/**
 * Browser
 *
 * @author cxxwl96
 * @since 2022/10/2 14:34
 */
public class Browser {
    /**
     * 浏览器打开链接
     *
     * @param url URL
     */
    public static void openHttpInBrowser(String url) {
        try {
            URI uri = URI.create(url);
            // 获取当前系统桌面扩展
            Desktop dp = Desktop.getDesktop();
            // 判断系统桌面是否支持要执行的功能
            if (dp.isSupported(java.awt.Desktop.Action.BROWSE)) {
                dp.browse(uri); // 获取系统默认浏览器打开链接
            }
        } catch (IOException exception) {
            Alert.error("链接" + url + "打不开", exception.getMessage());
        }
    }
}
