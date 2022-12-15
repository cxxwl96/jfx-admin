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

import com.jfoenix.controls.JFXButton;

import javafx.stage.Window;
import lombok.extern.slf4j.Slf4j;

/**
 * Alert
 *
 * @author cxxwl96
 * @since 2022/9/24 12:40
 */
@Slf4j
public class Alert {
    public static Message info(String content) {
        return info("提示", content);
    }

    public static Message info(String title, String content) {
        return info(title, content, null);
    }

    public static Message info(String title, String content, Window window) {
        final Message message = alert(window);
        message.info(title, content).show();
        return message;
    }

    public static Message warn(String content) {
        return warn("提示", content);
    }

    public static Message warn(String title, String content) {
        return warn(title, content, null);
    }

    public static Message warn(String title, String content, Window window) {
        final Message message = alert(window);
        message.warn(title, content).show();
        return message;
    }

    public static Message error(String content) {
        return error("提示", content);
    }

    public static Message error(String title, String content) {
        return error(title, content, null);
    }

    public static Message error(String title, String content, Window window) {
        final Message message = alert(window);
        message.error(title, content).show();
        return message;
    }

    public static Message alert(Window window) {
        final Message message = new Message(window);
        // 创建关闭按钮
        JFXButton closeButton = new JFXButton("OK");
        closeButton.setPrefWidth(100);
        closeButton.setOnAction(event -> message.getAlert().hideWithAnimation());
        // 设置动作信息
        message.getActions().add(closeButton);
        return message;
    }
}
