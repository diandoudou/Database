# Database Backup Project

中文 | English

---

## 中文说明

本项目是一个基于 Java + SQLite JDBC 的数据库备份与恢复工具。

## 项目目标

- 读取 SQLite 数据库文件
- 自动导出数据库结构与数据为 SQL 脚本
- 通过 SQL 脚本重建数据库

## 已实现功能

1. **数据库连接与管理**
   - 连接 SQLite 数据库
   - 关闭连接并提交事务

2. **数据库元数据导出**
   - 导出数据库版本信息（作为 SQL 注释）
   - 导出表结构（字段、主键、外键）
   - 导出索引（先 DROP 再 CREATE）
   - 导出表数据（INSERT 语句）

3. **依赖顺序处理**
   - 根据外键关系做拓扑排序
   - 按正确顺序生成建表语句，降低外键冲突风险

4. **数据格式化处理**
   - 支持 `NULL`
   - 支持字符串转义（单引号）
   - 支持 BLOB（二进制）转十六进制 SQL 字面量

5. **SQL 回放执行**
   - 读取导出的 SQL 文件
   - 执行脚本以进行数据库重建

## 技术栈

- **语言**：Java
- **数据库**：SQLite
- **接口**：JDBC (`org.sqlite.JDBC`)
- **核心 API**：
  - `DatabaseMetaData`
  - `ResultSet` / `ResultSetMetaData`
  - `FileWriter` / `BufferedWriter` / `BufferedReader`

## 项目结构

- `SourceCode/`
  - `Main.java`：程序入口
  - `DbBasic.java`：数据库连接与基础操作
  - `DbUser.java`：备份/恢复核心逻辑
- `SourceDataBase/`：源数据库样例
- `BackUpFiles/`：备份数据库与导出的 SQL 文件
- `Functional description.docx`：功能说明文档

## 运行说明

1. 编译 Java 源码
2. 运行 `Main` 类
3. 输入待处理的 SQLite 数据库文件路径
4. 程序将导出 SQL 并执行恢复流程

## 说明

当前实现中，部分输出文件名与执行路径为固定值，可根据实际需求进一步参数化。

---

## English Description

This project is a database backup and restore tool built with Java and SQLite JDBC.

## Objectives

- Read a SQLite database file
- Export database schema and data into SQL scripts
- Rebuild a new database from the generated SQL scripts

## Implemented Features

1. **Database Connection & Management**
   - Connects to SQLite databases
   - Commits transactions and closes connections

2. **Database Metadata Export**
   - Exports database version info as SQL comments
   - Exports table schema (columns, primary keys, foreign keys)
   - Exports indexes (DROP first, then CREATE)
   - Exports table data as `INSERT` statements

3. **Dependency Ordering**
   - Performs topological sorting based on foreign key relationships
   - Generates table creation statements in dependency-safe order

4. **SQL Value Formatting**
   - Supports `NULL`
   - Escapes string values (single quotes)
   - Supports BLOB values as hexadecimal SQL literals

5. **SQL Replay Execution**
   - Reads exported SQL files
   - Executes SQL scripts to rebuild the database

## Tech Stack

- **Language**: Java
- **Database**: SQLite
- **Interface**: JDBC (`org.sqlite.JDBC`)
- **Core APIs**:
  - `DatabaseMetaData`
  - `ResultSet` / `ResultSetMetaData`
  - `FileWriter` / `BufferedWriter` / `BufferedReader`

## Project Structure

- `SourceCode/`
  - `Main.java`: Program entry point
  - `DbBasic.java`: Database connection and basic operations
  - `DbUser.java`: Core backup/restore logic
- `SourceDataBase/`: Source database samples
- `BackUpFiles/`: Backup databases and exported SQL files
- `Functional description.docx`: Functional requirement document

## How to Run

1. Compile Java source files
2. Run the `Main` class
3. Enter the SQLite database file path
4. The program exports SQL and runs the restore process

## Notes

Some output filenames and execution paths are currently hardcoded and can be parameterized for better portability.