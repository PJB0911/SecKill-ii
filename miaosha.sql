/*
Navicat MySQL Data Transfer

Source Server         : localhost_3306
Source Server Version : 50730
Source Host           : localhost:3306
Source Database       : miaosha

Target Server Type    : MYSQL
Target Server Version : 50730
File Encoding         : 65001

Date: 2020-08-02 22:44:41
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for item
-- ----------------------------
DROP TABLE IF EXISTS `item`;
CREATE TABLE `item` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(64) NOT NULL DEFAULT '',
  `price` double(10,0) NOT NULL DEFAULT '0',
  `description` varchar(255) NOT NULL DEFAULT '',
  `sales` int(11) NOT NULL DEFAULT '0',
  `img_url` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of item
-- ----------------------------
INSERT INTO `item` VALUES ('1', 'iphone99', '3000', '最好用的手机', '1', 'https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=1053528658,1840018132&fm=26&gp=0.jpg');
INSERT INTO `item` VALUES ('2', '华为荣耀', '2000', '最好用的国产手机', '0', 'https://ss1.baidu.com/9vo3dSag_xI4khGko9WTAnF6hhy/image/h%3D300/sign=bd76ebc949df8db1a32e7a643923dddb/0ff41bd5ad6eddc4b0cc5eaa2edbb6fd526633bc.jpg');
INSERT INTO `item` VALUES ('3', 'OPPO', '1000', '一般般的oppo', '2', 'https://ss0.baidu.com/94o3dSag_xI4khGko9WTAnF6hhy/image/h%3D300/sign=bc384f4280dda144c5096ab282b6d009/dc54564e9258d109033142e3c658ccbf6d814d95.jpg');
INSERT INTO `item` VALUES ('4', '猪肉脯', '50', '最好吃的猪肉脯', '5', 'https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=1693993701,2241546290&fm=26&gp=0.jpg');

-- ----------------------------
-- Table structure for item_stock
-- ----------------------------
DROP TABLE IF EXISTS `item_stock`;
CREATE TABLE `item_stock` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `stock` int(11) NOT NULL DEFAULT '0',
  `item_id` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of item_stock
-- ----------------------------
INSERT INTO `item_stock` VALUES ('1', '99', '1');
INSERT INTO `item_stock` VALUES ('2', '100', '2');
INSERT INTO `item_stock` VALUES ('3', '48', '3');
INSERT INTO `item_stock` VALUES ('4', '995', '4');

-- ----------------------------
-- Table structure for order_info
-- ----------------------------
DROP TABLE IF EXISTS `order_info`;
CREATE TABLE `order_info` (
  `id` varchar(32) NOT NULL,
  `user_id` int(11) NOT NULL DEFAULT '0',
  `item_id` int(11) NOT NULL DEFAULT '0',
  `item_price` double NOT NULL DEFAULT '0',
  `amount` int(11) NOT NULL DEFAULT '0',
  `order_price` double NOT NULL DEFAULT '0',
  `promo_id` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of order_info
-- ----------------------------
INSERT INTO `order_info` VALUES ('2020080200000000', '1', '1', '3000', '1', '3000', '0');
INSERT INTO `order_info` VALUES ('2020080200000100', '1', '23', '50', '1', '50', '0');
INSERT INTO `order_info` VALUES ('2020080200000200', '1', '3', '1000', '1', '1000', '0');
INSERT INTO `order_info` VALUES ('2020080200000300', '1', '3', '1000', '1', '1000', '0');
INSERT INTO `order_info` VALUES ('2020080200000400', '1', '4', '9.99', '1', '9.99', '3');
INSERT INTO `order_info` VALUES ('2020080200000500', '1', '4', '9.99', '1', '9.99', '3');
INSERT INTO `order_info` VALUES ('2020080200000600', '1', '4', '9.99', '1', '9.99', '3');
INSERT INTO `order_info` VALUES ('2020080200000700', '1', '4', '9.99', '1', '9.99', '3');

-- ----------------------------
-- Table structure for promo
-- ----------------------------
DROP TABLE IF EXISTS `promo`;
CREATE TABLE `promo` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `promo_name` varchar(255) NOT NULL DEFAULT '',
  `start_date` datetime NOT NULL,
  `end_date` datetime NOT NULL,
  `item_id` int(11) NOT NULL DEFAULT '0',
  `promo_item_price` double NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of promo
-- ----------------------------
INSERT INTO `promo` VALUES ('1', 'iphone 抢购', '2020-08-03 23:59:59', '2020-08-04 23:59:59', '1', '2999');
INSERT INTO `promo` VALUES ('2', '华为荣耀抢购', '2020-08-04 23:59:59', '2020-08-05 23:59:59', '2', '1500');
INSERT INTO `promo` VALUES ('3', '猪肉脯大抢购！！！', '2020-08-02 22:09:30', '2020-08-06 23:59:59', '4', '9.99');

-- ----------------------------
-- Table structure for sequence_info
-- ----------------------------
DROP TABLE IF EXISTS `sequence_info`;
CREATE TABLE `sequence_info` (
  `name` varchar(255) NOT NULL DEFAULT '',
  `current_value` int(11) NOT NULL DEFAULT '0',
  `step` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sequence_info
-- ----------------------------
INSERT INTO `sequence_info` VALUES ('order_info', '8', '1');

-- ----------------------------
-- Table structure for user_info
-- ----------------------------
DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL DEFAULT '',
  `gender` tinyint(4) NOT NULL DEFAULT '0',
  `age` int(11) NOT NULL DEFAULT '0',
  `telphone` varchar(255) NOT NULL DEFAULT '',
  `register_mode` varchar(255) NOT NULL DEFAULT '',
  `third_party_id` varchar(64) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),
  UNIQUE KEY `telphone_unique_index` (`telphone`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user_info
-- ----------------------------
INSERT INTO `user_info` VALUES ('1', 'PJB', '1', '26', '17816855436', 'byphone', '');
INSERT INTO `user_info` VALUES ('2', 'WD', '1', '1', '1338979210', 'byphone', '');
INSERT INTO `user_info` VALUES ('3', 'FUCK', '1', '13', '123', 'byphone', '');
INSERT INTO `user_info` VALUES ('4', 'MOMO', '0', '27', '111', 'byphone', '');

-- ----------------------------
-- Table structure for user_password
-- ----------------------------
DROP TABLE IF EXISTS `user_password`;
CREATE TABLE `user_password` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `encrpt_password` varchar(128) NOT NULL DEFAULT '',
  `user_id` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user_password
-- ----------------------------
INSERT INTO `user_password` VALUES ('1', 'ICy5YqxZB1uWSwcVLSNLcA==', '1');
INSERT INTO `user_password` VALUES ('2', 'xMpCOKC5I4INzFCab3WEmw==', '2');
INSERT INTO `user_password` VALUES ('3', 'ICy5YqxZB1uWSwcVLSNLcA==', '3');
INSERT INTO `user_password` VALUES ('4', 'ICy5YqxZB1uWSwcVLSNLcA==', '4');
