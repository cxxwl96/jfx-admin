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

package com.cxxwl96.jfx.admin.client.view.setting.user;

import com.cxxwl96.jfx.admin.client.common.AbstractController;
import com.cxxwl96.jfx.admin.client.utils.SecurityUtil;
import com.jfoenix.controls.JFXListView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import io.datafx.controller.ViewController;
import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.FlowHandler;
import io.datafx.controller.flow.container.AnimatedFlowContainer;
import io.datafx.controller.flow.container.ContainerAnimations;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;

/**
 * IndexController
 *
 * @author cxxwl96
 * @since 2022/11/24 22:54
 */
@Slf4j
@ViewController("/views/setting/user/Index.fxml")
public class IndexController extends AbstractController {
    @FXML
    private JFXListView<Label> listMenu;

    @FXML
    private Label title;

    @FXML
    private StackPane mainPane;

    private final Map<String, StackPane> mainPaneMap = new HashMap<>();

    @PostConstruct
    public void init() {
        // 权限管理
        final List<Label> visibleItems = listMenu.getItems()
            .stream()
            .filter(item -> SecurityUtil.visible(item.getId()))
            .collect(Collectors.toList());
        listMenu.getItems().clear();
        listMenu.getItems().addAll(visibleItems);
        // 加载设置controller
        listMenu.getItems().forEach(item -> {
            try {
                final String controllerClass = (String) item.getUserData();
                final Class<?> clazz = Class.forName(controllerClass);
                final Flow flow = new Flow(clazz);
                final FlowHandler flowHandler = flow.createHandler();
                StackPane pane = flowHandler.start(
                    new AnimatedFlowContainer(Duration.millis(320), ContainerAnimations.FADE));
                mainPaneMap.put(item.getId(), pane);
            } catch (ClassNotFoundException | FlowException exception) {
                log.error(exception.getMessage(), exception);
            }
        });
        // 点击事件
        listMenu.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                return;
            }
            title.setText(newValue.getText());
            final StackPane pane = mainPaneMap.get(newValue.getId());
            mainPane.getChildren().clear();
            mainPane.getChildren().add(pane);
        });
        // 默认选中第一个
        listMenu.getSelectionModel().selectFirst();
    }
}
