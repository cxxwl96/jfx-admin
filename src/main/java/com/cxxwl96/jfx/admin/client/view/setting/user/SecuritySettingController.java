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
import com.cxxwl96.jfx.admin.client.utils.Alert;
import com.cxxwl96.jfx.admin.server.entity.User;
import com.cxxwl96.jfx.admin.server.lang.Result;
import com.cxxwl96.jfx.admin.server.service.impl.AuthServiceImpl;
import com.cxxwl96.jfx.admin.server.service.impl.UserServiceImpl;
import com.google.code.kaptcha.Producer;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;

import org.springframework.beans.factory.annotation.Autowired;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;

import io.datafx.controller.ViewController;
import io.datafx.controller.flow.action.ActionMethod;
import io.datafx.controller.flow.action.ActionTrigger;
import io.datafx.core.concurrent.ProcessChain;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * SecuritySettingController
 *
 * @author cxxwl96
 * @since 2022/11/26 22:03
 */
@Slf4j
@ViewController("/views/setting/user/SecuritySetting.fxml")
public class SecuritySettingController extends AbstractController {
    @FXML
    private VBox rootPane;

    @FXML
    private JFXPasswordField oldPassword;

    @FXML
    private JFXPasswordField newPassword;

    @FXML
    private JFXPasswordField confirmPassword;

    @FXML
    private ImageView imgCaptcha;

    @FXML
    private JFXTextField textCaptcha;

    @FXML
    @ActionTrigger("submitAction")
    private JFXButton submitBtn;

    @FXML
    private JFXSpinner spinner;

    // 验证码生成器
    @Autowired
    private Producer producer;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private AuthServiceImpl authService;

    // 验证码
    private String captcha;

    @PostConstruct
    private void init() {
        // 添加失去焦点校验
        Consumer<JFXPasswordField> focusedListener = (passwordField) -> {
            passwordField.focusedProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue) {
                    passwordField.validate();
                }
            });
        };
        focusedListener.accept(oldPassword);
        focusedListener.accept(newPassword);
        focusedListener.accept(confirmPassword);
        // 验证码图片添加点击事件
        imgCaptcha.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> flushCaptcha());
        // 绑定spinner
        spinner.visibleProperty().bind(rootPane.disableProperty());
        // 刷新验证码
        flushCaptcha();
    }

    @ActionMethod("submitAction")
    private void submitAction() {
        if (!validate()) {
            return;
        }
        if (!newPassword.getText().equals(confirmPassword.getText())) {
            Alert.error("提示", "新密码与确认密码不一致");
            return;
        }
        if (!captcha.equalsIgnoreCase(textCaptcha.getText())) {
            Alert.error("提示", "验证码错误");
            return;
        }
        ProcessChain.create().addRunnableInPlatformThread(() -> rootPane.setDisable(true)).addSupplierInExecutor(() -> {
            // 获取当前用户信息
            Result<User> result = authService.getCurrentAuthUser();
            if (!result.isSuccess()) {
                return result;
            }
            return userService.updatePassword(result.getData().getId(), oldPassword.getText(), newPassword.getText());
        }).addConsumerInPlatformThread(result -> {
            if (!result.isSuccess()) {
                Alert.error("提示", result.getMsg());
                return;
            }
            Alert.info("提示", result.getMsg());
            oldPassword.clear();
            newPassword.clear();
            confirmPassword.clear();
            textCaptcha.clear();
        }).onException(exception -> {
            log.error(exception.getMessage(), exception);
            Alert.error("发生未知错误", exception.getMessage());
        }).withFinal(() -> {
            flushCaptcha(); // 刷新验证码
            rootPane.setDisable(false);
        }).run();
    }

    /**
     * 表单校验
     *
     * @return 是否校验通过
     */
    private boolean validate() {
        return oldPassword.validate() && newPassword.validate() && confirmPassword.validate() && textCaptcha.validate();
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
