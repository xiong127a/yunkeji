-- 创建用户表（包含角色，默认普通用户）
CREATE TABLE `user` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    query_price DECIMAL(10, 2) NOT NULL DEFAULT 500,
    balance DECIMAL(10, 2) NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 创建资产表
CREATE TABLE asset (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    file_path VARCHAR(255),
    file_name VARCHAR(100),
    file_size BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES `user`(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 创建不动产查询记录表（包含查询费用）
CREATE TABLE real_estate_query_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    id_card VARCHAR(18) NOT NULL,
    request_no VARCHAR(100),
    status VARCHAR(20) DEFAULT 'SUBMITTED',
    pay_mode VARCHAR(20) DEFAULT 'STORED_VALUE',
    pay_status VARCHAR(20) DEFAULT 'PAID',
    query_fee DECIMAL(10, 2) NOT NULL DEFAULT 0,
    result TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES `user`(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 创建不动产查询文件表
CREATE TABLE real_estate_file (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    query_record_id BIGINT NOT NULL,
    file_type VARCHAR(50) NOT NULL,
    file_name VARCHAR(100) NOT NULL,
    file_path VARCHAR(255) NOT NULL,
    file_size BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (query_record_id) REFERENCES real_estate_query_record(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 用户余额变动记录表（储值/扣费/退款等）
CREATE TABLE user_balance_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    change_amount DECIMAL(10, 2) NOT NULL,
    change_type VARCHAR(20) NOT NULL, -- RECHARGE / DEDUCT / REFUND
    related_record_id BIGINT NULL,
    remark VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES `user`(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 支付订单表（扫码直付）
CREATE TABLE pay_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_no VARCHAR(64) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    query_record_id BIGINT,
    amount DECIMAL(10, 2) NOT NULL,
    pay_channel VARCHAR(20) NOT NULL, -- WECHAT / ALIPAY
    status VARCHAR(20) NOT NULL,      -- UNPAID / PAID / CLOSED
    qr_content VARCHAR(512),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES `user`(id),
    FOREIGN KEY (query_record_id) REFERENCES real_estate_query_record(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 插入默认管理员账号（用户名：admin 密码：yunkeji@123）
INSERT INTO `user` (username, email, password, role, created_at, updated_at)
VALUES ('admin', 'admin@yunkeji.local', '585659e522f4cb81b9f87cd6efd51863', 'ADMIN', NOW(), NOW());