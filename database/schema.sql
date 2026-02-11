-- E-COMMERCE WAREHOUSE SYSTEM - DATABASE SCHEMA
-- Oracle Database 21c XE

DROP TABLE products CASCADE CONSTRAINTS;
DROP TABLE categories CASCADE CONSTRAINTS;
DROP TABLE users CASCADE CONSTRAINTS;

CREATE TABLE users (
   id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
   username VARCHAR2(50) NOT NULL UNIQUE,
   password VARCHAR2(100) NOT NULL,
   email VARCHAR2(100) NOT NULL UNIQUE,
   full_name VARCHAR2(100) NOT NULL,
   role VARCHAR2(20) NOT NULL CHECK (role IN ('MANAGER', 'PICKER', 'RECEIVER', 'CONTROLLER')),
   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE categories (
    id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR2(100) NOT NULL UNIQUE,
    description VARCHAR2(255),
    active NUMBER(1) DEFAULT 1 CHECK (active IN (0, 1))
);

CREATE TABLE products (
    id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    sku VARCHAR2(50) NOT NULL UNIQUE,
    name VARCHAR2(100) NOT NULL,
    description VARCHAR2(255),
    price NUMBER(10,2) NOT NULL CHECK (price >= 0),
    stock NUMBER DEFAULT 0 CHECK (stock >= 0),
    reserved_stock NUMBER DEFAULT 0 CHECK (reserved_stock >= 0),
    min_stock NUMBER DEFAULT 5 CHECK (min_stock >= 0),
    location VARCHAR2(20),
    status VARCHAR2(20) NOT NULL CHECK (status IN ('AVAILABLE', 'LOW_STOCK', 'OUT_OF_STOCK')),
    category_id NUMBER NOT NULL,
    last_update TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES categories(id)
);

CREATE INDEX idx_product_sku ON products(sku);
CREATE INDEX idx_product_category ON products(category_id);
CREATE INDEX idx_product_status ON products(status);
CREATE INDEX idx_user_username ON users(username);