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

package com.cxxwl96.jfx.admin.client;

import com.cxxwl96.jfx.admin.client.common.ApplicationCache;
import com.cxxwl96.jfx.admin.client.common.ApplicationConfig;
import com.cxxwl96.jfx.admin.client.common.ApplicationStore;
import com.cxxwl96.jfx.admin.client.component.JFXDecorator;
import com.cxxwl96.jfx.admin.client.component.LaunchStage;
import com.cxxwl96.jfx.admin.client.view.login.LoginController;
import com.jfoenix.assets.JFoenixResources;
import com.jfoenix.svg.SVGGlyph;
import com.jfoenix.svg.SVGGlyphLoader;

import org.springframework.boot.SpringApplication;

import java.io.IOException;

import javax.swing.SwingUtilities;

import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.FlowHandler;
import io.datafx.controller.flow.container.AnimatedFlowContainer;
import io.datafx.controller.flow.container.ContainerAnimations;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;

/**
 * springboot & javafx 启动类
 *
 * @author cxxwl96
 * @since 2022/10/8 00:26
 */
@Slf4j
public class JFXApplication extends Application {
    // 启动页窗口
    private static LaunchStage launchStage;

    @FXMLViewFlowContext
    private final ViewFlowContext flowContext = new ViewFlowContext();

    /**
     * 启动程序
     *
     * @param args args
     */
    public static void run(Class<? extends JFXApplication> clazz, String[] args) {
        // 兼容macOS平台问题：Process manager already initialized: can't fully enable headless mode.
        System.setProperty("javafx.macosx.embedded", "true");
        java.awt.Toolkit.getDefaultToolkit();
        // 显示启动页
        SwingUtilities.invokeLater(() -> {
            new JFXPanel(); // 这将准备JavaFX工具包和环境
            Platform.runLater(() -> {
                launchStage = new LaunchStage();
                launchStage.show();
            });
        });
        // 启动spring容器
        SpringApplication.run(clazz, args);
        // 启动javafx程序
        Application.launch(clazz, args);
    }

    /**
     * fxml视图启动方法
     *
     * @param stage 父窗口
     */
    @Override
    public final void start(Stage stage) throws FlowException {
        // 启动前加载资源
        loadResourcesBeforeLaunch();
        // 设置舞台
        stage.setTitle(ApplicationConfig.getAppName());
        stage.getIcons().add(new Image(ApplicationConfig.getAppLogo()));
        stage.addEventFilter(WindowEvent.WINDOW_SHOWN, event -> Platform.runLater(launchStage::hide)); // 启动完成关闭启动页
        stage.setOnCloseRequest((event) -> Platform.exit());
        // 创建动画容器
        AnimatedFlowContainer container = new AnimatedFlowContainer(Duration.millis(320),
            ContainerAnimations.SWIPE_LEFT);
        // 创建装饰器
        JFXDecorator decorator = new JFXDecorator(stage, container.getView(), true, true, true);
        decorator.setCustomMaximize(true);
        decorator.setGraphic(new SVGGlyph(""));
        ApplicationStore.registerBean("Decorator", decorator); // 将装饰器交给datafx容器管理

        // 创建dataFx Flow流容器
        Flow contextFlow = new Flow(LoginController.class);
        final FlowHandler contextFlowHandler = contextFlow.createHandler(flowContext);
        ApplicationStore.registerBean("RootStage", stage);
        ApplicationStore.registerBean("ContextFlowHandler", contextFlowHandler);
        final StackPane stackPane = contextFlowHandler.start(container);
        stackPane.getStyleClass().add("backgroundPane");

        // 创建场景
        Scene scene = new Scene(decorator);
        scene.setFill(Color.TRANSPARENT);
        // 设置场景全局样式
        boolean darkTheme = ApplicationCache.getCache().isDarkTheme();
        if (darkTheme) {
            scene.getStylesheets().addAll(ApplicationStore.getDarkThemeResources());
        } else {
            scene.getStylesheets().addAll(ApplicationStore.getLightThemeResources());
        }
        ApplicationStore.setDarkTheme(darkTheme);
        // 设置舞台
        stage.setScene(scene);
        stage.show();
    }

    /**
     * 启动前加载资源
     */
    private void loadResourcesBeforeLaunch() {
        // 启动前加载
        new Thread(this::loadSvgResources).start();
    }

    /**
     * 加载SVG
     */
    private void loadSvgResources() {
        try {
            SVGGlyphLoader.loadGlyphsFont(JFoenixResources.load("/assets/fonts/IconFont/iconfont.svg").openStream(),
                ApplicationConfig.getFontIconPrefix());
            SVGGlyphLoader.loadGlyphsFont(JFoenixResources.load("/assets/fonts/IconFont/icomoon.svg").openStream(),
                ApplicationConfig.getFontIconPrefix());
        } catch (IOException exception) {
            log.error(exception.getMessage(), exception);
        }
    }
}
