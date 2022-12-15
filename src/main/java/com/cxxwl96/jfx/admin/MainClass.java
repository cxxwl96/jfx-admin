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

package com.cxxwl96.jfx.admin;

import com.cxxwl96.jfx.admin.client.JFXApplication;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * springboot & javafx 启动类
 *
 * @author cxxwl96
 * @since 2022/9/10 23:43
 */
@SpringBootApplication
@MapperScan(basePackageClasses = MainClass.class)
public class MainClass extends JFXApplication {
    public static void main(String[] args) {
        JFXApplication.run(MainClass.class, args);
    }
}
