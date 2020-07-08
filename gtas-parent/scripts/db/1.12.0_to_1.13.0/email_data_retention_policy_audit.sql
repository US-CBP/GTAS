create table email_data_retention_policy_audit
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
    email_id bigint unsigned not null,
    constraint FKa1mr5dteng12wy786x330hqe3
        foreign key (email_id) references email (id)
);

