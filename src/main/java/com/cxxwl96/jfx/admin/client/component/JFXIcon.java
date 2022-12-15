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

import com.cxxwl96.jfx.admin.client.utils.IconUtil;
import com.jfoenix.svg.SVGGlyph;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Node;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import lombok.extern.slf4j.Slf4j;

/**
 * JFXIcon
 *
 * @author cxxwl96
 * @since 2022/10/18 01:16
 */
@Slf4j
public class JFXIcon extends SVGGlyph {
    private final SimpleStringProperty iconCode = new SimpleStringProperty();

    private int glyphId;

    private String name;

    public JFXIcon() {
        iconCode.addListener((observable, oldValue, newValue) -> {
            Node icon = IconUtil.getIcon(newValue);
            if (icon instanceof SVGGlyph) {
                SVGGlyph glyph = (SVGGlyph) icon;
                this.glyphId = glyph.getGlyphId();
                this.name = glyph.getName();
                super.setShape(glyph.getShape());
                super.setFill(glyph.getFill());
                super.sizeProperty().bindBidirectional(glyph.sizeProperty());
                super.fillProperty().bindBidirectional(glyph.fillProperty());
                // 从icomoon导入后，图标被颠倒了，我们需要应用变换来校正图标
                super.getTransforms().add(new Scale(1, -1));
                Translate height = new Translate();
                height.yProperty().bind(Bindings.createDoubleBinding(() -> -super.getHeight(), super.heightProperty()));
                super.getTransforms().add(height);
                super.sizeProperty().bindBidirectional(glyph.sizeProperty());
            } else {
                log.error("IconCode {} cannot be found.", newValue);
            }
        });
    }

    public JFXIcon(String iconCode) {
        this();
        this.setIconCode(iconCode);
    }

    public String getIconCode() {
        return iconCode.get();
    }

    public SimpleStringProperty iconCodeProperty() {
        return iconCode;
    }

    public void setIconCode(String iconCode) {
        this.iconCode.set(iconCode);
    }

    @Override
    public int getGlyphId() {
        return glyphId;
    }

    @Override
    public String getName() {
        return name;
    }
}
