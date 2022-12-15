-- user
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user
(
    id          varchar(64) not null primary key,
    username    varchar(255) default NULL,
    password    varchar(255) default NULL,
    name        varchar(255) default NULL,
    sex         INT          default NULL,
    age         INT          default NULL,
    birthday    varchar(64)  default NULL,
    email       varchar(255) default NULL,
    phone       varchar(255) default NULL,
    avatar      varchar(255) default NULL,
    city        varchar(255) default NULL,
    last_login  varchar(64)  default NULL,
    dept_id     varchar(64)  default NULL,
    remark      varchar(255) default NULL,
    create_time varchar(64)  default NULL,
    create_by   varchar(64)  default NULL,
    update_time varchar(64)  default NULL,
    update_by   varchar(64)  default NULL
);
INSERT INTO sys_user (id, username, password, name, sex, age, birthday, email, phone, avatar, city, last_login, dept_id, remark, create_time, create_by, update_time, update_by) VALUES ('30d808cbd7c94c66b4512718151b00aa', 'administrator', '$2a$10$B8ljffNvuQDHsjoPsHIhSuIU1qBPgagv/ZYAiMtGk4Ub6sSTqfjqO', '超级管理员', 1, 24, null, 'cxxwl96@sina.com', '15100001111', '/head/head.jpg', null, '2022-12-07T00:46:03.975', null, null, '2022-10-22T01:13:40.685', null, '2022-11-28T00:18:05.490', '30d808cbd7c94c66b4512718151b00aa');
INSERT INTO sys_user (id, username, password, name, sex, age, birthday, email, phone, avatar, city, last_login, dept_id, remark, create_time, create_by, update_time, update_by) VALUES ('6ac6706202fb35007ba08df87965ed8b', 'admin', '$2a$10$F4Z4Mf/GyF8ynKWnk5geseOzl8py9bNJD6tmO2yTey2J.Xzmme80.', '成应奎', 0, 0, null, null, null, null, null, '2022-12-07T00:47:01.113', null, null, '2022-11-25T23:20:40.453', '30d808cbd7c94c66b4512718151b00aa', '2022-11-28T00:28:52.256', '30d808cbd7c94c66b4512718151b00aa');
INSERT INTO sys_user (id, username, password, name, sex, age, birthday, email, phone, avatar, city, last_login, dept_id, remark, create_time, create_by, update_time, update_by) VALUES ('1943f0783d3814c66ff4bcbab541045a', 'test', '$2a$10$QqHB2wUWyamQ1yfCZqJmBeR/y0jHQKEv7vm0B/0tb.805W4u4VOAm', '测试账号', 0, 0, null, null, null, null, null, '2022-11-28T00:26:41.969', null, null, '2022-11-28T00:25:20.654', '30d808cbd7c94c66b4512718151b00aa', '2022-11-28T00:41:33.750', '30d808cbd7c94c66b4512718151b00aa');

-- user role
DROP TABLE IF EXISTS sys_user_role;
CREATE TABLE sys_user_role
(
    id          varchar(64) not null primary key,
    user_id     varchar(64)  default NULL,
    role_id     varchar(64)  default NULL,
    remark      varchar(255) default NULL,
    create_time datetime     default NULL,
    create_by   varchar(64)  default NULL,
    update_time datetime     default NULL,
    update_by   varchar(64)  default NULL
);
INSERT INTO sys_user_role (id, user_id, role_id, remark, create_time, create_by, update_time, update_by) VALUES ('2ea968c3c25120a0db2bc8f8f61bb42f', '30d808cbd7c94c66b4512718151b00aa', '57778d0648b4df8e1596f5c6a9dc63c3', null, '2022-09-24T19:14:36.088', '30d808cbd7c94c66b4512718151b00aa', null, null);
INSERT INTO sys_user_role (id, user_id, role_id, remark, create_time, create_by, update_time, update_by) VALUES ('1ce675af7e73ce50251ae0136ed40ed0', '6ac6706202fb35007ba08df87965ed8b', '8465885b42ad665e4a92af40faa26b22', null, '2022-11-25T23:24:56.095', '30d808cbd7c94c66b4512718151b00aa', null, null);
INSERT INTO sys_user_role (id, user_id, role_id, remark, create_time, create_by, update_time, update_by) VALUES ('ce2f1f9345e531672055c0e56993b5ff', '1943f0783d3814c66ff4bcbab541045a', '60385a4b5e4537f3f6dc52dbb1171241', null, '2022-11-28T00:27:46.750', '30d808cbd7c94c66b4512718151b00aa', null, null);

-- role
DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role
(
    id          varchar(64) not null primary key,
    name        varchar(255) default NULL,
    code        varchar(255) default NULL,
    level       INT          default NULL,
    type        INT          default 0,
    status      INT          default 0,
    description varchar(255) default NULL,
    remark      varchar(255) default NULL,
    create_time datetime     default NULL,
    create_by   varchar(64)  default NULL,
    update_time datetime     default NULL,
    update_by   varchar(64)  default NULL
);
INSERT INTO sys_role (id, name, code, level, type, status, description, remark, create_time, create_by, update_time, update_by) VALUES ('57778d0648b4df8e1596f5c6a9dc63c3', '超级管理员', 'Administrator', 99999, 3, 0, '超级管理员拥有所有权限', null, '2022-09-24T19:14:36.072', '30d808cbd7c94c66b4512718151b00aa', null, null);
INSERT INTO sys_role (id, name, code, level, type, status, description, remark, create_time, create_by, update_time, update_by) VALUES ('8465885b42ad665e4a92af40faa26b22', '管理员', 'admin', 100, 1, 0, null, null, '2022-11-25T23:24:31.176', '30d808cbd7c94c66b4512718151b00aa', '2022-11-28T00:21:43.547', '30d808cbd7c94c66b4512718151b00aa');
INSERT INTO sys_role (id, name, code, level, type, status, description, remark, create_time, create_by, update_time, update_by) VALUES ('60385a4b5e4537f3f6dc52dbb1171241', '游客', 'visitor', 0, 0, 0, '', '', '2022-11-28T00:20:44.616', '30d808cbd7c94c66b4512718151b00aa', '2022-11-28T00:21:26.858', '30d808cbd7c94c66b4512718151b00aa');

-- role menu
DROP TABLE IF EXISTS sys_role_menu;
CREATE TABLE sys_role_menu
(
    id          varchar(64) not null primary key,
    role_id     varchar(64)  default NULL,
    menu_id     varchar(64)  default NULL,
    leaf        boolean      default NULL,
    remark      varchar(255) default NULL,
    create_time datetime     default NULL,
    create_by   varchar(64)  default NULL,
    update_time datetime     default NULL,
    update_by   varchar(64)  default NULL
);
INSERT INTO sys_role_menu (id, role_id, menu_id, leaf, remark, create_time, create_by, update_time, update_by) VALUES ('1a919b2085c4110521cf5e52de52a3f3', '60385a4b5e4537f3f6dc52dbb1171241', 'a15e56aab7c846e5821c936f070cf7f8', null, null, '2022-11-28T00:26:07.094', '30d808cbd7c94c66b4512718151b00aa', null, null);
INSERT INTO sys_role_menu (id, role_id, menu_id, leaf, remark, create_time, create_by, update_time, update_by) VALUES ('16adf63aec00ba2b52fbf58d0acf83a4', '60385a4b5e4537f3f6dc52dbb1171241', '647bc5ed2313bb89cf2e7d3ba06bae00', null, null, '2022-11-28T00:26:07.095', '30d808cbd7c94c66b4512718151b00aa', null, null);
INSERT INTO sys_role_menu (id, role_id, menu_id, leaf, remark, create_time, create_by, update_time, update_by) VALUES ('8777af3fd573821f679575d8654d4d15', '8465885b42ad665e4a92af40faa26b22', '4c26acf2d94dc59f8c038601f8ca329d', null, null, '2022-12-07T00:46:39.165', '30d808cbd7c94c66b4512718151b00aa', null, null);
INSERT INTO sys_role_menu (id, role_id, menu_id, leaf, remark, create_time, create_by, update_time, update_by) VALUES ('920682c65b7868c74305fd8dc05c8284', '8465885b42ad665e4a92af40faa26b22', '832d256d9c3b1ef927701e0d1dc47bff', null, null, '2022-12-07T00:46:39.179', '30d808cbd7c94c66b4512718151b00aa', null, null);
INSERT INTO sys_role_menu (id, role_id, menu_id, leaf, remark, create_time, create_by, update_time, update_by) VALUES ('ccea5661357f02d46c9712c1a6c06767', '8465885b42ad665e4a92af40faa26b22', '352432d53ff714c397f738ce9aef61a3', null, null, '2022-12-07T00:46:39.180', '30d808cbd7c94c66b4512718151b00aa', null, null);
INSERT INTO sys_role_menu (id, role_id, menu_id, leaf, remark, create_time, create_by, update_time, update_by) VALUES ('007f315de383ec7f73ef260b113b5ac8', '8465885b42ad665e4a92af40faa26b22', '95749ef5e258c9ad2972490f2637ed06', null, null, '2022-12-07T00:46:39.181', '30d808cbd7c94c66b4512718151b00aa', null, null);
INSERT INTO sys_role_menu (id, role_id, menu_id, leaf, remark, create_time, create_by, update_time, update_by) VALUES ('f74b317ffb245759ff5594f9691149bd', '8465885b42ad665e4a92af40faa26b22', '647bc5ed2313bb89cf2e7d3ba06bae00', null, null, '2022-12-07T00:46:39.181', '30d808cbd7c94c66b4512718151b00aa', null, null);
INSERT INTO sys_role_menu (id, role_id, menu_id, leaf, remark, create_time, create_by, update_time, update_by) VALUES ('6aca8a7ec2d8695c25538ef096898375', '8465885b42ad665e4a92af40faa26b22', '71a292a59a51045439152c49b29b3b51', null, null, '2022-12-07T00:46:39.185', '30d808cbd7c94c66b4512718151b00aa', null, null);
INSERT INTO sys_role_menu (id, role_id, menu_id, leaf, remark, create_time, create_by, update_time, update_by) VALUES ('0c6182e739a9ac860fcd03689375c23d', '8465885b42ad665e4a92af40faa26b22', 'f296adcbfa464ca4900c5d6ca2c796bd', null, null, '2022-12-07T00:46:39.185', '30d808cbd7c94c66b4512718151b00aa', null, null);
INSERT INTO sys_role_menu (id, role_id, menu_id, leaf, remark, create_time, create_by, update_time, update_by) VALUES ('3eb258b7133f19993052d16990d6935c', '8465885b42ad665e4a92af40faa26b22', '019ef3592ad0bf0ba3b733110e3c3d62', null, null, '2022-12-07T00:46:39.187', '30d808cbd7c94c66b4512718151b00aa', null, null);
INSERT INTO sys_role_menu (id, role_id, menu_id, leaf, remark, create_time, create_by, update_time, update_by) VALUES ('c1f1206f7cdf92c4e3be0b0249e8f34b', '8465885b42ad665e4a92af40faa26b22', 'a488581fb1e79527e69717aa02036c9f', null, null, '2022-12-07T00:46:39.188', '30d808cbd7c94c66b4512718151b00aa', null, null);
INSERT INTO sys_role_menu (id, role_id, menu_id, leaf, remark, create_time, create_by, update_time, update_by) VALUES ('229613d3729785158eb621575ed2cc01', '8465885b42ad665e4a92af40faa26b22', 'deaca451cbd84e40e333f014a5c04397', null, null, '2022-12-07T00:46:39.189', '30d808cbd7c94c66b4512718151b00aa', null, null);
INSERT INTO sys_role_menu (id, role_id, menu_id, leaf, remark, create_time, create_by, update_time, update_by) VALUES ('90491c71948517607233a5d13c188eb4', '8465885b42ad665e4a92af40faa26b22', '86377ddfa94c3019d8cfb386f0750cbf', null, null, '2022-12-07T00:46:39.190', '30d808cbd7c94c66b4512718151b00aa', null, null);
INSERT INTO sys_role_menu (id, role_id, menu_id, leaf, remark, create_time, create_by, update_time, update_by) VALUES ('c8afcddc431563d78d5965aa66d0d1ff', '8465885b42ad665e4a92af40faa26b22', '5fb69ed214f4d0136b14848f63637a6e', null, null, '2022-12-07T00:46:39.190', '30d808cbd7c94c66b4512718151b00aa', null, null);
INSERT INTO sys_role_menu (id, role_id, menu_id, leaf, remark, create_time, create_by, update_time, update_by) VALUES ('c239afb7ac6498da4f55bc51aefaa7e4', '8465885b42ad665e4a92af40faa26b22', 'e04b7878ba81fc77f83149cf9829f53c', null, null, '2022-12-07T00:46:39.191', '30d808cbd7c94c66b4512718151b00aa', null, null);
INSERT INTO sys_role_menu (id, role_id, menu_id, leaf, remark, create_time, create_by, update_time, update_by) VALUES ('2fbc7863ff9aae9a3d086287a0989207', '8465885b42ad665e4a92af40faa26b22', 'f6acb61109793051f9b370fae3328746', null, null, '2022-12-07T00:46:39.191', '30d808cbd7c94c66b4512718151b00aa', null, null);
INSERT INTO sys_role_menu (id, role_id, menu_id, leaf, remark, create_time, create_by, update_time, update_by) VALUES ('424b7094d5545446d12ddd581001aeb1', '8465885b42ad665e4a92af40faa26b22', 'f3f78186ff53383620c97e8f83ea63b5', null, null, '2022-12-07T00:46:39.192', '30d808cbd7c94c66b4512718151b00aa', null, null);
INSERT INTO sys_role_menu (id, role_id, menu_id, leaf, remark, create_time, create_by, update_time, update_by) VALUES ('067368dabe39241eec274ec82595fa6c', '8465885b42ad665e4a92af40faa26b22', 'a15e56aab7c846e5821c936f070cf7f8', null, null, '2022-12-07T00:46:39.192', '30d808cbd7c94c66b4512718151b00aa', null, null);
INSERT INTO sys_role_menu (id, role_id, menu_id, leaf, remark, create_time, create_by, update_time, update_by) VALUES ('c3521e73ec513f0770cc4d4704c7e894', '8465885b42ad665e4a92af40faa26b22', '222245d7bc4cebe75b82b42c0764b2f5', null, null, '2022-12-07T00:46:39.193', '30d808cbd7c94c66b4512718151b00aa', null, null);
INSERT INTO sys_role_menu (id, role_id, menu_id, leaf, remark, create_time, create_by, update_time, update_by) VALUES ('ca6ac05a9b33066599439981dde24dc3', '8465885b42ad665e4a92af40faa26b22', 'd0a903372029a3904fda5af9af5d80ed', null, null, '2022-12-07T00:46:39.194', '30d808cbd7c94c66b4512718151b00aa', null, null);
INSERT INTO sys_role_menu (id, role_id, menu_id, leaf, remark, create_time, create_by, update_time, update_by) VALUES ('0b86c1d6b48baad82b54d40daada6c88', '8465885b42ad665e4a92af40faa26b22', 'aed2b459cb4b442889cb1684060d2c28', null, null, '2022-12-07T00:46:39.194', '30d808cbd7c94c66b4512718151b00aa', null, null);
INSERT INTO sys_role_menu (id, role_id, menu_id, leaf, remark, create_time, create_by, update_time, update_by) VALUES ('6887591d7dc2a65b66c952a0f927d052', '8465885b42ad665e4a92af40faa26b22', '5424edc5d8aea52b44a2353649f05371', null, null, '2022-12-07T00:46:39.195', '30d808cbd7c94c66b4512718151b00aa', null, null);
INSERT INTO sys_role_menu (id, role_id, menu_id, leaf, remark, create_time, create_by, update_time, update_by) VALUES ('8437b7748dd6d4652c530e98da8e83e6', '8465885b42ad665e4a92af40faa26b22', '4daa0c0d7123ac2bcc26f84abc554cf4', null, null, '2022-12-07T00:46:39.195', '30d808cbd7c94c66b4512718151b00aa', null, null);
INSERT INTO sys_role_menu (id, role_id, menu_id, leaf, remark, create_time, create_by, update_time, update_by) VALUES ('854a12f3fadaff498db3b165ded3277c', '8465885b42ad665e4a92af40faa26b22', 'c20856fedd2f5e979946e5b5669548fc', null, null, '2022-12-07T00:46:39.196', '30d808cbd7c94c66b4512718151b00aa', null, null);
INSERT INTO sys_role_menu (id, role_id, menu_id, leaf, remark, create_time, create_by, update_time, update_by) VALUES ('f50e4772771114b5bea4012428dbf764', '8465885b42ad665e4a92af40faa26b22', '4304d29ab6ccefae5647e65d1369a8b1', null, null, '2022-12-07T00:46:39.196', '30d808cbd7c94c66b4512718151b00aa', null, null);
INSERT INTO sys_role_menu (id, role_id, menu_id, leaf, remark, create_time, create_by, update_time, update_by) VALUES ('5afbbe8ac3d8ad6d775ae0c655501bc8', '8465885b42ad665e4a92af40faa26b22', 'b27aab7847d256306fb226f1bf5a6375', null, null, '2022-12-07T00:46:39.196', '30d808cbd7c94c66b4512718151b00aa', null, null);
INSERT INTO sys_role_menu (id, role_id, menu_id, leaf, remark, create_time, create_by, update_time, update_by) VALUES ('a0af5bf11a17bf7c33beee5445313977', '8465885b42ad665e4a92af40faa26b22', '8827eebc745b8cc33ee8351c07907a9d', null, null, '2022-12-07T00:46:39.197', '30d808cbd7c94c66b4512718151b00aa', null, null);
INSERT INTO sys_role_menu (id, role_id, menu_id, leaf, remark, create_time, create_by, update_time, update_by) VALUES ('9565c4763f10b77caeed9e7a2d787539', '8465885b42ad665e4a92af40faa26b22', '0fbcc757931658d752cfd0e458609c03', null, null, '2022-12-07T00:46:39.197', '30d808cbd7c94c66b4512718151b00aa', null, null);
INSERT INTO sys_role_menu (id, role_id, menu_id, leaf, remark, create_time, create_by, update_time, update_by) VALUES ('09b524bc51245f8ee1ddbaf2c6500724', '8465885b42ad665e4a92af40faa26b22', '32f216795cde422cbce86783f539a912', null, null, '2022-12-07T00:46:39.198', '30d808cbd7c94c66b4512718151b00aa', null, null);
INSERT INTO sys_role_menu (id, role_id, menu_id, leaf, remark, create_time, create_by, update_time, update_by) VALUES ('37aa9917eaa450181cc4938d12d45556', '8465885b42ad665e4a92af40faa26b22', '34ad619f449d46f98735b0621620f295', null, null, '2022-12-07T00:46:39.199', '30d808cbd7c94c66b4512718151b00aa', null, null);

-- menu
DROP TABLE IF EXISTS sys_menu;
CREATE TABLE sys_menu
(
    id             varchar(64) not null primary key,
    p_id           varchar(64)  default NULL,
    order_no       INT          default NULL,
    code           varchar(255) default NULL,
    type           INT          default NULL,
    permissions    varchar(255) default NULL,
    title          varchar(255) default NULL,
    icon           varchar(255) default NULL,
    main           boolean      default NULL,
    resource_url   varchar(255) default NULL,
    hide           boolean      default NULL,
    http_url       boolean      default NULL,
    hide_toolbar   boolean      default NULL,
    http_url_blank boolean      default NULL,
    status         INT          default 0,
    remark         varchar(255) default NULL,
    create_time    datetime     default NULL,
    create_by      varchar(64)  default NULL,
    update_time    datetime     default NULL,
    update_by      varchar(64)  default NULL
);
INSERT INTO sys_menu (id, p_id, order_no, code, type, permissions, title, icon, main, resource_url, hide, http_url, hide_toolbar, http_url_blank, status, remark, create_time, create_by, update_time, update_by) VALUES ('a15e56aab7c846e5821c936f070cf7f8', null, 0, null, 1, null, '主页', 'home', 1, 'com.cxxwl96.jfx.admin.client.view.home.IndexController', 0, 0, 0, 0, 0, null, '2022-09-30T01:13:40.685', '30d808cbd7c94c66b4512718151b00aa', '2022-12-03T23:59:38.707', '30d808cbd7c94c66b4512718151b00aa');
INSERT INTO sys_menu (id, p_id, order_no, code, type, permissions, title, icon, main, resource_url, hide, http_url, hide_toolbar, http_url_blank, status, remark, create_time, create_by, update_time, update_by) VALUES ('aed2b459cb4b442889cb1684060d2c28', null, 0, null, 0, null, '系统管理', 'user-secret', 0, null, 0, 0, 0, 0, 0, null, '2022-09-30T01:13:40.689', '30d808cbd7c94c66b4512718151b00aa', '2022-10-16T17:37:28.338', '30d808cbd7c94c66b4512718151b00aa');
INSERT INTO sys_menu (id, p_id, order_no, code, type, permissions, title, icon, main, resource_url, hide, http_url, hide_toolbar, http_url_blank, status, remark, create_time, create_by, update_time, update_by) VALUES ('32f216795cde422cbce86783f539a912', 'aed2b459cb4b442889cb1684060d2c28', 0, null, 1, null, '用户管理', 'user', 0, 'com.cxxwl96.jfx.admin.client.view.system.user.IndexController', 0, 0, 0, 0, 0, null, '2022-09-30T01:13:40.691', '30d808cbd7c94c66b4512718151b00aa', '2022-10-16T17:38:20.639', '30d808cbd7c94c66b4512718151b00aa');
INSERT INTO sys_menu (id, p_id, order_no, code, type, permissions, title, icon, main, resource_url, hide, http_url, hide_toolbar, http_url_blank, status, remark, create_time, create_by, update_time, update_by) VALUES ('f296adcbfa464ca4900c5d6ca2c796bd', 'aed2b459cb4b442889cb1684060d2c28', 0, null, 1, null, '角色管理', 'group, users', 0, 'com.cxxwl96.jfx.admin.client.view.system.role.IndexController', 0, 0, 0, 0, 0, null, '2022-09-30T01:13:40.692', '30d808cbd7c94c66b4512718151b00aa', '2022-10-16T17:38:36.618', '30d808cbd7c94c66b4512718151b00aa');
INSERT INTO sys_menu (id, p_id, order_no, code, type, permissions, title, icon, main, resource_url, hide, http_url, hide_toolbar, http_url_blank, status, remark, create_time, create_by, update_time, update_by) VALUES ('34ad619f449d46f98735b0621620f295', 'aed2b459cb4b442889cb1684060d2c28', 0, null, 1, null, '菜单权限管理', 'sitemap', 0, 'com.cxxwl96.jfx.admin.client.view.system.menu.IndexController', 0, 0, 0, 0, 0, null, '2022-09-30T01:13:40.694', '30d808cbd7c94c66b4512718151b00aa', '2022-10-16T17:39:22.542', '30d808cbd7c94c66b4512718151b00aa');
INSERT INTO sys_menu (id, p_id, order_no, code, type, permissions, title, icon, main, resource_url, hide, http_url, hide_toolbar, http_url_blank, status, remark, create_time, create_by, update_time, update_by) VALUES ('c20856fedd2f5e979946e5b5669548fc', null, 0, '', 0, '', '设置', 'icon-ic-baseline-settings', 0, '', 0, 0, 0, 0, 0, '', '2022-11-24T23:16:37.561', '30d808cbd7c94c66b4512718151b00aa', null, null);
INSERT INTO sys_menu (id, p_id, order_no, code, type, permissions, title, icon, main, resource_url, hide, http_url, hide_toolbar, http_url_blank, status, remark, create_time, create_by, update_time, update_by) VALUES ('e04b7878ba81fc77f83149cf9829f53c', 'c20856fedd2f5e979946e5b5669548fc', 0, 'UserSetting', 1, null, '个人设置', 'icon-ri-user-settings-line', 0, 'com.cxxwl96.jfx.admin.client.view.setting.user.IndexController', 0, 0, 0, 0, 0, null, '2022-11-24T23:17:06.343', '30d808cbd7c94c66b4512718151b00aa', '2022-11-25T23:30:47.237', '30d808cbd7c94c66b4512718151b00aa');
INSERT INTO sys_menu (id, p_id, order_no, code, type, permissions, title, icon, main, resource_url, hide, http_url, hide_toolbar, http_url_blank, status, remark, create_time, create_by, update_time, update_by) VALUES ('647bc5ed2313bb89cf2e7d3ba06bae00', null, 0, '', 1, '', 'GitHub点个小星星', 'github', 0, 'http://github.com/cxxwl96/jfx-admin', 0, 1, 0, 1, 0, '', '2022-11-25T21:21:14.198', '30d808cbd7c94c66b4512718151b00aa', '2022-11-25T21:23:50.529', '30d808cbd7c94c66b4512718151b00aa');
INSERT INTO sys_menu (id, p_id, order_no, code, type, permissions, title, icon, main, resource_url, hide, http_url, hide_toolbar, http_url_blank, status, remark, create_time, create_by, update_time, update_by) VALUES ('4daa0c0d7123ac2bcc26f84abc554cf4', '32f216795cde422cbce86783f539a912', 0, null, 2, 'user:add', '添加', null, 0, null, 0, 0, 0, 0, 0, null, '2022-12-05T23:48:40.837', '30d808cbd7c94c66b4512718151b00aa', null, null);
INSERT INTO sys_menu (id, p_id, order_no, code, type, permissions, title, icon, main, resource_url, hide, http_url, hide_toolbar, http_url_blank, status, remark, create_time, create_by, update_time, update_by) VALUES ('0fbcc757931658d752cfd0e458609c03', '32f216795cde422cbce86783f539a912', 0, null, 2, 'user:delete', '删除', null, 0, null, 0, 0, 0, 0, 0, null, '2022-12-05T23:49:07.991', '30d808cbd7c94c66b4512718151b00aa', null, null);
INSERT INTO sys_menu (id, p_id, order_no, code, type, permissions, title, icon, main, resource_url, hide, http_url, hide_toolbar, http_url_blank, status, remark, create_time, create_by, update_time, update_by) VALUES ('4c26acf2d94dc59f8c038601f8ca329d', '32f216795cde422cbce86783f539a912', 0, null, 2, 'user:delete:batch', '批量删除', null, 0, null, 0, 0, 0, 0, 0, null, '2022-12-05T23:50:26.140', '30d808cbd7c94c66b4512718151b00aa', null, null);
INSERT INTO sys_menu (id, p_id, order_no, code, type, permissions, title, icon, main, resource_url, hide, http_url, hide_toolbar, http_url_blank, status, remark, create_time, create_by, update_time, update_by) VALUES ('4304d29ab6ccefae5647e65d1369a8b1', '32f216795cde422cbce86783f539a912', 0, null, 2, 'user:edit', '编辑', null, 0, null, 0, 0, 0, 0, 0, null, '2022-12-05T23:50:59.298', '30d808cbd7c94c66b4512718151b00aa', null, null);
INSERT INTO sys_menu (id, p_id, order_no, code, type, permissions, title, icon, main, resource_url, hide, http_url, hide_toolbar, http_url_blank, status, remark, create_time, create_by, update_time, update_by) VALUES ('a488581fb1e79527e69717aa02036c9f', '32f216795cde422cbce86783f539a912', 0, null, 2, 'user:detail', '详情', null, 0, null, 0, 0, 0, 0, 0, null, '2022-12-05T23:51:38.671', '30d808cbd7c94c66b4512718151b00aa', null, null);
INSERT INTO sys_menu (id, p_id, order_no, code, type, permissions, title, icon, main, resource_url, hide, http_url, hide_toolbar, http_url_blank, status, remark, create_time, create_by, update_time, update_by) VALUES ('019ef3592ad0bf0ba3b733110e3c3d62', '32f216795cde422cbce86783f539a912', 0, null, 2, 'user:allocation:roles', '分配角色', null, 0, null, 0, 0, 0, 0, 0, null, '2022-12-05T23:53:19.637', '30d808cbd7c94c66b4512718151b00aa', null, null);
INSERT INTO sys_menu (id, p_id, order_no, code, type, permissions, title, icon, main, resource_url, hide, http_url, hide_toolbar, http_url_blank, status, remark, create_time, create_by, update_time, update_by) VALUES ('5fb69ed214f4d0136b14848f63637a6e', 'f296adcbfa464ca4900c5d6ca2c796bd', 0, null, 2, 'role:add', '添加', null, 0, null, 0, 0, 0, 0, 0, null, '2022-12-05T23:54:34.450', '30d808cbd7c94c66b4512718151b00aa', null, null);
INSERT INTO sys_menu (id, p_id, order_no, code, type, permissions, title, icon, main, resource_url, hide, http_url, hide_toolbar, http_url_blank, status, remark, create_time, create_by, update_time, update_by) VALUES ('352432d53ff714c397f738ce9aef61a3', 'f296adcbfa464ca4900c5d6ca2c796bd', 0, null, 2, 'role:delete', '删除', null, 0, null, 0, 0, 0, 0, 0, null, '2022-12-05T23:54:53.920', '30d808cbd7c94c66b4512718151b00aa', null, null);
INSERT INTO sys_menu (id, p_id, order_no, code, type, permissions, title, icon, main, resource_url, hide, http_url, hide_toolbar, http_url_blank, status, remark, create_time, create_by, update_time, update_by) VALUES ('222245d7bc4cebe75b82b42c0764b2f5', 'f296adcbfa464ca4900c5d6ca2c796bd', 0, null, 2, 'role:delete:batch', '批量删除', null, 0, null, 0, 0, 0, 0, 0, null, '2022-12-05T23:55:21.446', '30d808cbd7c94c66b4512718151b00aa', null, null);
INSERT INTO sys_menu (id, p_id, order_no, code, type, permissions, title, icon, main, resource_url, hide, http_url, hide_toolbar, http_url_blank, status, remark, create_time, create_by, update_time, update_by) VALUES ('95749ef5e258c9ad2972490f2637ed06', 'f296adcbfa464ca4900c5d6ca2c796bd', 0, null, 2, 'role:edit', '编辑', null, 0, null, 0, 0, 0, 0, 0, null, '2022-12-05T23:55:44.205', '30d808cbd7c94c66b4512718151b00aa', null, null);
INSERT INTO sys_menu (id, p_id, order_no, code, type, permissions, title, icon, main, resource_url, hide, http_url, hide_toolbar, http_url_blank, status, remark, create_time, create_by, update_time, update_by) VALUES ('86377ddfa94c3019d8cfb386f0750cbf', 'f296adcbfa464ca4900c5d6ca2c796bd', 0, null, 2, 'role:detail', '详情', null, 0, null, 0, 0, 0, 0, 0, null, '2022-12-05T23:56:06.440', '30d808cbd7c94c66b4512718151b00aa', null, null);
INSERT INTO sys_menu (id, p_id, order_no, code, type, permissions, title, icon, main, resource_url, hide, http_url, hide_toolbar, http_url_blank, status, remark, create_time, create_by, update_time, update_by) VALUES ('71a292a59a51045439152c49b29b3b51', 'f296adcbfa464ca4900c5d6ca2c796bd', 0, null, 2, 'role:allocation:auth', '分配权限', null, 0, null, 0, 0, 0, 0, 0, null, '2022-12-05T23:56:43.199', '30d808cbd7c94c66b4512718151b00aa', null, null);
INSERT INTO sys_menu (id, p_id, order_no, code, type, permissions, title, icon, main, resource_url, hide, http_url, hide_toolbar, http_url_blank, status, remark, create_time, create_by, update_time, update_by) VALUES ('5424edc5d8aea52b44a2353649f05371', '34ad619f449d46f98735b0621620f295', 0, null, 2, 'menu:add', '添加菜单', null, 0, null, 0, 0, 0, 0, 0, null, '2022-12-05T23:57:23.999', '30d808cbd7c94c66b4512718151b00aa', null, null);
INSERT INTO sys_menu (id, p_id, order_no, code, type, permissions, title, icon, main, resource_url, hide, http_url, hide_toolbar, http_url_blank, status, remark, create_time, create_by, update_time, update_by) VALUES ('832d256d9c3b1ef927701e0d1dc47bff', '34ad619f449d46f98735b0621620f295', 0, null, 2, 'menu:edit', '编辑', null, 0, null, 0, 0, 0, 0, 0, null, '2022-12-05T23:57:55.324', '30d808cbd7c94c66b4512718151b00aa', null, null);
INSERT INTO sys_menu (id, p_id, order_no, code, type, permissions, title, icon, main, resource_url, hide, http_url, hide_toolbar, http_url_blank, status, remark, create_time, create_by, update_time, update_by) VALUES ('deaca451cbd84e40e333f014a5c04397', '34ad619f449d46f98735b0621620f295', 0, null, 2, 'menu:delete', '删除', null, 0, null, 0, 0, 0, 0, 0, null, '2022-12-05T23:58:15.150', '30d808cbd7c94c66b4512718151b00aa', null, null);
INSERT INTO sys_menu (id, p_id, order_no, code, type, permissions, title, icon, main, resource_url, hide, http_url, hide_toolbar, http_url_blank, status, remark, create_time, create_by, update_time, update_by) VALUES ('8827eebc745b8cc33ee8351c07907a9d', '34ad619f449d46f98735b0621620f295', 0, null, 2, 'menu:add:submenu', '添加子菜单', null, 0, null, 0, 0, 0, 0, 0, null, '2022-12-05T23:58:38.505', '30d808cbd7c94c66b4512718151b00aa', null, null);
INSERT INTO sys_menu (id, p_id, order_no, code, type, permissions, title, icon, main, resource_url, hide, http_url, hide_toolbar, http_url_blank, status, remark, create_time, create_by, update_time, update_by) VALUES ('f3f78186ff53383620c97e8f83ea63b5', '34ad619f449d46f98735b0621620f295', 0, null, 2, 'menu:delete:submenu', '删除子菜单', null, 0, null, 0, 0, 0, 0, 0, null, '2022-12-05T23:59:09.206', '30d808cbd7c94c66b4512718151b00aa', null, null);
INSERT INTO sys_menu (id, p_id, order_no, code, type, permissions, title, icon, main, resource_url, hide, http_url, hide_toolbar, http_url_blank, status, remark, create_time, create_by, update_time, update_by) VALUES ('f6acb61109793051f9b370fae3328746', 'e04b7878ba81fc77f83149cf9829f53c', 0, null, 2, 'usersetting:base', '基本设置', null, 0, null, 0, 0, 0, 0, 0, null, '2022-12-06T00:01:10.286', '30d808cbd7c94c66b4512718151b00aa', null, null);
INSERT INTO sys_menu (id, p_id, order_no, code, type, permissions, title, icon, main, resource_url, hide, http_url, hide_toolbar, http_url_blank, status, remark, create_time, create_by, update_time, update_by) VALUES ('d0a903372029a3904fda5af9af5d80ed', 'e04b7878ba81fc77f83149cf9829f53c', 0, null, 2, 'usersetting:security', '安全设置', null, 0, null, 0, 0, 0, 0, 0, null, '2022-12-06T00:01:52.414', '30d808cbd7c94c66b4512718151b00aa', null, null);
INSERT INTO sys_menu (id, p_id, order_no, code, type, permissions, title, icon, main, resource_url, hide, http_url, hide_toolbar, http_url_blank, status, remark, create_time, create_by, update_time, update_by) VALUES ('b27aab7847d256306fb226f1bf5a6375', 'e04b7878ba81fc77f83149cf9829f53c', 0, null, 2, 'usersetting:myroles', '我的角色', null, 0, null, 0, 0, 0, 0, 0, null, '2022-12-06T00:02:16.981', '30d808cbd7c94c66b4512718151b00aa', null, null);