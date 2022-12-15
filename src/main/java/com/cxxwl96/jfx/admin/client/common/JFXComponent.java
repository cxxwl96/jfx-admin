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

package com.cxxwl96.jfx.admin.client.common;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import io.datafx.controller.ViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import lombok.extern.slf4j.Slf4j;

/**
 * 自定义组件
 *
 * @author cxxwl96
 * @since 2022/10/18 00:16
 */
@Slf4j
public class JFXComponent extends StackPane implements IController {
    public JFXComponent() {
        load();
        invokePostConstruct();
    }

    /**
     * 加载视图
     */
    private void load() {
        final ViewController annotation = this.getClass().getDeclaredAnnotation(ViewController.class);
        if (annotation == null) {
            return;
        }
        final String path = annotation.value();
        final InputStream inputStream = JFXComponent.class.getResourceAsStream(path);
        final FXMLLoader loader = new FXMLLoader();
        loader.setController(this);
        Parent parent;
        try {
            parent = loader.load(inputStream);
        } catch (IOException exception) {
            log.error("Failed to load {}", path, exception);
            return;
        }
        super.getChildren().add(parent);
    }

    /**
     * 执行PostConstruct
     */
    private void invokePostConstruct() {
        // 执行PostConstruct
        final List<Method> postMethod = Arrays.stream(this.getClass().getDeclaredMethods())
            .filter(method -> method.isAnnotationPresent(PostConstruct.class))
            .collect(Collectors.toList());
        for (Method method : postMethod) {
            boolean accessible = method.isAccessible();
            try {
                method.setAccessible(true);
                method.invoke(this);
            } catch (IllegalAccessException | InvocationTargetException exception) {
                log.error("Invoke post construct method error: {}", exception.getMessage(), exception);
                break;
            } finally {
                method.setAccessible(accessible);
            }
        }
    }

    @Override
    public void initialize() {
    }
}
