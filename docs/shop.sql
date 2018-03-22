create database shop;
GRANT ALL PRIVILEGES ON shop.* TO shopadmin@localhost IDENTIFIED BY 'shop123' WITH GRANT OPTION;

create table alert(
    id int not null auto_increment primary key,
    title varchar(100),
    body text,
    publishedDate date);