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

package com.cxxwl96.jfx.admin.client.view.system.user;

import com.cxxwl96.jfx.admin.client.component.JFXTag;
import com.cxxwl96.jfx.admin.client.component.detail.DetailValueFactory;
import com.cxxwl96.jfx.admin.server.entity.User;

import cn.hutool.core.util.StrUtil;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;

/**
 * RoleDetailValueFactory
 *
 * @author cxxwl96
 * @since 2022/11/27 15:28
 */
public class RoleDetailValueFactory extends DetailValueFactory {
    @Override
    public Node getValue(Object data, String fieldName) {
        final User user = (User) data;
        if (user == null || user.getRoles() == null) {
            return new Label();
        }
        final FlowPane pane = new FlowPane();
        pane.setVgap(10);
        pane.setHgap(10);
        user.getRoles().forEach(role -> {
            final String tagText;
            if (StrUtil.isNotBlank(role.getCode())) {
                tagText = role.getName() + " " + role.getCode();
            } else {
                tagText = role.getName();
            }
            final JFXTag tag = new JFXTag(tagText);
            tag.setType(JFXTag.JFXTagType.PRIMARY);
            pane.getChildren().add(tag);
        });
        return pane;
    }
}
