-- Oracle 数据库初始化脚本

-- 创建用户表
DROP TABLE user;

CREATE TABLE user (
    id NUMBER(20) NOT NULL,
    username VARCHAR2(50) NOT NULL,
    password VARCHAR2(100) NOT NULL,
    email VARCHAR2(100),
    phone VARCHAR2(20),
    age NUMBER(3),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

-- 创建自增序列
DROP SEQUENCE user_seq;
CREATE SEQUENCE user_seq
    INCREMENT BY 1
    START WITH 1
    NOMAXVALUE
    NOCACHE;

-- 创建触发器实现自增
CREATE OR REPLACE TRIGGER user_trigger
BEFORE INSERT ON user
FOR EACH ROW
BEGIN
    SELECT user_seq.NEXTVAL INTO :NEW.id FROM dual;
END;
/

-- 创建唯一索引
CREATE UNIQUE INDEX uk_username ON user(username);

-- 创建普通索引
CREATE INDEX idx_email ON user(email);

-- 插入测试数据
INSERT INTO user (id, username, password, email, phone, age) VALUES (1, 'tom', '123456', 'tom@example.com', '13900139001', 26);
INSERT INTO user (id, username, password, email, phone, age) VALUES (2, 'jerry', '123456', 'jerry@example.com', '13900139002', 32);
INSERT INTO user (id, username, password, email, phone, age) VALUES (3, 'mike', '123456', 'mike@example.com', '13900139003', 29);
INSERT INTO user (id, username, password, email, phone, age) VALUES (4, 'jack', '123456', 'jack@example.com', '13900139004', 38);
INSERT INTO user (id, username, password, email, phone, age) VALUES (5, 'rose', '123456', 'rose@example.com', '13900139005', 24);

COMMIT;