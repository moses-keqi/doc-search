/*
 Navicat Premium Data Transfer

 Source Server         : mysql8
 Source Server Type    : MySQL
 Source Server Version : 80011
 Source Host           : localhost
 Source Database       : ytds

 Target Server Type    : MySQL
 Target Server Version : 80011
 File Encoding         : utf-8

 Date: 07/04/2018 15:24:18 PM
*/

SET NAMES utf8;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
--  Table structure for `tb_doc`
-- ----------------------------
DROP TABLE IF EXISTS `tb_doc`;
CREATE TABLE `tb_doc` (
  `doc_id` int(11) NOT NULL AUTO_INCREMENT,
  `doc_name` varchar(512) DEFAULT NULL,
  `doc_size` bigint(20) DEFAULT NULL,
  `doc_sha256` varchar(256) DEFAULT NULL,
  `doc_create_date` datetime DEFAULT NULL,
  `doc_user_id` int(11) DEFAULT NULL,
  `doc_file_id` varchar(128) DEFAULT NULL,
  `doc_open` smallint(6) DEFAULT NULL,
  `doc_type` varchar(128) DEFAULT NULL,
  `doc_title` varchar(512) DEFAULT NULL,
  `doc_content` longtext,
  `doc_delete` smallint(6) DEFAULT NULL,
  `doc_modify_date` datetime DEFAULT NULL,
  `doc_index` tinyint(4) DEFAULT NULL,
  `source` varchar(64) DEFAULT NULL,
  `source_url` varchar(1024) DEFAULT NULL,
  `doc_status` tinyint(4) DEFAULT '0',
  `doc_convert` tinyint(4) DEFAULT '0',
  PRIMARY KEY (`doc_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `tb_doc_draft`
-- ----------------------------
DROP TABLE IF EXISTS `tb_doc_draft`;
CREATE TABLE `tb_doc_draft` (
  `draft_doc_id` int(11) NOT NULL COMMENT '草稿文档id',
  `draft_save_date` datetime NOT NULL COMMENT '草稿保存时间',
  `draft_doc_content` longtext COMMENT '草稿文档内容',
  `draft_doc_title` varchar(512) DEFAULT NULL COMMENT '草稿文档标题',
  PRIMARY KEY (`draft_doc_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `tb_doc_recycle`
-- ----------------------------
DROP TABLE IF EXISTS `tb_doc_recycle`;
CREATE TABLE `tb_doc_recycle` (
  `recycle_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '回收id',
  `doc_id` int(11) NOT NULL COMMENT '文档id',
  `recycle_date` datetime NOT NULL COMMENT '回收日期',
  PRIMARY KEY (`recycle_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `tb_download_record`
-- ----------------------------
DROP TABLE IF EXISTS `tb_download_record`;
CREATE TABLE `tb_download_record` (
  `download_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '下载id',
  `download_user_id` int(11) NOT NULL COMMENT '下载人',
  `download_date` datetime NOT NULL COMMENT '下载日期',
  `doc_id` int(11) NOT NULL COMMENT '下载的文档id',
  PRIMARY KEY (`download_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `tb_evernote_import`
-- ----------------------------
DROP TABLE IF EXISTS `tb_evernote_import`;
CREATE TABLE `tb_evernote_import` (
  `evernote_id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `import_date` datetime DEFAULT NULL COMMENT '导入日期',
  `success_size` int(11) DEFAULT NULL COMMENT '成功条数',
  `user_id` int(11) DEFAULT NULL COMMENT '用户id',
  `import_status` tinyint(4) DEFAULT NULL COMMENT '导入状态,0表示等待导入，1表示导入中，2表示导入成功，3表示导入失败',
  `evernote_file_id` varchar(64) DEFAULT NULL COMMENT '印象笔记文件id',
  `import_result` varchar(512) DEFAULT NULL COMMENT '导入结果，中文描述',
  PRIMARY KEY (`evernote_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `tb_login_log`
-- ----------------------------
DROP TABLE IF EXISTS `tb_login_log`;
CREATE TABLE `tb_login_log` (
  `login_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '登录id',
  `user_id` int(11) NOT NULL COMMENT '用户id',
  `login_ip` varchar(128) NOT NULL COMMENT '登录ip',
  `login_date` datetime NOT NULL COMMENT '登录日期',
  `login_from` varchar(1024) DEFAULT NULL COMMENT '登录来源',
  PRIMARY KEY (`login_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `tb_register_mail`
-- ----------------------------
DROP TABLE IF EXISTS `tb_register_mail`;
CREATE TABLE `tb_register_mail` (
  `register_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '注册id',
  `email` varchar(255) NOT NULL COMMENT '注册邮箱',
  `code` varchar(8) NOT NULL COMMENT '注册代码',
  `send_date` datetime NOT NULL COMMENT '发送时间',
  PRIMARY KEY (`register_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `tb_reset_mail`
-- ----------------------------
DROP TABLE IF EXISTS `tb_reset_mail`;
CREATE TABLE `tb_reset_mail` (
  `reset_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '重置id',
  `email` varchar(255) NOT NULL COMMENT '重置邮箱',
  `code` varchar(8) NOT NULL COMMENT '重置密码代码',
  `send_date` datetime NOT NULL COMMENT '发送时间',
  PRIMARY KEY (`reset_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `tb_search_record`
-- ----------------------------
DROP TABLE IF EXISTS `tb_search_record`;
CREATE TABLE `tb_search_record` (
  `search_record_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '搜索id',
  `search_keyword` varchar(256) NOT NULL COMMENT '搜索关键词',
  `doc_id` int(11) NOT NULL COMMENT '搜索后阅读了哪篇文档',
  `user_id` int(11) NOT NULL COMMENT '搜索人',
  PRIMARY KEY (`search_record_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `tb_share_doc`
-- ----------------------------
DROP TABLE IF EXISTS `tb_share_doc`;
CREATE TABLE `tb_share_doc` (
  `share_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '分享id',
  `share_to` int(11) NOT NULL COMMENT '分享给哪个用户',
  `share_date` datetime NOT NULL COMMENT '分享日期',
  `doc_id` int(11) NOT NULL COMMENT '文档id',
  `valid_time` int(11) NOT NULL DEFAULT '0' COMMENT '文档有效期，单位秒，0表示无限',
  PRIMARY KEY (`share_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `tb_upload_file`
-- ----------------------------
DROP TABLE IF EXISTS `tb_upload_file`;
CREATE TABLE `tb_upload_file` (
  `file_id` varchar(64) NOT NULL,
  `file_path` varchar(512) DEFAULT NULL,
  `sha256` varchar(255) DEFAULT NULL,
  `file_size` int(12) DEFAULT NULL,
  `file_type` varchar(256) DEFAULT NULL,
  `file_desc` varchar(1024) DEFAULT NULL,
  `file_name` varchar(1024) DEFAULT NULL,
  `original_file_name` varchar(1024) DEFAULT NULL,
  PRIMARY KEY (`file_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
--  Table structure for `tb_user`
-- ----------------------------
DROP TABLE IF EXISTS `tb_user`;
CREATE TABLE `tb_user` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户id',
  `user_name` varchar(128) NOT NULL COMMENT '用户名',
  `user_pwd` varchar(256) NOT NULL COMMENT 'sha256来哈希用户密码（not use md5）sha256(salt+pwd)',
  `user_mail` varchar(256) NOT NULL COMMENT '用户邮箱',
  `register_date` datetime NOT NULL COMMENT '用户注册日期',
  `user_token` varchar(64) DEFAULT NULL COMMENT '用户token',
  `user_salt` varchar(64) NOT NULL COMMENT '加盐',
  `nick_name` varchar(64) DEFAULT '' COMMENT '用户昵称',
  `portrait_id` varchar(64) DEFAULT '' COMMENT '用户头像文件id',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

BEGIN;
INSERT INTO `tb_user` VALUES ('1', 'admin', '538b354a34e14fa792f011e18329d7af1b25a71f547087ab6cb5341e2feffb5d', '110@qq.com', '2018-06-28 02:08:19', '16f9ddf2d1d9499f8001b18639807b95', '9ed5800504f04da78b813047698bc77f', 'yt520', '');
COMMIT;
SET FOREIGN_KEY_CHECKS = 1;
