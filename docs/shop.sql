create database shop;
GRANT ALL PRIVILEGES ON shop.* TO shopadmin@localhost IDENTIFIED BY 'shop123' WITH GRANT OPTION;

create table alert (
    id int not null auto_increment primary key,
    title varchar(100),
    body text,
    publishedDate varchar(200));

create table item (
    id int not null auto_increment primary key,
    title varchar(100),
    body text,
    price int,
    imageNames text,
    publishedDate varchar(200)
);


--     private String userID;
--     private String userName;
--     private String userEmail;
--     private String userPassword;
--     private String createdAt;
create table member (
    id int not null auto_increment unique,
    userID varchar(50) not null primary key,
    userName varchar(50) not null,
    userEmail varchar(100) not null,
    userPassword varchar(50) not null,
    userPostAddress varchar(1000) not null,
    userPostCode varchar(100) not null,
    userDetailAddress varchar(1000) not null,
    createdAt varchar(200) not null
);

create table checkout (
    id int not null auto_increment primary key,
    imp_uid varchar(200) not null,
    merchant_uid varchar(200) not null,
    paid_amount varchar(20) not null,
    apply_num varchar(100) not null,
    buyer_email varchar(100) not null,
    buyer_name varchar(100) not null,
    buyer_tel varchar(100) not null,
    buyer_addr varchar(200) not null,
    buyer_postcode varchar(20) not null,
    buyer_request_message text not null,
    createdAt varchar(200) not null
);