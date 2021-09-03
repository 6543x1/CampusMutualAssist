-- MySQL dump 10.13  Distrib 5.7.32, for Win64 (x86_64)
--
-- Host: localhost    Database: classaffairsmanage
-- ------------------------------------------------------
-- Server version	5.7.32-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `admin_operation`
--

DROP TABLE IF EXISTS `admin_operation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `admin_operation`
(
    `operator`     varchar(100) NOT NULL,
    `operation`    varchar(100) NOT NULL,
    `targetUser`   varchar(100) NOT NULL,
    `operatedTime` datetime     NOT NULL,
    `reason`       varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin_operation`
--

LOCK
TABLES `admin_operation` WRITE;
/*!40000 ALTER TABLE `admin_operation` DISABLE KEYS */;
/*!40000 ALTER TABLE `admin_operation` ENABLE KEYS */;
UNLOCK
TABLES;

--
-- Table structure for table `files`
--

DROP TABLE IF EXISTS `files`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `files`
(
    `fid`        int(10) unsigned NOT NULL AUTO_INCREMENT,
    `name`       varchar(70)  NOT NULL,
    `path`       varchar(100) NOT NULL,
    `hash`       varchar(64) DEFAULT NULL,
    `classID`    varchar(6)  DEFAULT NULL,
    `type`       varchar(20) DEFAULT NULL,
    `username`   varchar(20)  NOT NULL,
    `uploadTime` datetime     NOT NULL,
    `fastUpload` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否快传',
    PRIMARY KEY (`fid`),
    KEY          `files_FK` (`username`),
    KEY          `files_FK_1` (`classID`),
    CONSTRAINT `files_FK` FOREIGN KEY (`username`) REFERENCES `user` (`username`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `files_FK_1` FOREIGN KEY (`classID`) REFERENCES `teaching_class` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `files`
--

LOCK
TABLES `files` WRITE;
/*!40000 ALTER TABLE `files` DISABLE KEYS */;
INSERT INTO `files`
VALUES (1, 'test2.xlsx', 'D:/camProject/', 'c92923536c7d77c4aa8c72e1af32eb9be3632fc9a4cc5fe989ed035a54b20b63', 'CIRD9F',
        '其他', 'student3', '2021-08-18 00:00:00', 0),
       (2, '大学生网球运动体能训练方法探析_刘颖平.pdf', 'D:/camFiles/CIRD9F/',
        '3fdc2c6d34612669fbdd6cb87770f8491ac833aea6e7e359609ccc8941c86f16', 'CIRD9F', '其他', 'student3',
        '2021-08-25 12:44:40', 0);
/*!40000 ALTER TABLE `files` ENABLE KEYS */;
UNLOCK
TABLES;

--
-- Table structure for table `notice`
--

DROP TABLE IF EXISTS `notice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `notice`
(
    `nid`           int(11) NOT NULL AUTO_INCREMENT,
    `title`         varchar(20)  NOT NULL,
    `body`          varchar(500) NOT NULL,
    `classID`       varchar(6)   NOT NULL,
    `publishedTime` datetime     NOT NULL,
    `publisher`     varchar(20)  NOT NULL,
    `deadLine`      datetime    DEFAULT NULL,
    `type`          varchar(10) DEFAULT NULL COMMENT 'classification太长了',
    `isPublic`      tinyint(1) NOT NULL DEFAULT '1',
    `confirm`       tinyint(1) NOT NULL DEFAULT '0',
    PRIMARY KEY (`nid`),
    KEY             `notice_FK` (`classID`),
    KEY             `notice_FK_1` (`publisher`),
    CONSTRAINT `notice_FK` FOREIGN KEY (`classID`) REFERENCES `teaching_class` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `notice_FK_1` FOREIGN KEY (`publisher`) REFERENCES `user` (`username`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notice`
--

LOCK
TABLES `notice` WRITE;
/*!40000 ALTER TABLE `notice` DISABLE KEYS */;
INSERT INTO `notice`
VALUES (1, '这是一个公告的标题', '欢迎来到新班级，这是本班级的公告处。', 'CIRD9F', '2021-07-25 13:27:37', 'teacher1', NULL, NULL, 1, 0),
       (2, '这是第二个公告的标题', '好康，是新公告哦？', 'CIRD9F', '2021-08-05 01:39:58', 'teacher1', NULL, NULL, 1, 0),
       (3, '这是第3个公告的标题', '我看你，是完全不懂哦？', 'CIRD9F', '2021-08-05 01:49:12', 'teacher1', NULL, NULL, 1, 0),
       (4, '这是第4个公告的标题', '同学们注意！我要变形了！', 'CIRD9F', '2021-08-05 01:57:50', 'teacher1', NULL, NULL, 1, 0),
       (5, '这是第1个公告的标题', '欢迎来到新班级，这是本班级的公告处。', '25VEO4', '2021-08-05 01:59:54', 'teacher1', NULL, NULL, 1, 0),
       (6, '本公告旨在测试缓存能否清空', '清空所有已缓存的页面', 'CIRD9F', '2021-08-10 00:57:01', 'teacher1', NULL, NULL, 1, 0),
       (7, '本公告旨在测试缓存能否清空', '清空所有已缓存的页面', 'CIRD9F', '2021-08-12 16:46:58', 'teacher1', '2021-08-12 16:46:00', NULL, 1,
        0),
       (8, '这条部分人不可见', '部分人不可见', 'CIRD9F', '2021-08-21 00:09:19', 'teacher1', NULL, '其他', 0, 0),
       (9, '这条部分人不可见', '部分人不可见', 'CIRD9F', '2021-08-21 20:01:12', 'teacher1', NULL, '其他', 0, 0),
       (10, '本公告：11111', '我是内容1111', 'CIRD9F', '2021-08-25 11:35:05', 'teacher1', '2021-08-27 16:46:00', NULL, 0, 0),
       (11, '本公告：11111', '我是内容1111', 'CIRD9F', '2021-08-25 11:37:26', 'teacher1', '2021-08-27 16:46:00', NULL, 0, 0),
       (12, '本公告：11111', '我是内容1111', 'CIRD9F', '2021-08-25 11:37:47', 'teacher1', '2021-08-27 16:46:00', NULL, 1, 0),
       (13, '本公告：22222', '我是内容1111', 'CIRD9F', '2021-08-26 21:59:14', 'teacher1', '2021-08-27 16:46:00', NULL, 1, 1),
       (14, '本公告：22222', '我是内容1111', 'CIRD9F', '2021-08-26 22:54:11', 'teacher1', '2021-08-27 16:46:00', NULL, 1, 1),
       (15, '卧槽，宾！', '卧槽！兄弟们抽！WCNM!', 'CIRD9F', '2021-08-29 22:48:35', 'teacher1', '2021-08-31 16:46:00', NULL, 1, 1),
       (16, '一波一波！', '小花生在干你妈呢?', 'CIRD9F', '2021-08-29 22:51:39', 'teacher1', '2021-08-31 16:46:00', NULL, 1, 1),
       (17, '我要骂人', '我干死你的妈', 'CIRD9F', '2021-08-29 23:00:21', 'teacher1', '2021-08-31 16:46:00', NULL, 1, 1),
       (18, '我要骂死你', '我操你妈抽抽抽', 'CIRD9F', '2021-08-29 23:18:41', 'teacher1', '2021-08-31 16:47:00', NULL, 1, 1),
       (19, '我要骂死你', '我***，来啊来屏蔽', 'CIRD9F', '2021-08-29 23:23:30', 'teacher1', '2021-08-31 16:47:00', NULL, 1, 1);
/*!40000 ALTER TABLE `notice` ENABLE KEYS */;
UNLOCK
TABLES;

--
-- Table structure for table `notice_confirmers`
--

DROP TABLE IF EXISTS `notice_confirmers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `notice_confirmers`
(
    `nid`        int(11) NOT NULL,
    `confirmers` json NOT NULL,
    PRIMARY KEY (`nid`),
    CONSTRAINT `notice_confirmers_FK` FOREIGN KEY (`nid`) REFERENCES `notice` (`nid`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用于持久化地保存原本在Redis中的确认数据';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notice_confirmers`
--

LOCK
TABLES `notice_confirmers` WRITE;
/*!40000 ALTER TABLE `notice_confirmers` DISABLE KEYS */;
/*!40000 ALTER TABLE `notice_confirmers` ENABLE KEYS */;
UNLOCK
TABLES;

--
-- Table structure for table `notice_files`
--

DROP TABLE IF EXISTS `notice_files`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `notice_files`
(
    `nid` int(11) NOT NULL,
    `fid` int(10) unsigned NOT NULL,
    KEY   `notice_files_FK` (`fid`),
    KEY   `notice_files_FK_1` (`nid`),
    CONSTRAINT `notice_files_FK` FOREIGN KEY (`fid`) REFERENCES `files` (`fid`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `notice_files_FK_1` FOREIGN KEY (`nid`) REFERENCES `notice` (`nid`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notice_files`
--

LOCK
TABLES `notice_files` WRITE;
/*!40000 ALTER TABLE `notice_files` DISABLE KEYS */;
INSERT INTO `notice_files`
VALUES (19, 1),
       (18, 1),
       (18, 2);
/*!40000 ALTER TABLE `notice_files` ENABLE KEYS */;
UNLOCK
TABLES;

--
-- Table structure for table `notice_permission`
--

DROP TABLE IF EXISTS `notice_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `notice_permission`
(
    `nid`    int(11) NOT NULL,
    `reader` varchar(20) CHARACTER SET utf8 NOT NULL,
    PRIMARY KEY (`reader`, `nid`),
    KEY      `notice_permission_FK_1` (`nid`),
    CONSTRAINT `notice_permission_FK` FOREIGN KEY (`reader`) REFERENCES `user` (`username`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `notice_permission_FK_1` FOREIGN KEY (`nid`) REFERENCES `notice` (`nid`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notice_permission`
--

LOCK
TABLES `notice_permission` WRITE;
/*!40000 ALTER TABLE `notice_permission` DISABLE KEYS */;
INSERT INTO `notice_permission`
VALUES (1, 'student3'),
       (8, 'student2'),
       (8, 'student3'),
       (9, 'student2'),
       (9, 'student3');
/*!40000 ALTER TABLE `notice_permission` ENABLE KEYS */;
UNLOCK
TABLES;

--
-- Table structure for table `signin`
--

DROP TABLE IF EXISTS `signin`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `signin`
(
    `signID`        int(10) unsigned NOT NULL AUTO_INCREMENT,
    `title`         varchar(30)                   NOT NULL,
    `signKey`       varchar(20)                   NOT NULL,
    `classID`       varchar(6) CHARACTER SET utf8 NOT NULL,
    `signedNum`     int(11) DEFAULT NULL,
    `totalNum`      int(11) DEFAULT NULL,
    `deadLine`      datetime                      NOT NULL,
    `signType`      enum('normal','key') NOT NULL,
    `publishedTime` datetime                      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `publisher`     varchar(20)                            DEFAULT NULL,
    PRIMARY KEY (`signID`),
    KEY             `signin_FK` (`classID`),
    CONSTRAINT `signin_FK` FOREIGN KEY (`classID`) REFERENCES `teaching_class` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `signin`
--

LOCK
TABLES `signin` WRITE;
/*!40000 ALTER TABLE `signin` DISABLE KEYS */;
INSERT INTO `signin`
VALUES (1, '默认签到', '1593', 'CIRD9F', NULL, NULL, '2021-08-16 11:32:27', 'normal', '2021-08-25 19:14:51', 'teacher1'),
       (2, '默认签到66621', '66621', 'CIRD9F', NULL, NULL, '2021-08-27 16:58:17', 'key', '2021-08-27 16:55:12', 'teacher1');
/*!40000 ALTER TABLE `signin` ENABLE KEYS */;
UNLOCK
TABLES;

--
-- Table structure for table `signin_signed`
--

DROP TABLE IF EXISTS `signin_signed`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `signin_signed`
(
    `signID` int(10) unsigned NOT NULL,
    `signed` json NOT NULL,
    PRIMARY KEY (`signID`),
    CONSTRAINT `signIn_signed_FK` FOREIGN KEY (`signID`) REFERENCES `signin` (`signID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='持久化签到信息保存到数据库（为什么不是signer呢？因为signer意思不太对）';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `signin_signed`
--

LOCK
TABLES `signin_signed` WRITE;
/*!40000 ALTER TABLE `signin_signed` DISABLE KEYS */;
/*!40000 ALTER TABLE `signin_signed` ENABLE KEYS */;
UNLOCK
TABLES;

--
-- Table structure for table `stu_points_detail`
--

DROP TABLE IF EXISTS `stu_points_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `stu_points_detail`
(
    `id`       int(10) unsigned NOT NULL AUTO_INCREMENT,
    `classID`  varchar(6)  NOT NULL,
    `operator` varchar(20) NOT NULL,
    `target`   varchar(20) NOT NULL,
    `points`   int(11) NOT NULL,
    `reason`   varchar(50) NOT NULL,
    `time`     datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY        `stu_points_detail_FK` (`operator`),
    KEY        `stu_points_detail_FK_1` (`target`),
    KEY        `stu_points_detail_FK_2` (`classID`),
    CONSTRAINT `stu_points_detail_FK` FOREIGN KEY (`operator`) REFERENCES `user` (`username`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `stu_points_detail_FK_1` FOREIGN KEY (`target`) REFERENCES `user` (`username`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `stu_points_detail_FK_2` FOREIGN KEY (`classID`) REFERENCES `teaching_class` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stu_points_detail`
--

LOCK
TABLES `stu_points_detail` WRITE;
/*!40000 ALTER TABLE `stu_points_detail` DISABLE KEYS */;
INSERT INTO `stu_points_detail`
VALUES (9, 'CIRD9F', 'teacher1', 'student3', 1, '测试加分(AOP)', '2021-08-31 22:02:25');
/*!40000 ALTER TABLE `stu_points_detail` ENABLE KEYS */;
UNLOCK
TABLES;

--
-- Table structure for table `stu_selection`
--

DROP TABLE IF EXISTS `stu_selection`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `stu_selection`
(
    `stuName` varchar(20) NOT NULL,
    `classID` varchar(6)  NOT NULL,
    `scores`  int(11) NOT NULL,
    PRIMARY KEY (`stuName`, `classID`),
    KEY       `stu_selection_FK_1` (`classID`),
    CONSTRAINT `stu_selection_FK` FOREIGN KEY (`stuName`) REFERENCES `user` (`username`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `stu_selection_FK_1` FOREIGN KEY (`classID`) REFERENCES `teaching_class` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stu_selection`
--

LOCK
TABLES `stu_selection` WRITE;
/*!40000 ALTER TABLE `stu_selection` DISABLE KEYS */;
INSERT INTO `stu_selection`
VALUES ('student1', 'CIRD9F', 0),
       ('student2', 'CIRD9F', 0),
       ('student3', '25VEO4', 0),
       ('student3', 'CIRD9F', 0);
/*!40000 ALTER TABLE `stu_selection` ENABLE KEYS */;
UNLOCK
TABLES;

--
-- Table structure for table `student_points`
--

DROP TABLE IF EXISTS `student_points`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `student_points`
(
    `username` varchar(20) NOT NULL,
    `classID`  varchar(6)  NOT NULL,
    `points`   int(11) NOT NULL,
    PRIMARY KEY (`username`, `classID`),
    KEY        `student_points_FK_1` (`classID`),
    CONSTRAINT `student_points_FK` FOREIGN KEY (`username`) REFERENCES `user` (`username`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `student_points_FK_1` FOREIGN KEY (`classID`) REFERENCES `teaching_class` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student_points`
--

LOCK
TABLES `student_points` WRITE;
/*!40000 ALTER TABLE `student_points` DISABLE KEYS */;
INSERT INTO `student_points`
VALUES ('student1', 'CIRD9F', 60),
       ('student2', 'CIRD9F', 60),
       ('student3', 'CIRD9F', 61),
       ('teacher1', 'CIRD9F', 60);
/*!40000 ALTER TABLE `student_points` ENABLE KEYS */;
UNLOCK
TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER trigger_pointsLimit
before update 
ON student_points FOR EACH ROW
begin 
	if new.points > 100
	then 
		set new.points = 100;
	end if;
end */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `teaching_class`
--

DROP TABLE IF EXISTS `teaching_class`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `teaching_class`
(
    `name`    varchar(20) NOT NULL,
    `teacher` varchar(20) NOT NULL,
    `id`      varchar(6)  NOT NULL,
    PRIMARY KEY (`id`),
    KEY       `teaching_class_FK` (`teacher`),
    CONSTRAINT `teaching_class_FK` FOREIGN KEY (`teacher`) REFERENCES `user` (`username`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `teaching_class`
--

LOCK
TABLES `teaching_class` WRITE;
/*!40000 ALTER TABLE `teaching_class` DISABLE KEYS */;
INSERT INTO `teaching_class`
VALUES ('测试用班级2', 'teacher1', '25VEO4'),
       ('测试用班级', 'teacher1', 'CIRD9F');
/*!40000 ALTER TABLE `teaching_class` ENABLE KEYS */;
UNLOCK
TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user`
(
    `username`     varchar(20) CHARACTER SET utf8  NOT NULL,
    `password`     varchar(100) CHARACTER SET utf8 NOT NULL,
    `uid`          int(11) NOT NULL AUTO_INCREMENT,
    `mailAddr`     varchar(20) CHARACTER SET utf8  DEFAULT NULL,
    `realName`     varchar(15) CHARACTER SET utf8  NOT NULL,
    `status`       int(11) NOT NULL COMMENT '还没决定好干啥',
    `role`         enum('admin','student','teacher','instructor') CHARACTER SET utf8 NOT NULL,
    `img_path`     varchar(100) CHARACTER SET utf8 DEFAULT NULL,
    `evaluation`   int(11) NOT NULL DEFAULT '4',
    `mobileNumber` varchar(11) CHARACTER SET utf8  DEFAULT '0',
    PRIMARY KEY (`username`),
    UNIQUE KEY `user_uid_IDX` (`uid`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK
TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user`
VALUES ('031902410', '$2a$10$FHkjHhr0DQouexagDfvBJOlfgyIPqfcfFUpQDoJlCcpTAWQO.7H9q', 7, NULL, 'LJN', 1, 'student', NULL,
        2, '0'),
       ('Jessie', '$2a$10$y8ppQ.ooPaE8lEFTvFDGaO0rcwu9Yr9ijCf/m4BQ5mBovMAR23YoG', 1, NULL, 'ljn', 10, 'admin', NULL, 2,
        ''),
       ('student1', '$2a$10$lSZcOhM3gQigNA1PRLWXT.iw9Er6vHR4dHzOJk7FedyB1QXvrsIQG', 4, NULL, 's1', 10, 'student', NULL,
        2, ''),
       ('student2', '$2a$10$usKIdDeHt1MxW.Wj0OSb8us6bsuCBWfcjCiIXGA9SSQrEG2IeFA1a', 5, NULL, 's2', 10, 'student', NULL,
        2, ''),
       ('student3', '$2a$10$mfZsHDgygKo83HXYMqdqpe8nZqgM1aKBcfSCu2zsgvow4X0aSi.M6', 6, NULL, 's3', 10, 'student', NULL,
        2, ''),
       ('teacher1', '$2a$10$izYs63Q96NYItVbEE2sEdOcotR/Q1Ev3erK9P6UOtS2L/HI/8juHG', 3, NULL, 't1', 10, 'teacher', NULL,
        2, ''),
       ('user1', '$2a$10$zGD5vvVACOicz4tDjW58Oe3apOEVNa9z8LWkjcaPCDhWidXXaX7ua', 2, NULL, 'u1', 10, 'student', NULL, 2,
        '');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK
TABLES;

--
-- Table structure for table `user_permission`
--

DROP TABLE IF EXISTS `user_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_permission`
(
    `username`   varchar(20) NOT NULL,
    `permission` varchar(20) NOT NULL,
    PRIMARY KEY (`username`, `permission`),
    CONSTRAINT `user_permission_FK` FOREIGN KEY (`username`) REFERENCES `user` (`username`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_permission`
--

LOCK
TABLES `user_permission` WRITE;
/*!40000 ALTER TABLE `user_permission` DISABLE KEYS */;
INSERT INTO `user_permission`
VALUES ('031902410', 'student'),
       ('Jessie', 'admin'),
       ('student1', 'student'),
       ('student1', 'student_CIRD9F'),
       ('student1', 'teacher_CIRD9F'),
       ('student2', 'student'),
       ('student2', 'student_CIRD9F'),
       ('student3', 'student'),
       ('student3', 'student_25VEO4'),
       ('student3', 'student_CIRD9F'),
       ('teacher1', 'teacher'),
       ('teacher1', 'teacher_25VEO4'),
       ('teacher1', 'teacher_CIRD9F'),
       ('user1', 'stu_0');
/*!40000 ALTER TABLE `user_permission` ENABLE KEYS */;
UNLOCK
TABLES;

--
-- Table structure for table `user_wechat`
--

DROP TABLE IF EXISTS `user_wechat`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_wechat`
(
    `username` varchar(20)  NOT NULL,
    `openID`   varchar(100) NOT NULL COMMENT '微信文档没具体说明多长，听说默认28，但官方长度为128',
    `tags`     varchar(100) DEFAULT NULL,
    PRIMARY KEY (`openID`),
    KEY        `user_wechat_FK` (`username`),
    CONSTRAINT `user_wechat_FK` FOREIGN KEY (`username`) REFERENCES `user` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_wechat`
--

LOCK
TABLES `user_wechat` WRITE;
/*!40000 ALTER TABLE `user_wechat` DISABLE KEYS */;
INSERT INTO `user_wechat`
VALUES ('Jessie', 'oUqup56c23bBkqGBcCLOvbkneM80', 'admin');
/*!40000 ALTER TABLE `user_wechat` ENABLE KEYS */;
UNLOCK
TABLES;

--
-- Table structure for table `vote`
--

DROP TABLE IF EXISTS `vote`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vote`
(
    `vid`           int(10) unsigned NOT NULL AUTO_INCREMENT,
    `classID`       varchar(6)  NOT NULL,
    `title`         varchar(30) NOT NULL,
    `limitation`    int(11) NOT NULL,
    `publishedTime` datetime    NOT NULL,
    `anonymous`     tinyint(1) NOT NULL,
    `publisher`     varchar(20)          DEFAULT NULL,
    `deadLine`      datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`vid`),
    KEY             `vote_FK` (`classID`),
    CONSTRAINT `vote_FK` FOREIGN KEY (`classID`) REFERENCES `teaching_class` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `vote`
--

LOCK
TABLES `vote` WRITE;
/*!40000 ALTER TABLE `vote` DISABLE KEYS */;
INSERT INTO `vote`
VALUES (1, 'CIRD9F', '这是一个投票的标题（需要持久化）', 2, '2021-08-10 16:57:44', 0, 'teacher1', '2021-08-27 15:06:24'),
       (2, 'CIRD9F', '快来投票', 2, '2021-08-13 20:55:18', 0, 'teacher1', '2021-08-27 15:06:24'),
       (3, 'CIRD9F', '快来投票吧', 2, '2021-08-13 20:57:25', 0, 'teacher1', '2021-08-27 15:06:24'),
       (4, 'CIRD9F', '快来投票吧', 2, '2021-08-13 20:58:01', 0, 'teacher1', '2021-08-27 15:06:24'),
       (5, 'CIRD9F', '快来投票吧', 2, '2021-08-13 21:06:20', 0, 'teacher1', '2021-08-27 15:06:24'),
       (6, 'CIRD9F', '快来投票吧', 2, '2021-08-13 21:27:56', 0, 'teacher1', '2021-08-27 15:06:24'),
       (7, 'CIRD9F', '持久化投票1', 2, '2021-08-15 23:52:02', 0, 'teacher1', '2021-08-27 15:06:24'),
       (8, 'CIRD9F', '新的投票100', 2, '2021-08-26 22:04:45', 0, 'teacher1', '2021-08-27 15:06:24'),
       (9, 'CIRD9F', '持久化投票100', 2, '2021-08-26 22:05:31', 0, 'teacher1', '2021-08-27 15:06:24'),
       (10, 'CIRD9F', '用于删除的投票', 2, '2021-08-29 11:48:15', 0, 'teacher1', '2021-09-05 11:48:16'),
       (11, 'CIRD9F', '用于删除的投票', 2, '2021-08-29 11:51:07', 0, 'teacher1', '2021-09-05 11:51:10');
/*!40000 ALTER TABLE `vote` ENABLE KEYS */;
UNLOCK
TABLES;

--
-- Table structure for table `vote_voters`
--

DROP TABLE IF EXISTS `vote_voters`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vote_voters`
(
    `vid`    int(10) unsigned NOT NULL,
    `voters` json NOT NULL,
    PRIMARY KEY (`vid`),
    CONSTRAINT `Vote_voters_FK` FOREIGN KEY (`vid`) REFERENCES `vote` (`vid`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='持久化投票信息到数据库中';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `vote_voters`
--

LOCK
TABLES `vote_voters` WRITE;
/*!40000 ALTER TABLE `vote_voters` DISABLE KEYS */;
/*!40000 ALTER TABLE `vote_voters` ENABLE KEYS */;
UNLOCK
TABLES;

--
-- Dumping routines for database 'classaffairsmanage'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-09-03 23:09:42
