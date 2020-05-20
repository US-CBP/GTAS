create table phone_data_retention_policy_audit
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
    phone_id bigint unsigned not null,
    constraint FKp4rtn2cxy4qf910imbgtcg692
        foreign key (phone_id) references phone (id)
);

