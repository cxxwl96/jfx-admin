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

package com.cxxwl96.jfx.admin.client.component.iconswitch;

import com.jfoenix.assets.JFoenixResources;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;

/**
 * IconSwitch
 *
 * @author cxxwl96
 * @since 2022/12/5 00:12
 */
public class IconSwitch extends Control {

    // CSS pseudo classes
    private BooleanProperty selected;

    private final Label symbol;

    // ******************** Constructors **************************************
    public IconSwitch() {
        getStyleClass().addAll("icon-switch");
        symbol = new Label();
        symbol.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
    }

    // ******************** Methods *******************************************
    public final boolean isSelected() {
        return null != selected && selected.get();
    }

    public final void setSelected(final boolean ON) {
        selectedProperty().set(ON);
    }

    public final BooleanProperty selectedProperty() {
        if (null == selected) {
            selected = new SimpleBooleanProperty();
        }
        return selected;
    }

    // ******************** Style related *************************************
    @Override
    protected Skin<?> createDefaultSkin() {
        return new IconSwitchSkin(this);
    }

    @Override
    public String getUserAgentStylesheet() {
        return JFoenixResources.load("/assets/css/icon-switch.css").toExternalForm();
    }

    public Label getSymbol() {
        return symbol;
    }
}
