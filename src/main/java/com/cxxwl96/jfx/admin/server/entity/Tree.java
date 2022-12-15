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

package com.cxxwl96.jfx.admin.server.entity;

import com.cxxwl96.jfx.admin.server.base.entity.TreeNode;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollUtil;

/**
 * 树形数据处理
 *
 * @author cxxwl96
 * @since 2021/8/18 22:35
 */
public class Tree<T extends TreeNode<T>> {
    private List<T> list;

    private List<T> tree;

    private String rootValue;

    /**
     * 构建树形数据
     *
     * @param list 数据列表
     * @param rootValue 根节点的值
     */
    public Tree(List<T> list, String rootValue) {
        this.list = CollUtil.newArrayList(list); // new instance
        this.tree = new ArrayList<>();
        this.rootValue = rootValue;
        this.build();
    }

    /**
     * 构建树形数据
     *
     * @param treeList 树形数据列表
     */
    public Tree(List<T> treeList) {
        this.tree = CollUtil.newArrayList(treeList);
        // this.list = toList(treeList); // toList后会将children设置为null
    }

    /**
     * 获取完整树形数据
     *
     * @return 完整树形数据
     */
    public List<T> getRootTree() {
        return this.tree;
    }

    /**
     * 获取子树，包含子树根节点
     *
     * @param code 子树根节点code
     * @return 子树
     */
    public T getCodeTree(String code) {
        return findSubTreeByCode(this.tree, code);
    }

    /**
     * 获取子树，包含子树根节点
     *
     * @param id 子树根节点id
     * @return 子树
     */
    public T getSubTree(String id) {
        return findSubTree(this.tree, id);
    }

    /**
     * 获取子节点，包含子树根节点
     *
     * @param id 子树根节点id
     * @return 子树
     */
    public List<T> getSubList(String id) {
        final T subTree = findSubTree(this.tree, id);
        if (subTree != null) {
            final List<T> list = toList(subTree);
            // 去掉children
            // list.forEach(item -> item.setChildren(null)); // 暂时不去掉children，不然后续操作会丢失数据
            return list;
        }
        return null;
    }

    /**
     * 获取子树，不包含子树根节点
     *
     * @param id 子树根节点id
     * @return 子树列表
     */
    public List<T> getChildrenTree(String id) {
        final T subTree = findSubTree(this.tree, id);
        if (subTree != null) {
            return subTree.getChildren();
        }
        return null;
    }

    /**
     * 获取子节点列表，不包含子树根节点
     *
     * @param id 子树根节点id
     * @return 子节点列表
     */
    public List<T> getChildrenList(String id) {
        final List<T> childrenTree = getChildrenTree(id);
        // 去掉children
        // if (list != null) {
        //     list.forEach(item -> item.setChildren(null)); // 暂时不去掉children，不然后续操作会丢失数据
        // }
        return toList(childrenTree);
    }

    /**
     * 将树形数据转为集合数据
     *
     * @param treeNode 树形数据
     * @return 数据列表
     */
    public List<T> toList(T treeNode) {
        List<T> list = new ArrayList<>();
        final List<T> childrenList = toList(treeNode.getChildren());
        // 去掉children
        // treeNode.setChildren(null); // 暂时不去掉children，不然后续操作会丢失数据
        list.add(treeNode);
        if (childrenList != null) {
            list.addAll(childrenList);
        }
        return list;
    }

    /**
     * 将树形数据转为集合数据
     *
     * @param treeList 树形数据
     * @return 数据列表
     */
    public List<T> toList(List<T> treeList) {
        if (treeList == null) {
            return null;
        } else if (treeList.size() == 0) {
            return treeList;
        }
        List<T> list = new ArrayList<>();
        for (T item : treeList) {
            list.add(item);
            if (CollUtil.isNotEmpty(item.getChildren())) {
                list.addAll(toList(item.getChildren()));
            }
            // 去掉children
            // item.setChildren(null); // 暂时不去掉children，不然后续操作会丢失数据
        }
        return list;
    }

    /**
     * 构建树形数据
     */
    private void build() {
        // 查找根节点
        list.forEach(node -> {
            if (rootValue == null && node.getPId() == null) {
                tree.add(node);
            } else if (rootValue != null && rootValue.equals(node.getPId())) {
                tree.add(node);
            }
        });
        // 排序
        this.tree = this.tree.stream().sorted(Comparator.comparingInt(T::getOrderNo)).collect(Collectors.toList());
        // 查找子节点
        this.tree.forEach(node -> node.setChildren(findChildren(node.getId())));
    }

    private List<T> findChildren(String pId) {
        List<T> children = new ArrayList<>();
        // 判断是否还有子节点
        final Optional<T> optional = this.list.stream().filter(item -> pId.equals(item.getPId())).findFirst();
        if (!optional.isPresent()) {
            return null;
        }
        // 查找子节点
        List<T> finalChildren = children;
        this.list.forEach(item -> {
            if (pId.equals(item.getPId())) {
                finalChildren.add(item);
            }
        });
        // 排序
        children = children.stream().sorted(Comparator.comparingInt(T::getOrderNo)).collect(Collectors.toList());
        for (T child : children) {
            child.setChildren(findChildren(child.getId()));
        }
        return children;
    }

    private T findSubTree(List<T> treeList, String id) {
        if (CollUtil.isEmpty(treeList)) {
            return null;
        }
        for (T node : treeList) {
            if (id != null && id.equals(node.getId())) {
                return node;
            }
            T result = findSubTree(node.getChildren(), id);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    private T findSubTreeByCode(List<T> treeList, String code) {
        if (CollUtil.isEmpty(treeList)) {
            return null;
        }
        for (T node : treeList) {
            if (code != null && code.equals(node.getCode())) {
                return node;
            }
            T result = findSubTreeByCode(node.getChildren(), code);
            if (result != null) {
                return result;
            }
        }
        return null;
    }
}
