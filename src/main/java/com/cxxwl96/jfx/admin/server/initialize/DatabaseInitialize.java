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

package com.cxxwl96.jfx.admin.server.initialize;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import javax.sql.DataSource;

import cn.hutool.core.date.StopWatch;
import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * DatabaseInitialize
 *
 * @author cxxwl96
 * @since 2022/12/15 22:18
 */
@Slf4j
@Component
public class DatabaseInitialize implements ApplicationRunner {
    @Value(value = "${spring.datasource.url}")
    private String url;

    @Autowired
    private DataSource dataSource;

    @Override
    public void run(ApplicationArguments args) {
        // 已存在则不初始化数据库
        String path = url.substring(url.lastIndexOf(":") + 1);
        if (path.contains("?")) {
            path = path.substring(0, path.lastIndexOf("?"));
        }
        File file = new File(path + ".mv.db");
        log.info("db file: {}", file.getPath());
        if (FileUtil.exist(file)) {
            return;
        }
        StopWatch watch = StopWatch.create("InitDB");
        watch.start();
        // 初始化数据库
        try {
            final ClassPathResource resource = new ClassPathResource("db/init.sql");
            final InputStreamReader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
            ScriptRunner scriptRunner = new ScriptRunner(dataSource.getConnection());
            scriptRunner.setSendFullScript(true); // 必须设置该参数为true，否则无法执行begin……end
            scriptRunner.setLogWriter(null);
            scriptRunner.runScript(reader);
        } catch (Exception exception) {
            log.error("Initialize database error: {}", exception.getMessage(), exception);
            return;
        }
        watch.stop();
        log.info("Initialize database in {} seconds", watch.getTotalTimeSeconds());
    }

}

