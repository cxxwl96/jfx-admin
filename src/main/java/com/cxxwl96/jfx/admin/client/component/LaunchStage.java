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

import com.cxxwl96.jfx.admin.client.common.ApplicationConfig;
import com.cxxwl96.jfx.admin.client.utils.Alert;

import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.FlowException;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.extern.slf4j.Slf4j;

/**
 * 启动页
 *
 * @author cxxwl96
 * @since 2022/10/6 16:23
 */
@Slf4j
public class LaunchStage extends Stage {
    public LaunchStage() {
        try {
            Flow flow = new Flow(LaunchPageController.class);
            StackPane pane = flow.start();
            pane.setStyle("-fx-background-color: TRANSPARENT");
            final Scene scene = new Scene(pane);
            scene.setFill(Paint.valueOf("TRANSPARENT"));
            super.getIcons().add(new Image(ApplicationConfig.getAppLogo()));
            super.setScene(scene);
            super.setTitle(ApplicationConfig.getAppName());
            super.initStyle(StageStyle.TRANSPARENT);
            super.setAlwaysOnTop(true);
        } catch (FlowException exception) {
            log.error(exception.getMessage(), exception);
            Alert.error("程序启动失败", exception.getMessage());
        }
    }
}
