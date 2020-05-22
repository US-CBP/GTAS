create table credit_card_data_retention_policy_audit
(
    id bigint unsigned auto_increment
        primary key,
    created_at datetime null,
    created_by varchar(20) null,
    updated_at datetime null,
    updated_by varchar(20) null,
    description varchar(255) null,
    guuid binary(255) null,
    action varchar(255) null,
    credit_card_id bigint unsigned not null,
    constraint FKi7dq88xw1vxvb2s45k5u2xbe1
        foreign key (credit_card_id) references credit_card (id)
);

