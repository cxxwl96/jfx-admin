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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.File;

import cn.hutool.core.io.FileUtil;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

/**
 * ApplicationCache
 *
 * @author cxxwl96
 * @since 2022/12/5 00:43
 */
@Slf4j
public class ApplicationCache {
    private static final File CACHE_FILE = new File(".cache/cache.json");

    @Getter
    private static Cache cache;

    static {
        if (!CACHE_FILE.exists()) {
            cache = new Cache();
            FileUtil.writeUtf8String(JSON.toJSONString(cache), CACHE_FILE);
        } else {
            final String cacheJson = FileUtil.readUtf8String(CACHE_FILE);
            try {
                cache = JSONObject.parseObject(cacheJson, Cache.class);
            } catch (Exception exception) {
                cache = new Cache();
                FileUtil.writeUtf8String(JSON.toJSONString(cache), CACHE_FILE);
            }
        }
    }

    @Data
    @Accessors(chain = true)
    public static class Cache {
        private boolean darkTheme = false;

        /**
         * 保存cache
         */
        public void flushCache() {
            FileUtil.writeUtf8String(JSON.toJSONString(this), CACHE_FILE);
        }
    }
}
