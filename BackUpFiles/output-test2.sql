-- Database Backup
-- Driver: SQLiteJDBC
-- Database: SQLite
-- Version: 3.7.2

DROP TABLE IF EXISTS `subject`;
CREATE TABLE `subject`(subjectId INT(6) NOT NULL,subjectName VARCHAR(40),classHour INT(5), PRIMARY KEY (subjectId));
INSERT INTO `subject` VALUES (1, 'e', NULL);
INSERT INTO `subject` VALUES (2, 'd', NULL);
INSERT INTO `subject` VALUES (3, 'c', NULL);
INSERT INTO `subject` VALUES (4, 't', NULL);
INSERT INTO `subject` VALUES (5, 'y', NULL);

DROP TABLE IF EXISTS `grade`;
CREATE TABLE `grade`(gradeId INT(6),gradeName VARCHAR(40), PRIMARY KEY (gradeId));
INSERT INTO `grade` VALUES (201, '201°');
INSERT INTO `grade` VALUES (202, '30°');
INSERT INTO `grade` VALUES (203, '203°');
INSERT INTO `grade` VALUES (204, '204°');
INSERT INTO `grade` VALUES (205, '205°');
INSERT INTO `grade` VALUES (206, '206°');
INSERT INTO `grade` VALUES (207, '207°');
INSERT INTO `grade` VALUES (208, '208°');
INSERT INTO `grade` VALUES (209, '209°');
INSERT INTO `grade` VALUES (210, '210°');
INSERT INTO `grade` VALUES (211, '30°');
INSERT INTO `grade` VALUES (212, '30°');
INSERT INTO `grade` VALUES (213, '30°');
INSERT INTO `grade` VALUES (1, 'w');
INSERT INTO `grade` VALUES (2, 's');

DROP TABLE IF EXISTS `center`;
CREATE TABLE `center`(centerId INT(6),centerName VARCHAR(40),centerCode VARCHAR(20),phone VARCHAR(50), PRIMARY KEY (centerId));
INSERT INTO `center` VALUES (1, 'aa', '101', '18255109670');
INSERT INTO `center` VALUES (2, 'bb', '102', '18255109671');
INSERT INTO `center` VALUES (3, 'cc', '550', '13755550000');
INSERT INTO `center` VALUES (4, 'dd', '104', '18255109673');
INSERT INTO `center` VALUES (5, 'ee', '105', '18255109674');
INSERT INTO `center` VALUES (6, 'ff', '106', '18255109675');
INSERT INTO `center` VALUES (7, 'rr', '107', '18255109676');
INSERT INTO `center` VALUES (8, 'aa', '108', '18255109677');
INSERT INTO `center` VALUES (9, 'gg', '109', '18255109678');
INSERT INTO `center` VALUES (10, '×ϺþÖÐÐÄ', '110', '18255109679');
DROP INDEX IF EXISTS centerIdIndex;
CREATE INDEX 'centerIdIndex' ON center (centerId);

DROP TABLE IF EXISTS `coach`;
CREATE TABLE `coach`(coachId INT(6),coaName VARCHAR(40),centerId INTEGER(20), PRIMARY KEY (coachId), FOREIGN KEY (centerId) REFERENCES center(centerId));
INSERT INTO `coach` VALUES (1, 'q', 1);
INSERT INTO `coach` VALUES (2, 'a', 2);
INSERT INTO `coach` VALUES (3, 'z', 3);

