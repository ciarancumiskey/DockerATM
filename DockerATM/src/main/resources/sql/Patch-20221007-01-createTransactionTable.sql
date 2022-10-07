create table transaction(id bigserial primary key,
    transaction_amount numeric(20, 2),
    transaction_dt timestamp,
    message varchar(255),
    customer_id varchar(10));

alter table transaction ADD FOREIGN KEY (customer_id) REFERENCES customer(account_number);