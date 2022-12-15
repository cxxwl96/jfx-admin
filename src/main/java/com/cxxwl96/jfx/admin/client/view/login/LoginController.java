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

package com.cxxwl96.jfx.admin.client.view.login;

import com.cxxwl96.jfx.admin.client.common.AbstractController;
import com.cxxwl96.jfx.admin.client.common.ApplicationConfig;
import com.cxxwl96.jfx.admin.client.common.ApplicationStore;
import com.cxxwl96.jfx.admin.client.utils.Alert;
import com.cxxwl96.jfx.admin.client.view.frame.IndexController;
import com.cxxwl96.jfx.admin.server.entity.User;
import com.cxxwl96.jfx.admin.server.lang.Result;
import com.cxxwl96.jfx.admin.server.service.impl.AuthServiceImpl;
import com.google.code.kaptcha.Producer;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.base.IFXValidatableControl;

import org.springframework.beans.factory.annotation.Autowired;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;

import io.datafx.controller.ViewController;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.action.ActionMethod;
import io.datafx.controller.flow.action.ActionTrigger;
import io.datafx.controller.flow.context.ActionHandler;
import io.datafx.controller.flow.context.FlowActionHandler;
import io.datafx.controller.util.VetoException;
import io.datafx.core.concurrent.ProcessChain;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * LoginController
 *
 * @author cxxwl96
 * @since 2022/9/11 12:26
 */
@Slf4j
@ViewController("/views/login/Login.fxml")
public class LoginController extends AbstractController {
    @ActionHandler
    private FlowActionHandler actionHandler;

    @FXML
    private Label appTitle;

    @FXML
    private Label appDescription;

    @FXML
    private StackPane loginBox;

    @FXML
    private JFXSpinner loginSpinner;

    @FXML
    private Label loginTitle;

    @FXML
    private JFXTextField textUsername;

    @FXML
    private JFXPasswordField textPassword;

    @FXML
    private ImageView imgCaptcha;

    @FXML
    private JFXTextField textCaptcha;

    @FXML
    @ActionTrigger("loginAction")
    private JFXButton loginBtn;

    // 验证码生成器
    @Autowired
    private Producer producer;

    // 认证服务类
    @Autowired
    private AuthServiceImpl authService;

    // 验证码
    private String captcha;

    @PostConstruct
    private void init() {
        // 初始化stage
        initStage();
        // 初始化登录界面
        initLogin();
    }

    /**
     * 初始化装饰器
     */
    private void initStage() {
        // 设置可调整大小
        Stage stage = ApplicationStore.getRootStage().orElseThrow(() -> new IllegalStateException("window is null"));
        stage.setWidth(800);
        stage.setHeight(520);
    }

    /**
     * 初始化登录界面
     */
    private void initLogin() {
        appTitle.setText(ApplicationConfig.getAppName().toUpperCase(Locale.ROOT));
        appDescription.setText(ApplicationConfig.getAppDescription());
        loginTitle.setText("登录" + ApplicationConfig.getAppName());
        // 刷新验证码
        flushCaptcha();
        // 输入框添加失去焦点事件
        Consumer<Node> focusedListener = (input) -> input.focusedProperty()
            .addListener((observable, oldValue, newValue) -> {
                if (!newValue && input instanceof IFXValidatableControl) {
                    ((IFXValidatableControl) input).validate();
                }
            });
        focusedListener.accept(textUsername);
        focusedListener.accept(textPassword);
        focusedListener.accept(textCaptcha);
        // 输入框添加回车事件
        Consumer<Node> keyListener = (input) -> input.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                loginAction();
            }
        });
        keyListener.accept(textUsername);
        keyListener.accept(textPassword);
        keyListener.accept(textCaptcha);
        // 验证码图片添加点击事件
        imgCaptcha.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> flushCaptcha());
        // 绑定登录事件
        loginSpinner.visibleProperty().bind(loginBox.disableProperty());
    }

    /**
     * 登录
     */
    @ActionMethod("loginAction")
    void loginAction() {
        if (!textUsername.validate() || !textPassword.validate() || !textCaptcha.validate()) {
            return;
        }
        ProcessChain.create().addRunnableInPlatformThread(() -> loginBox.setDisable(true)).addSupplierInExecutor(() -> {
            // 检查验证码
            if (!captcha.equalsIgnoreCase(textCaptcha.getText())) {
                log.error("Captcha: {}, InputCaptcha: {}", captcha, textCaptcha.getText());
                return Result.failed(Result.CODE_FAILED, "验证码错误", new User());
            }
            final String username = textUsername.getText();
            final String password = textPassword.getText();
            // 登录
            return authService.login(username, password);
        }).addConsumerInPlatformThread(result -> result.successAndThen(res -> {
            // 登录成功进入主界面
            log.info("{} login success", res.getData().getUsername());
            try {
                actionHandler.navigate(IndexController.class);
            } catch (VetoException | FlowException exception) {
                log.error(exception.getMessage(), exception);
            }
        }).failedAndThen((code, msg) -> {
            // 登录失败弹窗提示
            Alert.error("登录失败", msg);
            // 刷新验证码
            flushCaptcha();
        })).onException(exception -> {
            log.error(exception.getMessage(), exception);
            Alert.error("发生未知错误", exception.getMessage());
        }).withFinal(() -> loginBox.setDisable(false)).run();
    }

    /**
     * 刷新验证码
     */
    @SneakyThrows
    void flushCaptcha() {
        try {
            // 验证码
            captcha = producer.createText();
            final BufferedImage bufferedImage = producer.createImage(captcha);
            // 将验证码图片写入到output流
            final ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "jpg", os);
            InputStream is = new ByteArrayInputStream(os.toByteArray());
            final Image image = new Image(is);
            imgCaptcha.setImage(image);
        } catch (IOException exception) {
            log.error(exception.getMessage(), exception);
        }
    }
}
