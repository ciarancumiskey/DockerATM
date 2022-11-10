create database docker_atm;
\c docker_atm;

create table customer(account_number varchar(10) NOT NULL,
    pin varchar(10),
    balance numeric(20, 2),
    overdraft numeric(20, 2),
    primary key(account_number));

insert into customer (account_number, pin, balance, overdraft)
    values ('123456789', '1234', 800.00, 200.00);

insert into customer (account_number, pin, balance, overdraft)
    values ('987654321', '4321', 1230.00, 150.00);