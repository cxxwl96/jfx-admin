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
import com.cxxwl96.jfx.admin.client.common.ApplicationConfig;

import java.util.Locale;

import javax.annotation.PostConstruct;

import io.datafx.controller.ViewController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import lombok.extern.slf4j.Slf4j;

/**
 * LaunchPageController
 *
 * @author cxxwl96
 * @since 2022/10/6 16:23
 */
@Slf4j
@ViewController("/views/component/LaunchPage.fxml")
public class LaunchPageController extends AbstractController {
    @FXML
    private Label titleLabel;

    @PostConstruct
    private void init() {
        titleLabel.setText(ApplicationConfig.getAppName().toUpperCase(Locale.ROOT));
    }
}
