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

import com.cxxwl96.jfx.admin.client.common.AbstractController;
import com.cxxwl96.jfx.admin.client.utils.Alert;
import com.cxxwl96.jfx.admin.client.utils.Browser;
import com.cxxwl96.jfx.admin.client.utils.IconUtil;
import com.cxxwl96.jfx.admin.server.entity.Menu;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToolbar;

import javax.annotation.PostConstruct;

import cn.hutool.core.util.StrUtil;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.action.ActionMethod;
import io.datafx.controller.flow.action.ActionTrigger;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import lombok.extern.slf4j.Slf4j;

/**
 * WebViewController
 *
 * @author cxxwl96
 * @since 2022/10/2 14:25
 */
@Slf4j
@ViewController("/views/component/WebView.fxml")
public class WebViewController extends AbstractController {
    @FXMLViewFlowContext
    private ViewFlowContext context;

    @FXML
    private JFXToolbar toolbar;

    @FXML
    @ActionTrigger("back")
    private Label backBtn;

    @FXML
    @ActionTrigger("front")
    private Label frontBtn;

    @FXML
    @ActionTrigger("refresh")
    private Label refreshBtn;

    @FXML
    @ActionTrigger("share")
    private Label shareBtn;

    @FXML
    private JFXTextField titleText;

    @FXML
    private WebView browser;

    private WebEngine webEngine;

    public WebViewController() {
    }

    @PostConstruct
    private void init() {
        // 获取当前controller上下文中的menu
        final Menu menu = context.getRegisteredObject(Menu.class);
        // 初始化WebEngine;
        webEngine = browser.getEngine();
        webEngine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                titleText.setText(webEngine.getTitle());
            } else {
                titleText.setText("加载中...");
            }
        });
        webEngine.load(menu.getResourceUrl());

        // 是否隐藏toolbar
        if (menu.getHideToolbar()) {
            toolbar.setVisible(false);
        }

        // 初始化ToolBar
        backBtn.setGraphic(IconUtil.getIcon("icon-ic:baseline-chevron-left"));
        frontBtn.setGraphic(IconUtil.getIcon("icon-ic:baseline-chevron-right"));
        refreshBtn.setGraphic(IconUtil.getIcon("icon-ic:baseline-refresh"));
        shareBtn.setGraphic(IconUtil.getIcon("icon-ic:baseline-open-in-browser"));
        backBtn.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> backAction());
        frontBtn.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> frontAction());
        refreshBtn.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> refreshAction());
        shareBtn.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> shareAction());
    }

    @ActionMethod("back")
    private void backAction() {
        try {
            webEngine.getHistory().go(-1);
        } catch (Exception exception) {
            log.error("Back error: {}", exception.getMessage());
            Alert.info("已经是第一页了");
        }
    }

    @ActionMethod("front")
    private void frontAction() {
        try {
            webEngine.getHistory().go(1);
        } catch (Exception exception) {
            log.error("Front error: {}", exception.getMessage());
            Alert.info("已经是最后一页了");
        }
    }

    @ActionMethod("refresh")
    private void refreshAction() {
        webEngine.reload();
    }

    @ActionMethod("share")
    private void shareAction() {
        if (StrUtil.isNotBlank(webEngine.getLocation())) {
            Browser.openHttpInBrowser(webEngine.getLocation());
        }
    }
}
