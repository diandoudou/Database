-- Database Backup
-- Driver: SQLiteJDBC
-- Database: SQLite
-- Version: 3.7.2

DROP TABLE IF EXISTS `Course`;
CREATE TABLE `Course`(Cid INTEGER(20) NOT NULL,Cname TEXT(20),Tid INTEGER(20), PRIMARY KEY (Cid));
INSERT INTO `Course` VALUES (1, 'math', 2);
INSERT INTO `Course` VALUES (2, 'science', 1);
INSERT INTO `Course` VALUES (3, 'physic', 5);
INSERT INTO `Course` VALUES (4, 'english', 3);
INSERT INTO `Course` VALUES (5, 'alysice', 4);
INSERT INTO `Course` VALUES (6, 'math', 1);
INSERT INTO `Course` VALUES (7, 'network', 3);
INSERT INTO `Course` VALUES (8, 'computer', 4);
DROP INDEX IF EXISTS cidindex;
CREATE INDEX 'cidindex' ON Course (Cid);

DROP TABLE IF EXISTS `Student`;
CREATE TABLE `Student`(Sid INTEGER(20) NOT NULL,Sname TEXT(30) NOT NULL, PRIMARY KEY (Sid));
INSERT INTO `Student` VALUES (1, 'q');
INSERT INTO `Student` VALUES (2, 'a');
INSERT INTO `Student` VALUES (3, 'w');
INSERT INTO `Student` VALUES (4, 'z');
INSERT INTO `Student` VALUES (5, 'e');
INSERT INTO `Student` VALUES (6, 'd');
INSERT INTO `Student` VALUES (7, 'r');
INSERT INTO `Student` VALUES (8, 'c');
INSERT INTO `Student` VALUES (9, 's');

DROP TABLE IF EXISTS `SC`;
CREATE TABLE `SC`(Sid INTEGER(20) NOT NULL,Cid INTEGER(20) NOT NULL,Grade INTEGER(20), PRIMARY KEY (Sid, Cid), FOREIGN KEY (Cid) REFERENCES Course(Cid), FOREIGN KEY (Sid) REFERENCES Student(Sid));
INSERT INTO `SC` VALUES (1, 1, 90);
INSERT INTO `SC` VALUES (1, 2, 80);
INSERT INTO `SC` VALUES (2, 1, 60);
INSERT INTO `SC` VALUES (2, 2, 70);
INSERT INTO `SC` VALUES (3, 3, 88);
INSERT INTO `SC` VALUES (4, 5, 66);
INSERT INTO `SC` VALUES (5, 4, 100);

DROP TABLE IF EXISTS `Teacher`;
CREATE TABLE `Teacher`(Tid INTEGER(3),Tname TEXT(20), PRIMARY KEY (Tid));
INSERT INTO `Teacher` VALUES (1, 'aa');
INSERT INTO `Teacher` VALUES (2, 'bb');
INSERT INTO `Teacher` VALUES (3, 'cc');
INSERT INTO `Teacher` VALUES (4, 'dd');
INSERT INTO `Teacher` VALUES (5, 'ee');

