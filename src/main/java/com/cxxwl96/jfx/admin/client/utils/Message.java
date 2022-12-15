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

import com.cxxwl96.jfx.admin.client.common.ApplicationStore;
import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXDialogLayout;

import org.kordamp.ikonli.javafx.FontIcon;

import java.util.ArrayList;
import java.util.Collection;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Window;
import lombok.Getter;

/**
 * Message
 *
 * @author cxxwl96
 * @since 2022/11/3 23:01
 */
public class Message {
    @Getter
    private final JFXAlert<String> alert;

    @Getter
    private final Collection<Node> actions = new ArrayList<>();

    public Message() {
        this(null);
    }

    public Message(Window window) {
        if (window == null) {
            window = ApplicationStore.getRootStage().orElseThrow(() -> new IllegalStateException("window is null"));
        }
        this.alert = new JFXAlert<>(window);
    }

    public JFXAlert<String> info(String content) {
        return info("提示", content);
    }

    public JFXAlert<String> info(String title, String content) {
        final FontIcon fontIcon = new FontIcon();
        fontIcon.setIconLiteral("fas-info-circle");
        fontIcon.setIconColor(Paint.valueOf("#40508B"));
        fontIcon.setIconSize(24);
        return message(title, content, fontIcon);
    }

    public JFXAlert<String> warn(String content) {
        return warn("提示", content);
    }

    public JFXAlert<String> warn(String title, String content) {
        final FontIcon fontIcon = new FontIcon();
        fontIcon.setIconLiteral("fas-question-circle");
        fontIcon.setIconColor(Paint.valueOf("orange"));
        fontIcon.setIconSize(24);
        return message(title, content, fontIcon);
    }

    public JFXAlert<String> error(String content) {
        return error("提示", content);
    }

    public JFXAlert<String> error(String title, String content) {
        final FontIcon fontIcon = new FontIcon();
        fontIcon.setIconLiteral("fas-exclamation-circle");
        fontIcon.setIconColor(Paint.valueOf("red"));
        fontIcon.setIconSize(24);
        return message(title, content, fontIcon);
    }

    public JFXAlert<String> message(String title, String content, FontIcon icon) {
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setOverlayClose(true);

        final Label titleLabel = new Label(title);
        titleLabel.setTextFill(Paint.valueOf("red"));
        titleLabel.setPadding(new Insets(0, 0, 0, 10));

        final HBox hBox = new HBox();
        hBox.getChildren().addAll(icon, titleLabel);

        JFXDialogLayout layout = new JFXDialogLayout();
        layout.setHeading(hBox);
        final Label labelContent = new Label(content);
        labelContent.setWrapText(true);
        final StackPane stackPane = new StackPane(labelContent);
        stackPane.setPadding(new Insets(10, 0, 10, 0));
        stackPane.setAlignment(Pos.TOP_LEFT);
        stackPane.getStyleClass().add("backgroundPane");
        final ScrollPane scrollPane = new ScrollPane(stackPane);
        scrollPane.setFitToWidth(true);
        layout.setBody(scrollPane);
        layout.setActions(actions.toArray(new Node[0]));
        alert.setContent(layout);
        return alert;
    }
}
