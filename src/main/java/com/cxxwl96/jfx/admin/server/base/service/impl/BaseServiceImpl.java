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

package com.cxxwl96.jfx.admin.server.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cxxwl96.jfx.admin.server.base.entity.BaseEntity;
import com.cxxwl96.jfx.admin.server.base.form.OrderItem;
import com.cxxwl96.jfx.admin.server.base.form.SearchForm;
import com.cxxwl96.jfx.admin.server.base.form.WhereItem;
import com.cxxwl96.jfx.admin.server.base.service.BaseService;
import com.cxxwl96.jfx.admin.server.function.PropertyFunc;
import com.cxxwl96.jfx.admin.server.lang.SearchResult;
import com.cxxwl96.jfx.admin.server.utils.NamingUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.hutool.core.util.StrUtil;

/**
 * 服务基类
 *
 * @author cxxwl96
 * @since 2021/8/22 18:17
 */
public class BaseServiceImpl<M extends BaseMapper<T>, T extends BaseEntity> extends ServiceImpl<M, T>
    implements BaseService<T> {
    /**
     * 是否存在
     *
     * @param id ID
     * @return 是否存在
     */
    @Override
    public boolean exist(String id) {
        return super.getById(id) != null;
    }

    /**
     * 分页条件查询
     *
     * @param searchForm 查询表单
     * @return 数据
     */
    @Override
    public SearchResult<T> getPageList(SearchForm searchForm) {
        // 生成QueryWrapper条件
        final QueryWrapper<T> qw = generateWrapper(searchForm, new QueryWrapper<>());

        // 生成Pager分页
        Page<T> pager = new Page<>(searchForm.getPage(), searchForm.getPageSize());
        // 分页条件查询
        pager = super.page(pager, qw);
        // 封装返回数据
        final SearchResult<T> searchResult = new SearchResult<>();
        searchResult.setPage(pager.getCurrent());
        searchResult.setPageSize(pager.getSize());
        searchResult.setPageCount(pager.getTotal() % pager.getSize() == 0
            ? pager.getTotal() / pager.getSize()
            : pager.getTotal() / pager.getSize() + 1);
        searchResult.setTotal(pager.getTotal());
        searchResult.setItems(pager.getRecords());

        return searchResult;
    }

    /**
     * 生成Wrapper
     *
     * @param searchForm 查询表单
     * @return Wrapper
     */
    @Override
    public <E extends AbstractWrapper<T, String, E>> E generateWrapper(SearchForm searchForm, E wrapper) {
        if (wrapper == null) {
            throw new RuntimeException("Failed to generate wrapper with null");
        }
        if (!(wrapper instanceof UpdateWrapper) && !(wrapper instanceof QueryWrapper)) {
            throw new RuntimeException(String.format("%s not instanceof %s or %s.", wrapper.getClass().getName(),
                UpdateWrapper.class.getName(), QueryWrapper.class.getName()));
        }
        // 得到小写下划线的字段名
        PropertyFunc<T, ?> column = BaseEntity::getCreateTime;
        String createTimeFiled = NamingUtil.toLowerCaseUnderline(column.getFieldName());

        // 解析OrderItem
        // 若果没有order by，默认添加时间降序
        final List<OrderItem> orderItems = searchForm.getOrderItems();
        if (orderItems.size() == 0) {
            // 默认创建时间降序
            wrapper.orderByDesc(createTimeFiled);
        }
        for (OrderItem item : orderItems) {
            final String filed = NamingUtil.toLowerCaseUnderline(item.getColumn());
            if (item.isAsc()) {
                wrapper.orderByAsc(filed);
            } else {
                wrapper.orderByDesc(filed);
            }
        }

        // 解析WhereItem
        List<WhereItem> whereItems = searchForm.getWhereItems();
        for (WhereItem item : whereItems) {
            if (StrUtil.isNotBlank(item.getColumn()) && item.getCondition() != null && item.getValue() != null) {
                if (item.getWhereOperator() == WhereItem.WhereOperator.OR) {
                    wrapper.or();
                }
                final String filed = NamingUtil.toLowerCaseUnderline(item.getColumn());
                switch (item.getCondition()) {
                    case EQ:
                        wrapper.eq(filed, item.getValue());
                        break;
                    case NE:
                        wrapper.ne(filed, item.getValue());
                        break;
                    case GT:
                        wrapper.gt(filed, item.getValue());
                        break;
                    case GE:
                        wrapper.ge(filed, item.getValue());
                        break;
                    case LT:
                        wrapper.lt(filed, item.getValue());
                        break;
                    case LE:
                        wrapper.le(filed, item.getValue());
                        break;
                    case LIKE:
                        wrapper.like(filed, item.getValue());
                        break;
                    case RANGE_TIME:
                        // 创建时间范围查询
                        final List<?> value = (List<?>) item.getValue();
                        if (value.size() == 2) {
                            try {
                                // 修改时间部分为 00:00:00, 第二个日期+1天
                                final String startTimeStr = (String) value.get(0);
                                final String endTimeStr = (String) value.get(1);

                                final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                Date startTime = sdf.parse(startTimeStr);
                                Date endTime = sdf.parse(endTimeStr);
                                // 日期+1天
                                final Calendar calendar = Calendar.getInstance();
                                calendar.setTime(endTime);
                                calendar.add(Calendar.DAY_OF_MONTH, 1);
                                endTime = calendar.getTime();

                                wrapper.ge(createTimeFiled, startTime);
                                wrapper.le(createTimeFiled, endTime);
                            } catch (ClassCastException | ParseException exception) {
                                // 不做任何处理
                                log.error(exception.getMessage(), exception);
                            }
                        }
                        break;
                }
            }
        }
        return wrapper;
    }
}
