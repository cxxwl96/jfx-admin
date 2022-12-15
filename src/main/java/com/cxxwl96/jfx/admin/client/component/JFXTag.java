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

package com.cxxwl96.jfx.admin.client.component;

import java.util.Arrays;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import lombok.Getter;
import lombok.Setter;

/**
 * JFXTag
 *
 * @author cxxwl96
 * @since 2022/10/21 21:08
 */
public class JFXTag extends HBox {
    private final static String STYLE_CLASS = "jfx-tag";

    private final SimpleObjectProperty<JFXTagType> type = new SimpleObjectProperty<>();

    private final SimpleStringProperty text = new SimpleStringProperty();

    private final SimpleObjectProperty<Node> graphic = new SimpleObjectProperty<>();

    private final SimpleBooleanProperty closeable = new SimpleBooleanProperty();

    @Setter
    private EventHandler<Event> onClosed;

    public enum JFXTagType {
        PRIMARY("jfx-tag-primary"),
        SUCCESS("jfx-tag-success"),
        WARNING("jfx-tag-warning"),
        DANGER("jfx-tag-danger");

        @Getter
        private final String style;

        JFXTagType(String style) {
            this.style = style;
        }
    }

    public JFXTag() {
        this(null);
    }

    public JFXTag(String text) {
        this(null, text);
    }

    public JFXTag(Node graphic, String text) {
        this(graphic, text, false);
    }

    public JFXTag(Node graphic, String text, boolean closeable) {
        super.getStyleClass().add(STYLE_CLASS);
        Label label = new Label();
        label.textProperty().bindBidirectional(this.text);
        label.graphicProperty().bindBidirectional(this.graphic);
        JFXIcon closeBtn = new JFXIcon("icon:ion-ios-close-empty");
        closeBtn.getStyleClass().add("jfx-tag-close");
        closeBtn.managedProperty().bindBidirectional(this.closeable);
        closeBtn.setCursor(Cursor.HAND);
        closeBtn.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            super.setManaged(false);
            super.setVisible(false);
            if (onClosed != null) {
                onClosed.handle(event);
            }
        }); // 点击关闭
        this.setText(text);
        this.setGraphic(graphic);
        this.setCloseable(closeable);
        super.getChildren().addAll(label, closeBtn);
        super.setSpacing(5);
        super.setAlignment(Pos.CENTER);
        super.setPadding(new Insets(5));
        // 更换tag类型
        type.addListener((observable, oldValue, newValue) -> {
            Arrays.stream(JFXTagType.values()).forEach(jfxTagType -> {
                super.getStyleClass().remove(jfxTagType.getStyle());
            });
            super.getStyleClass().add(newValue.getStyle());
        });
    }

    public JFXTagType getType() {
        return type.get();
    }

    public SimpleObjectProperty<JFXTagType> typeProperty() {
        return type;
    }

    public void setType(JFXTagType type) {
        this.type.set(type);
    }

    public String getText() {
        return text.get();
    }

    public SimpleStringProperty textProperty() {
        return text;
    }

    public void setText(String text) {
        this.text.set(text);
    }

    public Node getGraphic() {
        return graphic.get();
    }

    public SimpleObjectProperty<Node> graphicProperty() {
        return graphic;
    }

    public void setGraphic(Node graphic) {
        this.graphic.set(graphic);
    }

    public boolean isCloseable() {
        return closeable.get();
    }

    public SimpleBooleanProperty closeableProperty() {
        return closeable;
    }

    public void setCloseable(boolean closeable) {
        this.closeable.set(closeable);
    }
}
