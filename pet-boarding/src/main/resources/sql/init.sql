-- =============================================
-- 宠物寄养馆管理系统 - 数据库初始化脚本
-- 仅建表，演示数据由 DataInitializer 自动生成
-- =============================================

CREATE DATABASE IF NOT EXISTS pet_boarding
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE pet_boarding;

-- =============================================
-- 1. 系统用户表
-- =============================================
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
  password VARCHAR(255) NOT NULL COMMENT '密码(BCrypt加密)',
  real_name VARCHAR(50) COMMENT '真实姓名',
  phone VARCHAR(20) COMMENT '手机号',
  email VARCHAR(100) COMMENT '邮箱',
  avatar VARCHAR(500) COMMENT '头像URL',
  role VARCHAR(20) NOT NULL DEFAULT 'CUSTOMER' COMMENT '角色: ADMIN/STAFF/CUSTOMER',
  status TINYINT DEFAULT 1 COMMENT '状态: 0=禁用 1=启用',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- =============================================
-- 2. 宠物表
-- =============================================
DROP TABLE IF EXISTS pet;
CREATE TABLE pet (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(50) NOT NULL COMMENT '宠物名称',
  type VARCHAR(20) NOT NULL COMMENT '类型: CAT/DOG/OTHER',
  breed VARCHAR(50) COMMENT '品种',
  age INT COMMENT '年龄(月)',
  gender VARCHAR(10) COMMENT '性别: MALE/FEMALE',
  weight DECIMAL(5,2) COMMENT '体重(kg)',
  owner_id BIGINT NOT NULL COMMENT '主人ID',
  photo VARCHAR(500) COMMENT '照片URL',
  medical_history TEXT COMMENT '病史',
  notes TEXT COMMENT '备注',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_owner (owner_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宠物表';

-- =============================================
-- 3. 笼舍表
-- =============================================
DROP TABLE IF EXISTS cage;
CREATE TABLE cage (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  cage_no VARCHAR(20) NOT NULL UNIQUE COMMENT '笼舍编号',
  type VARCHAR(20) NOT NULL COMMENT '类型: STANDARD/DELUXE/VIP',
  size VARCHAR(50) COMMENT '尺寸描述',
  status VARCHAR(20) DEFAULT 'AVAILABLE' COMMENT '状态: AVAILABLE/OCCUPIED/MAINTENANCE',
  daily_price DECIMAL(10,2) NOT NULL COMMENT '每日价格',
  description TEXT COMMENT '描述',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_status (status),
  INDEX idx_type (type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='笼舍表';

-- =============================================
-- 4. 服务项目表
-- =============================================
DROP TABLE IF EXISTS service_item;
CREATE TABLE service_item (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL COMMENT '服务名称',
  description TEXT COMMENT '服务描述',
  price DECIMAL(10,2) NOT NULL COMMENT '价格',
  duration INT COMMENT '时长(分钟)',
  status TINYINT DEFAULT 1 COMMENT '状态: 0=下架 1=上架',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务项目表';

-- =============================================
-- 5. 寄养订单表
-- =============================================
DROP TABLE IF EXISTS boarding_order;
CREATE TABLE boarding_order (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  order_no VARCHAR(32) NOT NULL UNIQUE COMMENT '订单编号',
  pet_id BIGINT NOT NULL COMMENT '宠物ID',
  owner_id BIGINT NOT NULL COMMENT '主人ID',
  cage_id BIGINT COMMENT '笼舍ID',
  check_in_date DATE NOT NULL COMMENT '入住日期',
  check_out_date DATE NOT NULL COMMENT '退房日期',
  status VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态: PENDING/CONFIRMED/CHECKED_IN/COMPLETED/CANCELLED',
  total_price DECIMAL(10,2) DEFAULT 0 COMMENT '总价',
  deposit DECIMAL(10,2) DEFAULT 0 COMMENT '押金/定金',
  special_requirements TEXT COMMENT '特殊要求',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_owner (owner_id),
  INDEX idx_pet (pet_id),
  INDEX idx_status (status),
  INDEX idx_order_no (order_no),
  INDEX idx_cage (cage_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='寄养订单表';

-- =============================================
-- 6. 订单服务关联表
-- =============================================
DROP TABLE IF EXISTS order_service;
CREATE TABLE order_service (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  order_id BIGINT NOT NULL COMMENT '订单ID',
  service_id BIGINT NOT NULL COMMENT '服务ID',
  quantity INT DEFAULT 1 COMMENT '数量',
  price DECIMAL(10,2) NOT NULL COMMENT '单价',
  INDEX idx_order (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单服务关联表';

-- =============================================
-- 7. 每日记录表
-- =============================================
DROP TABLE IF EXISTS daily_record;
CREATE TABLE daily_record (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  order_id BIGINT NOT NULL COMMENT '订单ID',
  record_date DATE NOT NULL COMMENT '记录日期',
  feeding_status VARCHAR(20) COMMENT '喂食状态: FED/NOT_FED/SPECIAL',
  health_status VARCHAR(20) COMMENT '健康状态: GOOD/FAIR/POOR',
  mood VARCHAR(20) COMMENT '情绪: HAPPY/CALM/ANXIOUS/AGGRESSIVE',
  notes TEXT COMMENT '备注',
  photos VARCHAR(1000) COMMENT '照片(逗号分隔URL)',
  staff_id BIGINT COMMENT '记录员工ID',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_order_date (order_id, record_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='每日记录表';

-- =============================================
-- 8. 费用结算表
-- =============================================
DROP TABLE IF EXISTS billing;
CREATE TABLE billing (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  order_id BIGINT NOT NULL UNIQUE COMMENT '订单ID',
  total_amount DECIMAL(10,2) DEFAULT 0 COMMENT '总金额',
  deposit DECIMAL(10,2) DEFAULT 0 COMMENT '已付定金',
  additional_charges DECIMAL(10,2) DEFAULT 0 COMMENT '附加费用',
  final_amount DECIMAL(10,2) DEFAULT 0 COMMENT '最终金额',
  payment_status VARCHAR(20) DEFAULT 'UNPAID' COMMENT '支付状态: UNPAID/DEPOSIT_PAID/FULLY_PAID/REFUNDED',
  payment_method VARCHAR(20) COMMENT '支付方式: WECHAT/ALIPAY/CASH/CARD',
  payment_time DATETIME COMMENT '支付时间',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_order (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='费用结算表';

-- =============================================
-- 9. 操作日志表
-- =============================================
DROP TABLE IF EXISTS operation_log;
CREATE TABLE operation_log (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT COMMENT '操作用户ID',
  username VARCHAR(50) COMMENT '操作用户名',
  module VARCHAR(50) COMMENT '操作模块',
  operation VARCHAR(50) COMMENT '操作类型',
  description VARCHAR(500) COMMENT '操作描述',
  method VARCHAR(200) COMMENT '请求方法',
  params TEXT COMMENT '请求参数',
  ip VARCHAR(50) COMMENT 'IP地址',
  duration BIGINT COMMENT '耗时(ms)',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_user (user_id),
  INDEX idx_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- =============================================
-- 说明：演示数据（用户/笼舍/服务）由 DataInitializer 在应用首次启动时自动生成
-- 默认密码均为 123456
-- =============================================
