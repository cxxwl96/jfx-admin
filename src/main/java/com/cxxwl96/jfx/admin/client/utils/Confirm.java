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

import java.util.function.Consumer;

import javafx.event.ActionEvent;
import javafx.stage.Window;

/**
 * Confirm
 *
 * @author cxxwl96
 * @since 2022/11/3 23:13
 */
public class Confirm {
    private Consumer<ActionEvent> okHandler;

    private Consumer<ActionEvent> cancelHandler;

    /**
     * 确认前置处理
     *
     * @param confirmHandler 确认前置处理
     * @return this
     */
    public Confirm okAction(Consumer<ActionEvent> confirmHandler) {
        this.okHandler = confirmHandler;
        return this;
    }

    /**
     * 取消前置处理
     *
     * @param cancelHandler 取消前置处理
     * @return this
     */
    public Confirm cancelAction(Consumer<ActionEvent> cancelHandler) {
        this.cancelHandler = cancelHandler;
        return this;
    }

    public static Confirm buildConfirm() {
        return new Confirm();
    }

    public Message info(String content) {
        return info("提示", content);
    }

    public Message info(String title, String content) {
        return info(title, content, null);
    }

    public Message info(String title, String content, Window window) {
        final Message message = confirm(window);
        message.info(title, content).show();
        return message;
    }

    public Message warn(String content) {
        return warn("提示", content);
    }

    public Message warn(String title, String content) {
        return warn(title, content, null);
    }

    public Message warn(String title, String content, Window window) {
        final Message message = confirm(window);
        message.warn(title, content).show();
        return message;
    }

    public Message error(String content) {
        return error("提示", content);
    }

    public Message error(String title, String content) {
        return error(title, content, null);
    }

    public Message error(String title, String content, Window window) {
        final Message message = confirm(window);
        message.error(title, content).show();
        return message;
    }

    public Message confirm(Window window) {
        final Message message = new Message(window);
        // 创建确认按钮
        JFXButton confirmButton = new JFXButton("确认");
        confirmButton.setPrefWidth(100);
        confirmButton.setOnAction(event -> {
            if (okHandler != null) {
                okHandler.accept(event);
            }
            if (event.isConsumed()) {
                return;
            }
            message.getAlert().hideWithAnimation();
        });
        // 创建取消按钮
        JFXButton cancelButton = new JFXButton("取消");
        cancelButton.getStyleClass().add("jfx-button-flat");
        cancelButton.setPrefWidth(100);
        cancelButton.setOnAction(event -> {
            if (cancelHandler != null) {
                cancelHandler.accept(event);
            }
            if (event.isConsumed()) {
                return;
            }
            message.getAlert().hideWithAnimation();
        });
        // 设置动作信息
        message.getActions().add(confirmButton);
        message.getActions().add(cancelButton);
        return message;
    }
}
